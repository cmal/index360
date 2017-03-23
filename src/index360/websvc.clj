(ns index360.websvc
  (:require [clj-http.client :as client]
            [cheshire.core :as cjson]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [clojure.string :as str]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :as params]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.multipart-params :as mp]

            [taoensso.timbre :as log]
            [mount.core :refer [defstate]]
            )
  (:use
   [ring.middleware.json]
   [ring.util.json-response]
   [ring.adapter.jetty]
   ;;   [selmer.parser]
   [ring.util.response]
   [compojure.core]
   [index360.global]
   [index360.mongo]
   ))

(defn api-test []
  (json-response "hello"))

(defn page-not-found []
  {"status" false
   "error" "page not found"})

(defroutes main-routes

  (GET "/test" [] (api-test))
  (GET "/so-media/:query" [] api-so-media)
  (GET "/so-index/:query" [] api-so-index)

  (route/not-found (page-not-found)))

(def svc-app
  (wrap-routes main-routes
               #(-> %
                    wrap-reload
                    keyword-params/wrap-keyword-params
                    cookies/wrap-cookies
                    params/wrap-params
                    mp/wrap-multipart-params)))

(defn start-svc [port]
  (log/info (format "Start websvc at port: %d" port))
  (run-jetty svc-app {:port port :join false :join? false}))


(defstate websvc-app
  :start (start-svc (:http-port @g-config))
  :stop (.stop websvc-app))
