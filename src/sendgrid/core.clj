(ns sendgrid.core
  (:require [clj-http.client :as client]))

;; SendGrid offers a Web API that allows customers to retrieve information about
;; their account such as statistics, bounces, spam reports, unsubscribes, and other information.
;; This API is not RESTful since for most calls both GET and POST HTTP verbs can be used
;; interchangeably, and other verbs are not supported.

;; Authentication
;; **********************************

(def creds (ref {}))

(defn auth! [user pass]
  (dosync
    (ref-set creds {:api_user user :api_pass pass})))

;; **********************************

(def sendgrid "https://sendgrid.com/api")

;; Requests
;; **********************************

(defn normalize-endpoint
  "Normalize an endpoint so that it always starts with a trailing slash"
  [endpoint]
  (if (.startsWith endpoint "/")
    endpoint
    (str "/" endpoint)))

(defn normalize-method [method]
  (if (keyword? method) (name method) method))

(defn url
  "Constructs a URL for a request"
  [endpoint method & {:keys [format] :or {format "json"}}]
  (let [args [(normalize-endpoint endpoint) (normalize-method method) format]
        resource (->> args
                      (interpose ".")
                      (apply str))]
    (str sendgrid resource)))

(defn build-request
  [url & [auth params]]
  (let [auth-params (or auth @creds)
        query-params (merge auth-params params)]
    { :method :get
      :query-params query-params
      :url url } ))

(defn <>
  "Performs a HTTP request to SendGrid API"
  ([url]
    (client/request (build-request url)))
  ([url auth]
    (client/request (build-request url auth))))

(defn >> [& args]
  (->> (apply <> args)
       :body))

;; **********************************

(defn profile
  "Get a SendGrid account profile"
  ([] (profile @creds))
  ([auth]
  (let [u (url "profile" :get)]
    (<> u auth))))

