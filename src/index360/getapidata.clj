(ns index360.getapidata
  (:require [clj-http.client :as client]
            [cheshire.core :as cjson]
            [clojure.string :as str]
            [monger.core :as mg]
            [monger.credentials :as mcred]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            )
  )

(def joudou-key-cert
  ;; joudou keystore/trust-store
  {:keystore "/Users/yuzhao/jks.ks"
   :keystore-type "jks" ; default: jks
   :keystore-pass "89362556"
   :trust-store "/Users/yuzhao/jks.ks"
   :trust-store-type "jks"
   :trust-store-pass "89362556"})

(defn get-secucodes!
  []
  (client/get
   "https://www.joudou.com/stockinfogate/stockidlist"
   joudou-key-cert))



(def stockids
  (get (cjson/parse-string
        (:body (get-secucodes!)))
       "data"))

(defn get-stocknames!
  []
  (apply
   concat
   (map #(-> {:query-params {:secucodes (str/join "," %)}
              :content-type :json}
             (merge joudou-key-cert)
             ((fn [data]
                (client/post
                 "https://test.joudou.com/stockinfogate/dataapi/stocknames"
                 data)))
             :body
             cjson/parse-string)
        (partition-all 50 stockids))))

(def stocknames
  (get-stocknames!))

(def stocknames-without-space
  (map #(str/replace % #" " "") (get-stocknames!)))

(def g-instance (atom {}))

(defn get-mongo-connect []
  (or (get @g-instance :mongo-conn nil)
      (let [conn (mg/connect)]
        (swap! g-instance
               assoc :mongo-conn conn)
        conn)))

(def mongo-db
  (mg/get-db
   (get-mongo-connect)
   "index360"))

;; stockids

;; 指定时间
;; http://index.haosou.com/index/indexqueryhour?q=300498,300182&t=7


;; 指数概况
;; http://index.haosou.com/index/overviewJson?q=300182

(def infos (atom {}))

(defn get-infos-fn [lst]
  (let [mp
        {
         ;; :overview
         ;;    (get
         ;;     (cjson/parse-string
         ;;      (:body
         ;;       (client/get
         ;;        (str
         ;;         "http://index.haosou.com/index/overviewJson?q="
         ;;         (str/join "," lst)))))
         ;;     "data")
            :soIndex
            (get-in
             (cjson/parse-string
              (:body
               (client/get
                (str
                 "http://index.haosou.com/index/soIndexJson?q="
                 (str/join "," lst)))))
             ["data"])
            :soMedia
            (get-in
             (cjson/parse-string
              (:body
               (client/get
                (str
                 "http://index.haosou.com/index/soMediaJson?q="
                 (str/join "," lst)))))
             ["data"])}]
    #_(println (get (:soIndex mp) "index"))
    ;; (doseq [{:strs [query data]} (:overview mp)]
    ;;   (do (swap! infos
    ;;              assoc-in
    ;;              [(keyword query) :overview]
    ;;              data)
    ;;       (mc/update
    ;;        mongo-db "overview"
    ;;        {:query query}
    ;;        {$set {:data data}}
    ;;        {:upsert true})))
    (let [{:strs [index period]} (:soIndex mp)]
      (doseq [[k v] index]
        (do
          (swap! infos
                 assoc-in
                 [(keyword k) :soIndex]
                 {:index v :period period})
          (mc/update
           mongo-db "soIndex"
           {:query k}
           {$set {:index v :period period}}
           {:upsert true}))))
    (let [{:strs [media period]} (:soMedia mp)]
      (doseq [[k v] media]
        (do
          (swap! infos
                 assoc-in
                 [(keyword k) :soMedia]
                 {:media v :period period})
          (mc/update
           mongo-db "soMedia"
           {:query k}
           {$set {:media v :period period}}
           {:upsert true}))))))

(defn get-infos!
  []
  (let [infos-promise (promise)]
    (doseq [lst (drop 380 (partition-all 5 stocknames-without-space))]
      (do
        (get-infos-fn lst)
        (println "Querying " lst "...")
        (Thread/sleep (+ 2000 (* 1000 (Math/random)))))))
  (spit "360index.txt" @infos))

;; 搜索指数趋势
;; http://index.haosou.com/index/soIndexJson?q=300182
;; 媒体关注度
;; http://index.haosou.com/index/soMediaJson?q=300182


;; stocknames

;; 指数概况

;; 搜索指数趋势

;; 媒体关注度

