(ns simpleui.flashcards3.web.views.play-effect
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :refer [get-src] :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent ^:endpoint panel [req ^:edn randoms]
  (let [[[_ src] randoms] (slideshow/get-slideshow-shuffled query-fn slideshow_id randoms)]
    (if src
      [:div {:class "flex justify-center"
             :hx-post "panel"
             :hx-vals {:randoms (pr-str randoms)}}
       [:img {:src (get-src src)
              :style {:max-width "1000px"}}]]
      [:div.p-6.text-xl "Empty"])))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../../output.css"]
       :js ["../../../random.js"]
       :hyperscript? true}
      (-> req (assoc :query-fn query-fn) panel)))))
