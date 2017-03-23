(ns index360.mongo
  (:require [monger.core :as mg]
            [monger.credentials :as mcred]
            [monger.collection :as mc]
            [monger.operators :refer :all]


            [taoensso.timbre :as log])
  (:use [index360.global]
        [ring.util.json-response]))

(def g-mongo (atom nil))

(defn do-mongo-connect! ""
  []
  (let [mongo-conf (:mongo @g-config)
        conn (mg/connect {:host (:host mongo-conf)
                          :port (:port mongo-conf)})]
    (reset! g-mongo conn)))

(defn get-mongo-db ""
  []
  (mg/get-db
   (or @g-mongo (do-mongo-connect!))
   (:db (:mongo @g-config))))

(defn api-so-media ""
  [req]
  (let [query (get-in req [:params :query])
        res (mc/find-one-as-map (get-mongo-db) "soMedia" {:query query})]
    (json-response
     {:status true
      :data (:media res)
      :from (get-in req [:period :from])
      :to (get-in req [:period :to])})))

(defn api-so-index ""
  [req]
  (let [query (get-in req [:params :query])
        res (mc/find-one-as-map (get-mongo-db) "soIndex" {:query query})]
    (json-response
     {:status true
      :data (:index res)
      :from (get-in res [:period :from])
      :to (get-in res [:period :to])})))
