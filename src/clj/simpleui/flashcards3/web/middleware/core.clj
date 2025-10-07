(ns simpleui.flashcards3.web.middleware.core
  (:require
    [simpleui.flashcards3.env :as env :refer [prod?]]
    [ring.middleware.basic-authentication
     :refer [wrap-basic-authentication]]
    [ring.middleware.defaults :as defaults]
    [ring.middleware.session.cookie :as cookie]))

(defn authenticated? [name pass]
  (and (= name (System/getenv "SLIDESHOW_USER"))
       (= pass (System/getenv "SLIDESHOW_PASS"))))

(defn wrap-base
  [{:keys [metrics site-defaults-config cookie-secret] :as opts}]
  (let [cookie-store (cookie/cookie-store {:key (.getBytes ^String cookie-secret)})]
    (fn [handler]
      (cond-> ((:middleware env/defaults) handler opts)
              true (defaults/wrap-defaults
                     (assoc-in site-defaults-config [:session :store] cookie-store))
              prod? (wrap-basic-authentication authenticated?)
              ))))
