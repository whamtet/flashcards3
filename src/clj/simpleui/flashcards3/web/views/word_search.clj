(ns simpleui.flashcards3.web.views.word-search
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.controllers.word-search :as word-search]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- parse-level [s]
  (if-let [[_ match] (re-find #"(\d)\." s)]
    (Long/parseLong match)
    1))

(defn- get-default [query-fn slideshow_id]
  (-> (slideshow/get-slideshow-name query-fn slideshow_id)
      parse-level
      (* 2)
      (+ 10)))

(defcomponent panel [req ^:long grid-size]
  (let [grid-size (or grid-size (get-default query-fn slideshow_id))
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
