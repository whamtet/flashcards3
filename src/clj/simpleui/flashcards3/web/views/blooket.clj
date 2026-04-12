(ns simpleui.flashcards3.web.views.blooket
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent form [req init]
  [:form#my-form
   {:class "p-2"
    :action "../blooket-csv"
    :method "POST"}
   [:div.flex.items-center.py-2
    [:input {:type "submit"
             :value "Create"
             :class "bg-clj-blue py-1.5 px-3 rounded-lg text-white mr-2"}]]
   [:div.flex
    [:div.w-80.p-2
     [:div.text-xl.mb-2 "Questions (one per line)"]
     [:textarea {:class "border rounded-md p-2 w-full"
                 :rows 20
                 :name "questions"}]]
    [:div.w-80.p-2
     [:div.text-xl.mb-2 "Answers (one per line)"]
     [:textarea {:class "border rounded-md p-2 w-full"
                 :rows 20
                 :name "answers"}
      init]]]
   ])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (form req)))))
