(defproject index360 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]           ;; Command line
                 [clj-http "3.4.1"]
                 [clojurewerkz/quartzite "2.0.0"]         ;; 定时任务
                 [clj-time "0.13.0"]
                 [cheshire "5.7.0"]                       ;; JSON encode and decode

                 [com.taoensso/timbre "4.8.0"]            ;; Profiling
                 [com.taoensso/tufte "1.1.1"]

                 [http-kit "2.2.0"]                       ;; for websocket
                 [com.taoensso/sente "1.11.0"]            ;; for websocket

                 [org.clojure/tools.nrepl "0.2.12"]
                 #_[org.clojure/tools.logging "0.3.1"]      ;; for repl logging?

                 [com.novemberain/monger "3.1.0"]
                 [org.clojure/core.async "0.3.441"]
                 [ring "1.5.0"]                           ;; WEB HTTP framework
                 [ring/ring-json "0.4.0"]
                 [ring-json-response "0.2.0"]
                 [compojure "1.5.0"]                      ;; WEB Route framework
                 [mount "0.1.11"]
                 [com.novemberain/monger "3.0.2"]         ;; Mongodb
                 ]
  )
