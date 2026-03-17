(ns simpleui.flashcards3.web.views.students
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx]]))

(defn- form [req]
  [:form {:class "p-2"
          :action (if (:basic-authentication req) "../api/studentss" "../api/students")
          :method "POST"}
   (components/submit "Parse")
   [:input {:class "rounded-md border mt-2 p-2"
            :name "stars"
            :placeholder "Stars (optional)"}]
   [:textarea {:class "w-full rounded-md border mt-2 p-2"
               :style {:height "30vh"}
               :placeholder "Questions - one per line"
               :name "questions"}]
   (when (:basic-authentication req)
     [:input {:class "rounded-md border mt-2 p-2 w-full"
              :name "url"
              :placeholder "URL"}])
   [:textarea {:class "w-full rounded-md border mt-2 p-2"
               :style {:height "60vh"}
               :placeholder "Students - copy from VUS attendance form"
               :required true
               :name "students"}]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (form req)))))
