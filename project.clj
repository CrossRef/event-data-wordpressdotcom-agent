(defproject event-data-wordpressdotcom-agent "0.2.2"
  :description "Event Data Wordpress.com Agent"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.crossref.event-data-agent-framework "0.2.0"]
                 [event-data-common "0.1.30"]
                 [commons-codec/commons-codec "1.10"]
                 [robert/bruce "0.8.0"]
                 [clj-http "2.3.0"]
                 [org.slf4j/slf4j-simple "1.7.21"]
                 [clj-time "0.12.0"]
                 [yogthos/config "0.8"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.apache.commons/commons-lang3 "3.5"]
                 [throttler "1.0.0"]]
  :main ^:skip-aot event-data-wordpressdotcom-agent.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :prod {:resource-paths ["config/prod"]}
             :dev  {:resource-paths ["config/dev"]}})
