(ns event-data-wordpressdotcom-agent.monitor
  "Expose a server to monitor status."
  (:require [baleen.monitor :as baleen-monitor]))


(defn run [context]
  (baleen-monitor/run context))
