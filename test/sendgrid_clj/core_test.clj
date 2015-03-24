(ns sendgrid-clj.core-test
  (:use clojure.test
        sendgrid-clj.core))

(def not-nil? (complement nil?))

(defn- get-sg-username []
  (System/getenv "SENDGRID_USERNAME"))

(defn- get-sg-password []
  (System/getenv "SENDGRID_PASSWORD"))

(defn- get-sg-to []
  (System/getenv "SENDGRID_TO_ADDR"))

(defn- get-auth []
  {:api_user (get-sg-username)
   :api_key (get-sg-password)})

(deftest test-auth
  (testing "auth-from-environment sanity check"
    (is (not-nil? (get-sg-username)))
    (is (not-nil? (get-sg-password)))
    (is (not-nil? (get-sg-to)))
    ))

(deftest test-profile
  (testing "profile api call"
    (is (= (:username (profile (get-auth))) (get-sg-username)))))

(deftest test-send
  (testing "simple send"
    (is (= "success"
           (:message (send-email (get-auth)
                                 {:to (get-sg-to)
                                  :from "test@test.com"
                                  :subject "Mail"
                                  :text "<h1>Hello world</h1>"}))))))

(deftest test-stats
  (testing "stats"
    (let [output (stats (get-auth))]
      (is (< 0 (int (:requests output))))
      (is (< 0 (int (:delivered output)))))))

(deftest test-bounces
  (testing "bounces"
    (let [output (bounces (get-auth))]
      ; should probably force a bounce to see this become something else.
      (is (= 0 (count output))))))

(deftest test-spam-reports
  (testing "spam-reports"
    (let [output (spam-reports (get-auth))]
      ; probably best if this comes back empty.
      (is (= 0 (count output))))))
