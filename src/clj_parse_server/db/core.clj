(ns clj-parse-server.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              [environ.core :refer [env]]))

;; Tries to get the Mongo URI from the environment variable
(defonce db (let [uri (:database-url env)
                  {:keys [db]} (mg/connect-via-uri uri)]
              db))
