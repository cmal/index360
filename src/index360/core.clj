(ns index360.core
  (:require [clj-http.client :as client]
            [mount.core :as mount]
            [index360.websvc :as websvc]
            [clojure.tools.cli :refer [parse-opts]]
            )
  (:use [index360.getapidata]
        ;; [index360.scheduler]
        [index360.global]
        )
  )

(defn load-global-config! []
  (swap! g-config merge (load-file "conf/config.clj")))


(defn action-360index [& options]
  ;; (start-nrepl-server (:repl-port @g-config))

  ;; (start-index360-schedule)
  )

(def cli-options
  [["-h" "--help"]
   ["-r" "--nrepl"]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (parse-opts args cli-options)]

    (load-global-config!)

    (case (first arguments)

      "360index" (action-360index options)

      "websvc" (mount/start #'websvc/websvc-app)

      )))
