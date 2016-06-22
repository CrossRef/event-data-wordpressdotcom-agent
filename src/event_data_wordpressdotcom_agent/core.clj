(ns event-data-wordpressdotcom-agent.core
  (:require [clojure.tools.logging :as l])
  (:require [event-data-wordpressdotcom-agent.wordpressdotcom :as wordpressdotcom]
            [event-data-wordpressdotcom-agent.push :as push]
            [event-data-wordpressdotcom-agent.monitor :as monitor])
  (:require [baleen.context :as baleen]
            [baleen.time :as baleen-time]
            [baleen.stash :as baleen-stash]
            [baleen.monitor :as baleen-monitor])

  (:gen-class))

(def config
  #{})

(defn main-queue-domain [context]
  (l/info "Queue domains...")
  (wordpressdotcom/queue-domains context)
  (l/info "Done queueing domains."))

(defn main-process [context]
  (l/info "Process...")
  (baleen-monitor/register-heartbeat context "process")
  (wordpressdotcom/process context))

(defn main-push [context]
  (l/info "Push")
  (baleen-monitor/register-heartbeat context "push")
  (push/run context))

(defn main-monitor [context]
  (l/info "Monitor")
  (baleen-monitor/register-heartbeat context "monitor")
  (monitor/run context))

(defn main-unrecognised-action
  [command]
  (l/fatal "ERROR didn't recognise " command))

(defn run-daily
  "Run daily tasks. Stashing logs.
  This will run the last 30 days' worth of daily tasks (starting with day-before-yesterday) if they haven't been done.
  Don't do yesterday because the agent may be busy collecting data for yesterday at any given time."
  [context]
  (let [ymd-range (baleen-time/last-n-days-ymd 30 :day-before-yesterday)]
    (l/info "Checking " (count ymd-range) "past days")
    (doseq [date-str ymd-range]
      (l/info "Check " date-str)
      (baleen-stash/stash-jsonapi-redis-list context (str "wordpressdotcom-input-" date-str) (str "logs/" date-str "/input.json") "wordpressdotcom-input" false)
      (baleen-stash/stash-jsonapi-redis-list context (str "wordpressdotcom-matched-" date-str) (str "logs/" date-str "/matched.json") "wordpressdotcom-match"  false)
      (baleen-stash/stash-jsonapi-redis-list context (str "wordpressdotcom-unmatched-log-" date-str) (str "logs/" date-str "/unmatched.json") "wordpressdotcom-match"  false))))

(defn -main
  [& args]
  (let [context (baleen/->Context
                  "wordpressdotcom"
                  "Wordpress.com Event Data Agent"
                  config)

        command (first args)]

    (baleen/boot! context)

    (l/info "Command: " command)

    (condp = command
      "queue-domains" (main-queue-domain context)
      "daily" (run-daily context)
      "process" (main-process context)
      "push" (main-push context)
      "monitor" (main-monitor context)
      (main-unrecognised-action command))))
