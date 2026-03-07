(ns simpleui.flashcards3.web.views.hours
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.env :refer [prod?]]
    [simpleui.flashcards3.web.controllers.hours :as hours]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent ^:endpoint hours [req command new-hours ^:date time]
  (case command
    "add" (hours/parse-hours new-hours)
    "del" (hours/delete-hour time)
    nil)
  [:div.p-3 {:hx-target "this"}
   [:a {:href "../hours-total/"}
    (components/button "Summary")]
   [:div.mt-3
    (for [[week frequency] (hours/weeks)]
      [:div.flex.items-center.p-2
       week ": " [:b.bold.ml-2 frequency]])]
   (for [[time class] (hours/get-hours)]
     [:div.flex.items-center.p-2
      time
      [:div.ml-2 class]
      [:div {:class "ml-2"
             :hx-delete "hours:del"
             :hx-vals {:time time}
             :hx-confirm (when prod? (format "Delete %s?" time))}
       (components/button "Delete")]])
   [:form {:class "mt-2"
           :hx-post "hours:add"}
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
