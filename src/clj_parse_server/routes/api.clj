(ns clj-parse-server.routes.api
  (:require [clj-parse-server.db.core :refer [db]]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.query :as mq]
            [monger.joda-time]
            [clj-time.core :as time]
            [clj-time.format :as f]
            [compojure.core :refer [defroutes GET POST PUT]]
            [ring.mock.request :as mock]
            [cheshire.core :refer :all]
            [cheshire.generate :refer [add-encoder encode-str]])
  (:import [org.bson.types ObjectId]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (generate-string data)})

(add-encoder org.bson.types.ObjectId encode-str)
(add-encoder org.joda.time.DateTime
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (f/unparse (f/formatters :date-time) c))))

;; input: sku,-qty
;; output: {"sku" 1, "qty" -1}
(defn format-order [order]
  (if (empty? order)
    {}
    (apply merge (map (fn [key]
                        (if (= \- (first key))
                          (array-map (subs key 1) -1)
                          (array-map key 1)))
                      (clojure.string/split order #",")))))

;; input key: "product"
;; input val: {"id" "123", "__type" "Pointer"}
;; output:    {"product.id" "123", "product.__type" "Pointer"}
(defn format-one-query [key val]
  (reduce (fn [r [k v]]
            (assoc r (str key "." k) v))
          {} val))

(def where-key [:$lt :$lte :$gt :$gte :$ne :$in :$nin :$exists :$select :$dontSelect :$all :$regex])

(defn format-query [query]
  (reduce (fn [r [k v]]
            (if (and (map? v) (not (some (partial contains? v) where-key)))
              (merge r (format-one-query k v))
              (assoc r k v)))
          {} query))

(def DEFAULT_LIMIT 100)
(def MAX_LIMIT 100000)                  ; parse set it for 1000
(defn format-limit [limit]
  (let [limit (try
                (Integer. limit)
                (catch NumberFormatException e DEFAULT_LIMIT))]
    (if (<= limit 0)
      DEFAULT_LIMIT
      (min limit MAX_LIMIT))))

(defn find-objects [classes where order limit]
  (mq/with-collection db classes
    (mq/find (format-query where))
    (mq/fields {:_id 0})
    (mq/sort (format-order order))
    (mq/limit (format-limit limit))))

(defn find-classes [classes {:keys [where order limit] :as params}]
  {:results (find-objects classes where order limit)})

(defn post-classes [classes params]
  (let [oid (get params "objectId" (str (ObjectId.)))
        now (time/now)
        data (-> params
                 (dissoc :classes)
                 (assoc :updatedAt now))]
    (mc/upsert db classes {:objectId oid} {$set data, $setOnInsert {:createdAt now}})
    {:createdAt now, :objectId oid}))

(defn fetch-by-objectId [classes oid]
  (let [result (mc/find-one-as-map db classes {:objectId oid} {:_id 0})]
    (json-response result)))

(defn create-object! [classes params]
  (post-classes classes params))

(defn query-or-create-object! [classes {:keys [_method] :as request}]
  (let [request (dissoc request :_method)
        method (case _method
                 "GET" find-classes
                 create-object!)]
    (method classes request)))

(declare api-routes)

(defn batch [{:keys [params]}]
  (let [requests (:requests params)]
    (loop [response [] requests requests]
      (if (empty? requests)
        (json-response response)
        (let [{:keys [method path body]} (first requests)
              method (keyword (clojure.string/lower-case method))
              new-request (-> (mock/request method path body)
                              (assoc :params body)
                              (mock/content-type "application/json; charset=utf8"))
              resp (api-routes new-request)]
          (recur (conj response {:success (parse-string (:body resp))}) ; because (:body resp) is a generated-string by cheshire.core
                 (rest requests)))))))

(defroutes api-routes
  (GET  "/1/classes/:classes/:oid" {{classes :classes oid :oid} :params} (fetch-by-objectId classes oid))
  (GET  "/1/classes/:classes" m (println m) (json-response []))
  (POST "/1/classes/:classes" {{classes :classes :as params} :params} (json-response (query-or-create-object! classes params)))
  (POST "/1/batch" req (batch req)))
