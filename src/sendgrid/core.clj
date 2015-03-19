(ns sendgrid.core
  (:require [clj-http.client :as client]))

;; SendGrid offers a Web API that allows customers to retrieve information about
;; their account such as statistics, bounces, spam reports, unsubscribes, and other information.
;; This API is not RESTful since for most calls both GET and POST HTTP verbs can be used
;; interchangeably, and other verbs are not supported.

;; API endpoints
;; **********************************

(def +endpoints+
  {:profile "profile.get"
   :stats "stats.get"
   :bounces "bounces.get"
   :spam "spamreports.get"
   :unsubscribes "unsubscribes.get"
   :advanced-stats "stats.getAdvanced"
   :send  "mail.send"})

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

(defn build-url
  "Constructs a full Sendgrid url from a partial endpoint"
  [endpoint]
  (if (clojure.string/blank? endpoint)
    (throw (Exception. "Endpoint blank"))
      (str sendgrid "/" endpoint ".json")))

(defn build-request
  [url & [auth params]]
  (let [auth-params (or auth @creds)
        query-params (merge auth-params params)
        attachment (:attachment params)
        base {:method :get
              :as :json
              :query-params query-params
              :url url}]
    (if attachment
      (assoc base
        :method :post
        :multipart [{:name "title" :content (:title attachment)}
                    {:name "Content/type" :content (:content-type attachment)}
                    {:name (str "files[" (.getName (:file attachment)) "]") :content (:file attachment)}])
      base)))

(defn normalize-auth [& [user pass]]
  {:api_user user :api_key pass})

(defmacro with-error-handling [& body]
  `(try ~@body
     (catch Exception e#
       (prn (.message e#)))))

(defn <>
  "Performs a HTTP request to SendGrid API"
  ([url]
    (client/request (build-request url)))
  ([url auth]
    (client/request (build-request url auth)))
  ([url auth params]
    (with-error-handling
      (client/request (build-request url auth params)))))

(defn >>
  "Same as <> but only returns the body of the response"
  [& args]
  (->> (apply <> args) :body))

(defmacro with-results
  [& body]
  `(into {} (do ~@body)))

;; **********************************

(defn profile
  "Get a SendGrid account profile"
  ([] (profile @creds))
  ([auth]
    (into {}
      (>> (build-url (:profile +endpoints+)) auth))))

;; Mail
;; **********************************

(defn assert-keys! [m keys]
  (if-let [check-result (map (partial contains? m) keys)]
    (when ((complement every?) true? check-result)
      (throw (Exception. "Required keys are missing")))))

(defn send-email
  ([message]
    (send-email @creds message))
  ([auth message]
    (assert-keys! message [:to :from :subject])
      (>> (build-url (:send +endpoints+)) auth message)))

(comment
  (send-email auth {
    :to "owain@owainlewis.com"
    :from "jack@twitter.com"
    :subject "Mail"
    :text "<h1>Hello world</h1>"}))

;; Stats

(defn stats
  "Fetch general account stats
   If no date range is specified it returns stats
   for the current day
   - days
   - start_date
   - end_date
   - aggregate"
  ([auth] (stats auth {}))
  ([auth params-hash]
     (let [url (build-url (:stats +endpoints+))]
       (into {}
         (>> url auth params-hash)))))

;; TODO needs ability to pass in the params

(defn bounces
  ([auth] (bounces auth {}))
  ([auth params]
    (>> (build-url
          (:bounces +endpoints+)) auth params)))

(defn spam-reports
  ([auth] (spam-reports auth {}))
  ([auth params]
    (>> (build-url
          (:spam +endpoints+)) auth params)))

