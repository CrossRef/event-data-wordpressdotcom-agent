(defproject event-data-wordpressdotcom-agent "0.1.0-SNAPSHOT"
  :description "Event Data Reddit Agent"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.slf4j/slf4j-simple "1.7.21"]
                 [clj-time "0.12.0"]
                 [robert/bruce "0.8.0"]
                 [redis.clients/jedis "2.8.0"]
                 [yogthos/config "0.8"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.amazonaws/aws-java-sdk "1.11.6"]
                 [http-kit "2.1.18"]
                 [org.clojure/data.json "0.2.6"]

                 [crossref/baleen "0.1"]]
  :main ^:skip-aot event-data-wordpressdotcom-agent.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :prod {:resource-paths ["config/prod"]}
             :dev  {:resource-paths ["config/dev"]}})
