(ns simpleui.flashcards3.web.views.word-search
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.word-search :as word-search]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent panel [req ^:long grid-size]
  (let [grid-size (or grid-size 12)
        {:keys [grid words]} (word-search/ws-grid
                              query-fn
                              slideshow_id
                              grid-size)]
    [:div
     [:div {:class "grid border"
            :style {:grid-template-rows (format "repeat(%s, minmax(0, 1fr))" grid-size)
                    :grid-template-columns (format "repeat(%s, minmax(0, 1fr))" grid-size)}}
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
