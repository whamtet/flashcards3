(ns simpleui.flashcards3.web.middleware.auth
  (:require
    [ring.middleware.basic-authentication
     :refer [wrap-basic-authentication]]))

(defn authenticated? [name pass]
  (and (= name (System/getenv "SLIDESHOW_USER"))
       (= pass (System/getenv "SLIDESHOW_PASS"))))

(defn wrap-auth [handler]
  ;; always wrap for studentss etc
  (wrap-basic-authentication handler authenticated?))
