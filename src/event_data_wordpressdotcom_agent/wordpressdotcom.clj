(ns event-data-wordpressdotcom-agent.wordpressdotcom
  "Interfacing with Wordpress.com."
  
  (:require [baleen.queue :as baleen-queue]
            [baleen.time :as baleen-time]
            [baleen.context :as baleen-context]
            [baleen.util :as baleen-util]
            [baleen.web :as baleen-web]
            [baleen.stash :as baleen-stash]
            [baleen.reverse :as baleen-reverse])

  (:require [clojure.set :as set]
            [clojure.tools.logging :as l]
            [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:require [config.core :refer [env]])
  (:require [clj-time.core :as clj-time]
            [clj-time.format :as clj-time-format]
            [clj-time.coerce :as clj-time-coerce])
  (:require [org.httpkit.client :as http-client])
  (:import [java.util UUID])
  (:import [com.amazonaws.services.s3 AmazonS3 AmazonS3Client]
           [com.amazonaws.auth BasicAWSCredentials]
           [com.amazonaws.services.s3.model GetObjectRequest PutObjectRequest])
  (:require [robert.bruce :refer [try-try-again]])
  (:gen-class))

; ; wordpressdotcom includes subdomains
(def hardcoded-domains ["doi.org"])

(defn parse-page
  "Parse response JSON. Return
  {:after-token ; can be nil
   [{:link, :title, :epoch-time, :date}]}"
  [json-data]
  (let [parsed (json/read-str json-data)]
    (map (fn [child]
                  { :link (get child "link")
                    :title (get child "title")
                    :epoch-time (get child "epoch_time")}) parsed)))
                    

(defn fetch-page
  [domain page-number]
  (l/info "Fetch domain:" domain "page" page-number)
  (let [result @(http-client/get "https://en.search.wordpress.com" {:query-params {"f" "json" "size" 20 "t" "post" "q" domain "page" page-number "s" "date"}
                                                                    :headers {"User-Agent" "Crossref Event Data eventdata.crossref.org (labs@crossref.org)"}})]
    (l/info "Response" (:headers result))
    (parse-page (:body result))))

(defn fetch-data-for-domain
  "Return a seq of all the parsed results that match the start and end date."
  [domain start-date end-date]
  ; Iterate all pages.
  (loop [results (list)
         page-number 1]
          ; If we're blocked after 20 tries (17 minute delay) then just give up the whole thing.
    (let [result (try-try-again {:tries 10 :decay :double :delay 10000} #(fetch-page domain page-number))
          filtered (filter (fn [item]
                            (let [date (clj-time-coerce/from-long (* 1000 (Long/parseLong (:epoch-time item))))]
                              (and (clj-time/after? date start-date)
                                   (clj-time/before? date end-date)))) result)

          dates (map #(clj-time-coerce/from-long (* 1000 (Long/parseLong (:epoch-time %)))) result)
          dates-before-start (filter #(clj-time/before? % start-date) dates)
          ; don't continue if we get an empty result.
          ; don't continue if some of the dates are before the earliest date we're interested in.
          continue (and (not-empty result)
                        (empty? dates-before-start))

          results (concat results filtered)]

      (if continue
        (recur results (inc page-number))
        results))))

(defn yesterday-bounds
  "Return a vector of [start-of-yesterday, end-of-yesterday]"
  []
  (let [today (clj-time/now)
        yesterday (clj-time/minus today (clj-time/days 1))]
    
        [(clj-time/date-midnight (clj-time/year yesterday) (clj-time/month yesterday) (clj-time/day yesterday))
         (clj-time/date-midnight (clj-time/year today) (clj-time/month today) (clj-time/day today))]))

(defn process-f
  [context json-blob]
  (let [parsed (json/read-str json-blob)
        {domain "domain" start-date-str "start-date" end-date-str "end-date"} parsed
        start-date (clj-time-coerce/from-string start-date-str)
        end-date (clj-time-coerce/from-string end-date-str)
        new-items (fetch-data-for-domain domain start-date end-date)]

    ; Now visit every page!
    (doseq [item new-items]
      (l/info "Look for DOIs in" (:link item))
      (let [response (try-try-again {:tries 4 :decay :double :delay 10000} #(deref (http-client/get (:link item) {:headers {"User-Agent" "Crossref Event Data eventdata.crossref.org (labs@crossref.org)"}})))
            body (:body response)
            dois (when body (baleen-web/extract-dois-from-body-via-landing-page-urls context body))
            events (map (fn [doi]
              {:doi doi
               :event-id (baleen-util/new-uuid)}) dois)]
            (if-not (empty? events)
              (baleen-queue/enqueue context "matched" (json/write-str {:input item :events events}) true)
              (baleen-queue/enqueue context "unmatched" (json/write-str {:input item :events []}) true))))

    (l/info "Done domain" domain ". Having a sleep."))

  ; Don't overload API
  (Thread/sleep 5000)
  true)

(defn process
  "Process domains from queue. Runs forever."
  [context]
  (l/info "Start processing domains.")
  (baleen-queue/process-queue context "input" (partial process-f context)))

(defn queue-domains
  "Queue all domains for yesterday."
  [context]
  (let [; Start and end of yesterday.
        [start-date end-date] (yesterday-bounds)

        _ (l/info "Check Wordpress.com for " start-date end-date)

        ; Fetch a fresh set of domains.
        domains (baleen-reverse/fetch-domains context)
        all-domains (concat domains hardcoded-domains)

        ; Transform into a JSONable object for storing.
        domains-to-save (map #(hash-map "domain" %) all-domains)]
    (l/info "Queue up domains...")
    (doseq [domain all-domains]
      (baleen-queue/enqueue
        context
        "input"
        (json/write-str {:domain domain :start-date (clj-time-coerce/to-string start-date) :end-date (clj-time-coerce/to-string end-date)})
        true))
    (l/info "Finished queuing up domains.")

    (l/info "Stash rules")
    (baleen-stash/stash-jsonapi-list context domains-to-save "filter-rules/current.json" "domain" true)
    (baleen-stash/stash-jsonapi-list context domains-to-save (str "filter-rules/" (baleen-time/format-ymd start-date) ".json") "domain" false)
    (l/info "Finished stashing rules.")))

