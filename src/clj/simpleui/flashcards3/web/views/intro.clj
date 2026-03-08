(ns simpleui.flashcards3.web.views.intro
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(def intro
  [:div.p-2
   [:div.text-blue-500.p-4
    [:a {:href "students/" :target "_blank"}
     "Student Questionnaire"]]
   [:div.text-blue-500.p-4
    [:a {:href "fill/" :target "_blank"}
     "Gap Fill"]]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["output.css"]}
      intro))))
