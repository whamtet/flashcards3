(ns simpleui.flashcards3.web.controllers.students
  (:require
    [clojure.string :as string]
    [simpleui.flashcards3.web.htmx :refer [page-htmx]]
    [simpleui.flashcards3.web.views.icons :as icons]))

(defn- after [s split]
  (last (.split s split)))

(def r #"(?m)^\d+\s+(\D+)")

(defn q-header [question]
  [:th {:class "border border-gray-300 px-4 py-2 text-left font-semibold"}
   question])

(defn s-row [student colspan]
  [:tr
   [:td {:class "border border-gray-300 px-2 py-2 whitespace-nowrap"}
    student]
   [:td {:class "border border-gray-300 px-4 py-2"
         :colspan colspan}]])

(defn s-row2 [student colspan stars]
  [:tr
   [:td {:class "border border-gray-300 px-2 py-2 whitespace-nowrap"}
    student]
   (for [i (range colspan)]
     [:td {:class "border border-gray-300 px-4 py-2"}
      [:div.flex
       (interpose " " (repeat stars icons/star))]])])

(defn- disp2 [questions students stars]
  [:table {:class "table-fixed min-w-full border border-gray-300"}
   [:colgroup
    [:col {:class "w-12"}]
    [:col {:class "w-auto" :span (count questions)}]]

   [:thead {:class "bg-gray-100"}
    [:tr
     [:th {:class "border border-gray-300 px-2 py-2 text-left font-semibold"}]
     (map q-header questions)]]

   [:tbody
    (map
     (if stars #(s-row2 % (count questions) stars) #(s-row % (count questions)))
     students)]])

(defn- disp [questions students stars]
  (page-htmx
   {:css ["../output.css"]}
   (disp2 questions students stars)))

(defn- parse-questions [questions]
  (-> questions .trim (.split "\n")))

(defn parse [{:keys [questions students stars]}]
  (let [students (->> (after students "Note")
                      (re-seq r)
                      (map #(-> % second .trim)))
        stars (when (not-empty stars) (Long/parseLong stars))]
    (disp
     (parse-questions questions)
     (concat students ["&nbsp;" "&nbsp;" "&nbsp;" "&nbsp;"])
     stars)))

