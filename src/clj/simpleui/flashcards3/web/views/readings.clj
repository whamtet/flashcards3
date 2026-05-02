(ns simpleui.flashcards3.web.views.readings
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.env :refer [prod?]]
      [simpleui.flashcards3.web.controllers.reading :as reading]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- href [href]
  (if prod?
    {:href href :target "_blank"}
    {:href href}))

(defn- row [{:keys [reading_name details]}]
  [:div
   [:div.relative.group.inline-flex.items-center
    [:div.p-2.text-2xl.flex.items-center
     [:div.text-clj-blue.mr-2.cursor-pointer {:onclick "alert('hi')"}
      reading_name]
     (for [i (range 4)]
       [:div.text-clj-blue.mr-2.cursor-pointer {:onclick "alert('ok')"}
        i])]]])

(defcomponent ^:endpoint panel [req ^:prompt reading-name command]
  (case command
    "new" (when reading-name
            (reading/add-reading query-fn reading-name)
            :refresh)
    [:div.p-2
     [:div.flex.items-center.mb-1
      [:div {:class "my-1 mr-2"
             :hx-post "panel:new"
             :hx-prompt "New Reading Name"}
       (components/button "New Reading")]]
     (map row (reading/get-readings query-fn))]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
