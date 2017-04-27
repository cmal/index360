(defproject index360 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/clojurescript "1.9.229"]
   [org.clojure/core.async "0.3.441"]

   [org.clojure/tools.cli "0.3.5"]           ;; Command line
   [clojurewerkz/quartzite "2.0.0"]         ;; 定时任务
   [clj-http "3.4.1"]
   [clj-time "0.13.0"]
   [cheshire "5.7.0"]                       ;; JSON encode and decode

   [com.taoensso/timbre "4.8.0"]            ;; Profiling
   [com.taoensso/tufte "1.1.1"]
   [com.taoensso/encore "2.91.0"]
   [com.taoensso/sente "1.11.0"]            ;; for websocket

   [http-kit "2.2.0"]                       ;; for websocket

   [org.clojure/tools.nrepl "0.2.12"]
   #_[org.clojure/tools.logging "0.3.1"]      ;; for repl logging?

   [com.novemberain/monger "3.1.0"]         ;; Mongodb

   [ring "1.5.0"]                           ;; WEB HTTP framework
   [ring/ring-defaults        "0.2.1"]      ;; Includes `ring-anti-forgery`, etc.
   ;; [ring-anti-forgery      "1.0.0"]
   [ring/ring-json "0.4.0"]
   [ring-json-response "0.2.0"]

   [compojure "1.5.0"]                      ;; WEB Route framework
   [hiccup "1.0.5"] ; Optional, just for HTML

   [mount "0.1.11"]

   [com.cognitect/transit-clj  "0.8.290"]
   [com.cognitect/transit-cljs "0.8.239"]


   ]

  :plugins
  [[lein-pprint         "1.1.2"]
   [lein-ancient        "0.6.10"]
   [com.cemerick/austin "0.1.6"]
   [lein-cljsbuild      "1.1.4"]
   [cider/cider-nrepl   "0.12.0"] ; Optional, for use with Emacs
   ]

  :cljsbuild
  {:builds
   [{:id :cljs-client
     :source-paths ["src"]
     :compiler {:output-to "target/main.js"
                :optimizations :whitespace #_:advanced
                :pretty-print true}}]}

  )
