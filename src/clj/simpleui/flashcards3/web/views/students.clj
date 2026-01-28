(ns simpleui.flashcards3.web.views.students
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      [:form {:class "p-2"
              :action "../api/students"
              :method "POST"}
       (components/submit "Parse")
       [:textarea {:class "w-full rounded-md border mt-2"
                   :style {:height "90vh"}
                   :name "text"}]]))))
