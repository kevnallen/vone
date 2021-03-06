(defproject vone "0.1.0-SNAPSHOT"
  :description "VersionOne Reporting"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0"]
                 [cheshire "5.2.0"]
                 [clj-http-lite "0.2.0"]
                 [slingshot "0.10.3"]
                 [clj-time "0.5.1"]
                 [org.clojure/data.csv "0.1.2"]]
  :plugins [[appengine-magic "0.5.0"]
            [lein-ring "0.8.6"]]
  :ring {:handler vone.server/handler}
  :main vone.server)

