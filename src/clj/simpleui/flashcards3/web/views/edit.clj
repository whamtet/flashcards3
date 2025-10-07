(ns simpleui.flashcards3.web.views.edit
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent ^:endpoint panel [req ^:prompt slideshow-name command]
  (case command
    [:div.p-2
     slideshow_id]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["/output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
