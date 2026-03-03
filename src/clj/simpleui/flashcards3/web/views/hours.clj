(ns simpleui.flashcards3.web.views.hours
  (:require
    [clojure.string :as string]
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.hours :as hours]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent ^:endpoint hours [req command new-hours]
  (case command
    "add" (hours/parse-hours new-hours)
    nil)
  [:div.p-2 {:hx-target "this"}
   (for [[time class] (hours/get-hours)]
     [:div.flex.items-center time class])
   [:form {:hx-post "hours:add"}
    (components/submit "Add")
    [:textarea {:class "w-full rounded-md border mt-2 p-2"
                :style {:height "50vh"}
                :required true
                :placeholder "New Hours"
                :name "new-hours"}]]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (hours req)))))
