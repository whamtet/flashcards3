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

(defn- reading-button [reading_id reading-name]
  [:div.text-clj-blue.mr-2.cursor-pointer {:hx-get "editor"
                                           :hx-vals {:reading_id reading_id}
                                           :id (str "edit_" reading_id)
                                           :hx-target "#editor"}
   reading-name])

(defn- row [{:keys [reading_id reading_name details]}]
  [:div
   [:div.relative.group.inline-flex.items-center
    [:div.p-2.text-2xl.flex.items-center
     (reading-button reading_id reading_name)
     [:div.mr-2 ": "]
     [:a.text-clj-blue.mr-2 (href (format "../fills/%s/" reading_id))
      "Fill"]]]])

(defcomponent ^:endpoint editor [req reading-name details command]
  (case command
    "reading-name"
    (do
      (reading/reading-name query-fn reading_id reading-name)
      (reading-button reading_id reading-name))
    "details"
    (do
      (reading/reading-details query-fn reading_id details)
      nil)
    (let [{:keys [reading_name details]} (reading/get-reading query-fn reading_id)]
      [:div#editor
       [:input {:class "p-2 rounded-md border w-full mb-2"
                :hx-post "editor:reading-name"
                :name "reading-name"
                :value reading_name
                :hx-target (str "#edit_" reading_id)
                :hx-vals {:reading_id reading_id}}]
       [:textarea {:class "border rounded-md p-2 w-full"
                   :style {:height "40vh"}
                   :hx-post "editor:details"
                   :name "details"
                   :hx-vals {:reading_id reading_id}}
        details]])))

(defcomponent ^:endpoint panel [req ^:prompt reading-name command]
  editor
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
     [:div#editor]
     (map row (reading/get-readings query-fn))]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
