(ns simpleui.flashcards3.web.views.icon-search
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.pdf2 :as pdf2]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]])
  (:import
    java.io.File))

(def name->slurped
  (zipmap
   (map pdf2/short-name pdf2/icon-files)
   (map slurp pdf2/icon-files)))

(defcomponent ^:endpoint icons [req ^:trim s]
  (let [s (if s (.toLowerCase s) "")]
    [:div#icons {:class "flex flex-wrap mt-2 pt-2 border-t"}
     (for [[short-name svg] name->slurped
           :when (.contains short-name s)]
       [:div {:class "m-2 cursor-pointer"
              :hx-get "selected"
              :hx-vals {:to-add short-name}
              :hx-target "#selected"
              :hx-swap "beforeend"
              :title short-name} svg])]))

(defcomponent ^:endpoint selected [req to-add]
  (if-let [svg (name->slurped to-add)]
    [:div {:class "m-2 cursor-pointer"
           :hx-get "selected"}
     [:input {:type "hidden"
              :name "images"
              :value to-add}]
     svg]
    ""))

(defcomponent form [req]
  selected
  [:form {:class "p-2"
          :action "../pdf-icon"}
   [:div.flex.mb-2
    [:input {:class "p-2 border rounded-md mr-2"
             :placeholder "Search..."
             :hx-get "icons"
             :hx-target "#icons"
             :name "s"
             :hx-trigger "keyup changed delay:500ms"}]
    (components/submit "Go")]
   [:div#selected.flex.mb-2]
   (icons req)])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]
       :hyperscript? true}
      (form req)))))
