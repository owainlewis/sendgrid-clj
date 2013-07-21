(ns sendgrid.core
  (:require [clj-http.client :as client]))

(def creds (ref {:user "" :pass ""}))

(defn auth! [user pass]
  (dosync
    (ref-set creds { :user user :pass pass })))

(def sendgrid "https://sendgrid.com/api")

(defn normalize-endpoint [endpoint]
  (if (.startsWith endpoint "/")
    endpoint
    (str "/" endpoint)))

(defn url [endpoint method & {:keys [format] :or {format "json"}}]
  (let [resource (->> [(normalize-endpoint endpoint) method format]
                      (interpose ".")
                      (apply str))]
    (str sendgrid resource)))

(defn build-request
  [method url & [auth params]]
  (let [auth-params (or auth @creds)
        query-params (merge auth-params params)]
    { :method method
      :query-params query-params
      :url url } ))

(defn > [method url]
  (client/request (build-request method url)))

