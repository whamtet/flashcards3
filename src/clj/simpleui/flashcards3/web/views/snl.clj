(ns simpleui.flashcards3.web.views.snl
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.students-persist :as students-persist]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(def form
  [:form#my-form
   {:class "p-2"
    :action "../api/snl"
    :method "POST"}
   [:span.p-1
    [:input {:type "submit"
             :value "Create"
             :class "bg-clj-blue py-1.5 px-3 rounded-lg text-white"}]]
   [:input {:class "rounded-md border mt-2 p-2"
            :name "limit"
            :placeholder "Limit (optional)"}]
   [:textarea {:class "w-full rounded-md border mt-2 p-2"
               :style {:height "30vh"}
               :placeholder "Phrases - one per line"
               :name "phrases"}]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      form))))
