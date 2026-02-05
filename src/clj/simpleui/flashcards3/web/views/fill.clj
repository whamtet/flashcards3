(ns simpleui.flashcards3.web.views.fill
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent ^:endpoint select [req text]
  [:form {:class "p-2"
          :hx-post "edit"}
   (components/submit "Edit")
   [:input {:type "hidden"
            :name "text"
            :value text}]
   [:p text]])

(defcomponent ^:endpoint edit [req text]
  select
  [:form {:class "p-2"
          :hx-post "select"}
   (components/submit "Highlight")
   [:textarea {:class "w-full rounded-md border mt-2 p-2 update"
               :style {:height "80vh"}
               :name "text"}
    text]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (edit req)))))
