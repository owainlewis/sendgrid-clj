# Sendgrid

A Clojure library for SendGrid

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

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
