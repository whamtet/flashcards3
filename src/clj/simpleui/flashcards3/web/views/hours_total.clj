(ns simpleui.flashcards3.web.views.hours-total
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.env :refer [prod?]]
    [simpleui.flashcards3.web.controllers.hours :as hours]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- table-row [[course count hours]]
  [:tr {:class "hover:bg-gray-50"}
   [:td {:class "px-4 py-2 text-sm text-gray-800"} course]
   [:td {:class "px-4 py-2 text-sm text-gray-800"} count]
   [:td {:class "px-4 py-2 text-sm text-gray-800"} hours]])

(defcomponent ^:endpoint report [req ^:long year ^:long month]
  (let [current (hours/year-month)
        ym (if year (hours/year-month year month) current)
        previous (hours/dec-month ym)
        next (hours/inc-month ym)
        {:keys [table total]} (hours/ym-table ym)]
    [:div.p-3 {:hx-target "this"}
     [:div.flex.items-center
      [:div {:hx-get "report"
             :hx-vals {:year (.getYear previous)
                       :month (.getMonthValue previous)}}
       (components/button "Previous Month")]
      ym
      (when (not= current ym)
        [:div {:hx-get "report"
               :hx-vals {:year (.getYear next)
                         :month (.getMonthValue next)}}
         (components/button "Next Month")])]
     [:div {:class "overflow-x-auto mt-4"}
      [:table {:class "min-w-full border border-gray-200 rounded-lg shadow-sm"}
       [:thead {:class "bg-gray-100"}
        [:tr
         [:th {:class "px-4 py-2 text-left text-sm font-semibold text-gray-700 border-b"}
          "Course Code"]
         [:th {:class "px-4 py-2 text-left text-sm font-semibold text-gray-700 border-b"}
          "Count"]
         [:th {:class "px-4 py-2 text-left text-sm font-semibold text-gray-700 border-b"}
          "Total Hours"]]]
       [:tbody {:class "divide-y divide-gray-200 bg-white"}
        (map table-row table)
        [:tr {:class "hover:bg-gray-50 font-semibold"}
         [:td {:class "px-4 py-2 text-sm text-gray-800"}]
         [:td {:class "px-4 py-2 text-sm text-gray-800"}]
         [:td {:class "px-4 py-2 text-sm text-gray-800"} total]]]]]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (report req)))))
