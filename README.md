# Sendgrid

A Clojure library for SendGrid

[![Clojars Project](http://clojars.org/sendgrid-clj/latest-version.svg)](http://clojars.org/sendgrid-clj)

## Installation

Add the following to your dependencies to install sendgrid from [Clojars](https://clojars.org/sendgrid).

```clojure
[sendgrid "0.1.0"]
```

## Use

All functions require authorization which is just a map with the following keys

```clojure
(def auth {:api_user "blah" :api_key "blah"})
```

```clojure
(profile auth)

;; {:country "GB", :last_name "Lewis", :state "", :website_access "true",
;;  :address2 "", :city "Cardiff", :username "owainlewis", :phone "",
;;  :email "owain@owainlewis.com", :active "true", :first_name "Owain",
;;  :zip "", :address "",
;;  :website "http://owainlewis.com"}
```

## Mail

To send an email message via SendGrid. Note that certain keys are required

```clojure
(send-email auth {
  :to "owain@owainlewis.com"
  :from "jack@twitter.com"
  :subject "Mail"
  :text "<h1>Hello world</h1>"})

;; {:message "success"}
```
## Sending an attachment

If you'd like to send an attachment with your email, pass an attachment map as a param:

```clojure
(send-email auth {
  :to "owain@owainlewis.com"
  :from "jack@twitter.com"
  :subject "Mail"
  :text "<h1>Hello world</h1>"
  :attachment {:title "huzzah!"
               :content-type "text/csv"
               :file (io/file "huzzah.csv")}})
```


## Stats

```clojure
(stats auth)

;; With extra params

(stats auth {:start_date "" :end_date ""})

```

## Testing

Export SENDGRID_PASSWORD and SENDGRID_USERNAME in your shell, then run the tests.
```shell
> export SENDGRID_USERNAME=app123XYZ@heroku.com
> export SENDGRID_PASSWORD=xxyyzz2244
> export SENDGRID_TO_ADDR=you@wherever.com
> lein test
<... testing output ...>

```





## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
