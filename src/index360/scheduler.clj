(ns index360.scheduler
  (:require [cheshire.core :as cjson]
            [taoensso.timbre :as log]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as jobs]
            [clojurewerkz.quartzite.triggers :as tr]
            [clojurewerkz.quartzite.schedule.daily-interval :refer [schedule monday-through-friday starting-daily-at time-of-day ending-daily-at with-interval-in-minutes with-interval-in-hours with-interval-in-days with-misfire-handling-instruction-fire-and-proceed with-misfire-handling-instruction-do-nothing]]
            [clojurewerkz.quartzite.schedule.cron :as cron]

            )

  )


(jobs/defjob daily-import-data
  )

(defn start-index360-scheduler
  []
  (log/info "scheduler started")
  )
