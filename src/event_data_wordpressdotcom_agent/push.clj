(ns event-data-wordpressdotcom-agent.push
  "Push events to Lagotto."
  (:require [baleen.queue :as baleen-queue]
            [baleen.web :as baleen-web]
            [baleen.util :as baleen-util]
            [baleen.lagotto :as baleen-lagotto])
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as l]
            [clojure.set :refer [difference]])
  (:require [org.httpkit.client :as http]
            [clj-time.coerce :as clj-time-coerce]))



(defn process-f [context json-blob]
  (let [item-parsed (json/read-str json-blob)
        input (get item-parsed "input")
        events (get item-parsed "events")
        timestamp (clj-time-coerce/to-string (clj-time-coerce/from-long (* 1000 (Integer/parseInt (get input "epoch-time")))))

        title (get-in item-parsed ["input" "title"])
        author-name (when-let [author-name (get-in item-parsed ["input" "author"])]
                      (str "https://wordpressdotcom.com/u/" author-name))

        url (get-in item-parsed ["input" "link"])

          
        results (doall (map (fn [{doi "doi" event-id "event-id"}]
                                  (baleen-lagotto/send-deposit
                                    context
                                    :subj-title title
                                    :subj-url url
                                    :subj-work-type "post-blog"
                                    :subj-author author-name
                                    :obj-doi doi
                                    :action "add"
                                    :event-id event-id
                                    :date-str timestamp
                                    :source-id "wordpress"
                                    :relation-type "discusses")) events))]

      ; Return success.
      (every? true? results)))

(defn run
  "Run processing. Blocks forever."
  [context]
  (baleen-queue/process-queue context "matched" (partial process-f context) :keep-done true))

