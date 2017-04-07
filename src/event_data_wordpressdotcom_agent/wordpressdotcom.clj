(ns event-data-wordpressdotcom-agent.wordpressdotcom
  "Interfacing with Wordpress.com."  
  (:require [clojure.set :as set]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [config.core :refer [env]]
            [clj-time.core :as clj-time]
            [clj-time.format :as clj-time-format]
            [clj-time.coerce :as clj-time-coerce]
            [clj-http.client :as http-client]
            [robert.bruce :refer [try-try-again]])
  (:import [java.util UUID]
           [org.apache.commons.codec.digest DigestUtils]
           [org.apache.commons.lang3 StringEscapeUtils])
  (:gen-class))


(def date-format
  (:date-time-no-ms clj-time-format/formatters))

(def user-agent "CrossrefEventDataBot (eventdata@crossref.org)")

(def query-url-base "https://en.search.wordpress.com")

; The Wordpress API is very flaky and can paginate erratically. Never go past this many pages.
(def max-pages 20)

(defn stop-at-dupe
  "Take from items until we meet a duplicate pair. Wordpress.com API sometimes sends dulicate pages. Bail out if that happens."
  [items]
  (map first (take-while (partial apply not=) (partition-all 2 1 items))))

(defn fetch-pages
  "Retrieve Wordpress results as a lazy seq of parsed pages of {:url :results}."
  ([domain] (fetch-pages domain 1))
  ([domain page-number]
    ; If we're blocked after 20 tries (17 minute delay) then just give up the whole thing.
    (let [query-params {"f" "json"
                        "size" 20
                        "t" "post"
                        "q" (str \" domain \")
                        "page" page-number
                        "s" "date"}

          ; The API returns nil value to represent the end of results, but also sometimes at random during pagination.
          ; Because most queries result in nil, don't re-try every nil. Instead only do this when we're at least into page 2 of a result set.
          ; This is a compromise between retrying every single failed result and potentially missing out on some.
          ; This is just a quirk of the Wordpress.com API.
          result-parsed (try
                          (try-try-again {:tries 3 :decay :double :delay 1000}
                                         (fn [] (let [result (http-client/get query-url-base {:query-params query-params :headers {"User-Agent" user-agent}})
                                                      body (when-let [body (:body result)] (json/read-str body))]
                                                  (if body
                                                      body
                                                      (when (> page 1)
                                                        (throw (new Exception)))))))
                          (catch Exception ex nil))

          url (str query-url-base "?" (http-client/generate-query-string query-params))]
      (log/info "Retrieved" url)

      ; The API just returns nil when there's no more data (or we got another failure).
      (if (nil? result-parsed)
        nil
        (lazy-cat [{:url url :results result-parsed}]
                  (fetch-pages domain (inc page-number)))))))

(defn parse-item
  "Parse a page item into an Action."
  [item]
  (let [url (get item "link")
        title (get item "title")
        date (-> item
               (get "epoch_time")
               Long/parseLong
               (* 1000)
               clj-time-coerce/from-long)

        date-str (clj-time-format/unparse date-format date)

        ; Use the URL of the blog post as the action identifier.
        ; This means that the same blog post in different feeds (or even sources) will have the same ID.
        action-id (DigestUtils/sha1Hex ^String url)]
    {:id action-id
     :url url
     :relation-type-id "discusses"
     :occurred-at date-str
     :observations [{:type :content-url :input-url url :sensitive true}]
     :subj {
      :type "post-weblog"
      ; We find occasional HTML character entities.
      :title (StringEscapeUtils/unescapeHtml4 title)}}))

(defn parse-page
  [page]
  {:url (:url page) :actions (map parse-item (:results page))})

(defn take-pages-after
  [date pages]
  "Accept a seq of pages of Actions. Take pages until we get a page on which every entry occurs on or before the date."
  (take-while (fn [page]
                (some #(->> % :occurred-at (clj-time-format/parse date-format) (clj-time/before? date)) (:actions page)))
              pages))

(defn fetch-parsed-pages-after
  "Return a seq of Percolator pages from the API that concern the given domain. As many pages as have events that occurred after the date."
  [date domain]
  (take-pages-after date (map parse-page (->> domain fetch-pages stop-at-dupe (take max-pages)))))
