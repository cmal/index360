(ns index360.core
  (:require [clj-http.client :as client]
            )
  (:use [index360.scheduler]
        [index360.getapidata]
        )
  )

(defn load-global-config! []
  (swap! g-config merge (load-file "conf/config.clj")))

(defn action-360index [& options]
  (start-nrepl-server (:repl-port @g-config))

  (start-index360-schedule)
  )


(defn -main [& args]
  let [{:keys [options arguments errors summary]}
       (parse-opts args cli-options)]

  (load-global-config!)

  (case (first arguments)

    "360index" (action-360index options)

    ))
