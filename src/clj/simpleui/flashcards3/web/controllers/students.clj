(ns simpleui.flashcards3.web.controllers.students
  (:require
    [clojure.string :as string]
    [simpleui.flashcards3.web.htmx :refer [page-htmx]]))

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

(defn- disp2 [questions students]
  [:table {:class "table-fixed min-w-full border border-gray-300"}
   [:colgroup
    [:col {:class "w-12"}]
    [:col {:class "w-auto" :span (count questions)}]]

   [:thead {:class "bg-gray-100"}
    [:tr
     [:th {:class "border border-gray-300 px-2 py-2 text-left font-semibold"}]
     (map q-header questions)]]

   [:tbody
    (map #(s-row % (count questions)) students)]])

(defn- disp [questions students]
  (page-htmx
   {:css ["../output.css"]}
   (disp2 questions students)))

(defn- parse-questions [questions]
  (-> questions .trim (.split "\n")))

(defn parse [{:keys [questions students]}]
  (->>
   (after students "Note")
   (re-seq r)
   (map #(-> % second .trim))
   (disp (parse-questions questions))))

