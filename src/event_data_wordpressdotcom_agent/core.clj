(ns event-data-wordpressdotcom-agent.core
  (:require [clojure.tools.logging :as log]
            [clj-time.core :as clj-time]
            [clj-time.format :as clj-time-format]
            [event-data-common.status :as status]
            [event-data-wordpressdotcom-agent.wordpressdotcom :as wordpressdotcom]
            [org.crossref.event-data-agent-framework.core :as c]
            [config.core :refer [env]])
  (:gen-class))

(def source-token "7750d578-d74d-4e92-9348-cd443cbb7afa")
(def license "https://creativecommons.org/publicdomain/zero/1.0/")
(def version (System/getProperty "event-data-wordpressdotcom-agent.version"))

(def date-format
  (:date-time-no-ms clj-time-format/formatters))

(defn check-all-domains
  "Check all domains for unseen links."
  [artifacts callback]
  (log/info "Start crawl all Domains on Wordpress.com at" (str (clj-time/now)))
  (status/send! "wordpressdotcom-agent" "process" "scan-domains" 1)
  (let [[domain-list-url domain-list] (get artifacts "domain-list")
        domains (clojure.string/split domain-list #"\n")
        ; Take 48 hours worth of pages to make sure we cover everything. The Percolator will dedupe.
        num-domains (count domains)
        counter (atom 0)
        cutoff-date (-> 48 clj-time/hours clj-time/ago)]
    (doseq [domain domains]
      (swap! counter inc)
      (log/info "Query for domain:" domain @counter "/" num-domains " = " (int (* 100 (/ @counter num-domains))) "%")
      (let [pages (wordpressdotcom/fetch-parsed-pages-after cutoff-date domain)
            package {:source-token source-token
                     :source-id "wordpressdotcom"
                     :license license
                     :agent {:version version :artifacts {:domain-set-artifact-version domain-list-url}}
                     :extra {:cutoff-date (clj-time-format/unparse date-format cutoff-date) :queried-domain domain}
                     :pages pages}]
        (log/info "Sending package...")
        (callback package)
        (log/info "Sent package."))))
  (log/info "Finished scan."))

(def agent-definition
  {:agent-name "wordpressdotcom-agent"
   :version version
   :jwt (:wordpressdotcom-jwt env)
   :schedule [{:name "check-all-domains"
              :seconds 86400 ; wait 24 hours between scans
              :fixed-delay true
              :fun check-all-domains
              :required-artifacts ["domain-list"]}]
   :runners []})

(defn -main [& args]
  (c/run agent-definition))

