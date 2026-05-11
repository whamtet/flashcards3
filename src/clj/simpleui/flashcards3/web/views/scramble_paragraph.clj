(ns simpleui.flashcards3.web.views.scramble-paragraph
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.env :refer [prod?]]
      [simpleui.flashcards3.web.controllers.reading :as reading]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- get-paragraphs [query-fn reading_id]
  (let [p (reading/get-paragraphs query-fn reading_id)
        n (-> p count (* 0.5) long)]
    [(take n p)
     (drop n p)]))

[:div.grid.grid-cols-1]
[:div.grid-cols-2]
[:div.grid-cols-3]
[:div.grid-cols-4]
[:div.grid-cols-5]
[:div.grid-cols-6]

(defn- paragraph [x]
  [:div.flex.items-center.justify-center.p-4.border
   [:p.fit.text-center.leading-tight
    x]])
(defn- row [items]
  [:div {:class (format "grid grid-cols-%s" (max (count items) 1))
         :style {:height "50vh"}}
   (map paragraph items)])

(defcomponent panel [req]
  (let [[a b] (get-paragraphs query-fn reading_id)]
    [:div.h-screen.flex.flex-col
     (row a)
     (row b)]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../scramble.js"]
       :fitty? true}
      (-> req (assoc :query-fn query-fn) panel)))))
