(ns simpleui.flashcards3.web.views.word-search
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.word-search :as word-search]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent panel [req ^:long grid-size]
  (let [{:keys [grid words]} (word-search/ws-grid
                              query-fn
                              slideshow_id
                              (or grid-size 10))]
    [:div
     [:div {:class "grid grid-rows-10 grid-cols-10 border"}
      (for [row grid col row]
        [:div.p-4 col])]
     [:div.flex.mt-2
      (for [word words]
        [:div.mr-8 word])]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
