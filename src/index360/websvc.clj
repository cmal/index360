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

            [taoensso.sente  :as sente]
            [taoensso.sente.server-adapters.http-kit      :refer (get-sch-adapter)]

            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]

            [mount.core :as mount]
            )
  (:use
   [org.httpkit.server]
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

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (http/get-sch-adapter) {})]

  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(defroutes main-routes

  (GET "/test" [] (api-test))
  (GET "/so-media/:query" [] api-so-media)
  (GET "/so-index/:query" [] api-so-index)

  ;; websocket channel
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req))

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


(mount/defstate websvc-app
  :start (start-svc (:http-port @g-config))
  :stop (.stop websvc-app))

(mount/start #'websvc-app)


(chsk-send! ; Using Sente
  [:request-id {:name "Rich Hickey" :type "Awesome"}] ; Event
  8000 ; Timeout
  ;; Optional callback:
  (fn [reply] ; Reply is arbitrary Clojure data
    (if (sente/cb-success? reply) ; Checks for :chsk/closed, :chsk/timeout, :chsk/error
      (println reply)
      (println "error"))))



(defonce chan (atom nil))

(defonce clients (atom {}))

;; http-ket websocket
(defn handler
  [request]
  (log/info request)
  (with-channel request channel
    (reset! clients assoc channel true)
    (on-close channel (fn [status]
                        (println "channel closed: " status)))
    #_(on-receive channel (fn [data] ;; echo it back
                          (println "received message: " data)
                          (send! channel data)))))

;; send location information, id x y
;; id : 1~10
;; x,y : 0~640, 0~480
(defn send-loc!
  [id x y]
  (future (loop []
            (doseq [client @clients]
              (send! (key client) (generate-string [id x y]))))))

(defonce locs (atom {}))

(defn rand-init-loc
  []
  (doseq [id (range 10)]
    (swap! locs
           assoc id [(rand-int 640) (rand-int 480)])))

(defn rand-loc-run
  []
  (loop []
    (Thread/sleep 100)
    (let [id (rand-int 10)
          x (min 640 (+ (rand-int 10) (get-in @locs id 0)))
          y (min 480 (+ (rand-int 10) (get-in @locs id 1)))]
      (swap! locs
             assoc id [x y])
      (send-loc! id x y))))

(log/merge-config!
   {:timestamp-opts {:timezone (java.util.TimeZone/getTimeZone "Asia/Shanghai")}
    :appenders {:spit (appenders/spit-appender
                       {:fname "index360.log"})}})

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and http://http-kit.org/migration.html#reload
  (reset! server (run-server #'handler {:port 9090})))

;; (stop-server) (start-server)
;; and resources/ws.html onMessage do onSend
;; always loop, never end
;; This is a working version.
