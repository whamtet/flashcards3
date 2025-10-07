(ns simpleui.flashcards3.web.htmx
  (:require
   [hiccup.core :as h]
   [hiccup.page :as p]
   [ring.util.http-response :as http-response]
   [simpleui.core :as simpleui]
   [simpleui.flashcards3.env :refer [dev?]]
   [simpleui.flashcards3.web.resource-cache :as resource-cache]
   [simpleui.render :as render]))

(defn page [opts & content]
  (-> (p/html5 opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn ui [opts & content]
  (-> (h/html opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn- unminify [^String s]
  (if dev?
    (.replace s ".min" "")
    s))

(defn- scripts [js hyperscript? stripe?]
  (cond-> js
          hyperscript? (conj (unminify "https://unpkg.com/hyperscript.org@0.9.12/dist/_hyperscript.min.js"))
          stripe? (conj (resource-cache/cache-suffix "/checkout.js")
                        "https://js.stripe.com/v3/")))

(defn page-htmx [{:keys [css js hyperscript? stripe?]} & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "SimpleUI Flashcards"]
    [:link {:rel "icon" :href "/logo_dark.svg"}]
    (when stripe?
          [:link {:rel "stylesheet" :href "/checkout.css"}])
    (for [sheet css]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body
    (render/walk-attrs body)
    [:script {:src
              (unminify "https://unpkg.com/htmx.org@1.9.5/dist/htmx.min.js")}]
    [:script "htmx.config.defaultSwapStyle = 'outerHTML';"]
    (map
     (fn [src]
       [:script {:src src}])
     (scripts js hyperscript? stripe?))]))

(defn page-simple [{:keys [css]} & body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    (for [sheet css]
      [:link {:rel "stylesheet" :href (resource-cache/cache-suffix sheet)}])]
   [:body (render/walk-attrs body)]))

(def optionals
  '{slideshow_id [slideshow_id (Long/parseLong (:slideshow_id params))]
    step [step (Long/parseLong (:step params))]})

(defmacro defcomponent
  [name [req :as args] & body]
  (if-let [sym (simpleui/symbol-or-as req)]
    `(simpleui/defcomponent ~name ~args
      (let [{:keys [~'path-params ~'query-fn]} ~sym
            ~'params (merge ~'params ~'path-params)
            ~@(->> body flatten (mapcat optionals) distinct)]
        ~@body))
    (throw (Exception. "req ill defined"))))
