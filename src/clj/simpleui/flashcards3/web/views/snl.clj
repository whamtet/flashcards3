(ns simpleui.flashcards3.web.views.snl
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.students-persist :as students-persist]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent form [req init]
  [:form#my-form
   {:class "p-2"
    :action "../api/snl"
    :method "POST"}
   [:div.flex.items-center.py-2
    [:input {:type "submit"
             :value "Create"
             :class "bg-clj-blue py-1.5 px-3 rounded-lg text-white mr-2"}]
    [:input {:class "rounded-md border p-2 mr-2"
             :name "limit"
             :placeholder "Limit (optional)"}]
    [:div.p-2.cursor-pointer.mr-2 {:onclick "addTick('✅')"} "✅"]
    [:div.p-2.cursor-pointer.mr-2 {:onclick "addTick('❌')"} "❌"]]
   [:textarea {:id "phrases"
               :class "w-full rounded-md border mt-2 p-2"
               :style {:height "30vh"}
               :placeholder "Phrases - one per line"
               :name "phrases"}
    init]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]
       :js ["../snl.js"]}
      (form req)))))
