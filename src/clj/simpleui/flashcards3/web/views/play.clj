(ns simpleui.flashcards3.web.views.play
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent panel [req]
  (let [slides (slideshow/get-slideshow-slides query-fn slideshow_id)
        last? (-> slides count dec (= step))
        href (if (or (empty? slides) last?)
               "../../.."
               (format "../../../play/%s/%s/" slideshow_id (inc step)))]
    [:a {:href href}
     (when (-> slides count (> 1))
       [:script (format "addListener(%s, %s)" (count slides) step)])
     (if (empty? slides)
       [:div.p-6.text-xl "Empty"]
       [:img {:src (-> step slides second)}])]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../../output.css"]
       :js ["../../../random.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
