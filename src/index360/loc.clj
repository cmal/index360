(ns index360.loc
  (:require
   [index360.ws :as ws :refer [connected-uids chsk-send!]]))

;; send location information, id x y
;; id : 1~10
;; x,y : 0~640, 0~480
(defn send-loc!
  [id x y]
  (future
    (doseq [uid (:any @connected-uids)]
      (chsk-send! uid [:node/loc (str [id x y])]))))

(defonce locs (atom {}))

(def node-cnt 100)

(defn rand-init-loc
  []
  (doseq [id (range node-cnt)]
    (swap! locs
           assoc id [(rand-int 640) (rand-int 480)])))

(defn rand-loc-run
  [num]
  (let [step 20]
    (doseq [i (range num)]
      (Thread/sleep 5)
      (let [id (rand-int node-cnt)
            x (max 20 (min 620 (+ (- (/ step 2.)) (rand-int step) (or (get-in @locs [id 0]) (rand-int 640)))))
            y (max 20 (min 460 (+ (- (/ step 2.)) (rand-int step) (or (get-in @locs [id 1]) (rand-int 480)))))]
        (swap! locs
               assoc id [x y])
        (send-loc! id x y)))))


