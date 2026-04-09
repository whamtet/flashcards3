(ns simpleui.flashcards3.web.views.word-search
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.word-search :as word-search]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

[:div.grid.grid-rows-10.grid-cols-10]
[:div.border.grid-rows-11.grid-cols-11]
[:div.grid-rows-12.grid-cols-12]
[:div.grid-rows-13.grid-cols-13]
[:div.grid-rows-14.grid-cols-14]
[:div.grid-rows-15.grid-cols-15]
[:div.grid-rows-16.grid-cols-16]
[:div.grid-rows-17.grid-cols-17]
[:div.grid-rows-18.grid-cols-18]
[:div.grid-rows-19.grid-cols-19]
[:div.grid-rows-20.grid-cols-20]
(defcomponent panel [req ^:long grid-size]
  (let [{:keys [grid words]} (word-search/ws-grid
                              query-fn
                              slideshow_id
                              (or grid-size 10))]
    [:div
     [:div {:class (format "grid grid-rows-%s grid-cols-%s border" grid-size grid-size)}
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
