(ns simpleui.flashcards3.web.views.dominos
  (:require
    [clojure.string :as string]
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.env :refer [prod?]]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- truncate [s]
  (if (-> s count odd?)
    (butlast s)
    s))

(defn- shuffle-pairs* [phrases]
  (loop [done (->> phrases (take-nth 2) (mapv list))
         todo (->> phrases rest (take-nth 2) (zipmap (range)))
         i 0]
    (if (not-empty todo)
      (let [[j tail] (-> todo (dissoc i) seq rand-nth)]
        (recur
          (update done i conj tail)
          (dissoc todo j)
          (inc i)))
      done)))

(defn- shuffle-pairs [phrases]
  (->> phrases (map #(.trim %)) (take-while not-empty) truncate shuffle-pairs*))

(defn- td [x] [:td [:div.mt-8.border-2.p-2.border-black x]])

(defn- dominos [phrases]
  [:table
   [:tbody
    (for [[[a b] [c d]] (->> phrases shuffle-pairs (partition-all 2))]
      [:tr
       (td a)
       (td b)
       [:td (repeat 10 "&nbsp;")]
       (when c
         (list
          (td c)
          (td d)))])]])

(defcomponent ^:endpoint disp [req ^:array phrases]
  [:div {:class "p-4 text-2xl"
         :hx-target "this"}
   [:div {:class "print:hidden"
          :hx-get "edit"
          :hx-include ".phrases"}
    (components/button "Edit")
    (for [phrase phrases]
      [:input.phrases {:type "hidden" :name "phrases" :value phrase}])]
   (dominos phrases)])

(defcomponent ^:endpoint edit [req ^:array phrases]
  disp
  (let [phrases (or phrases (repeat 40 ""))]
    [:form {:class "p-2"
            :hx-post "disp"}
     [:div
      (components/submit "Disp")]
     (for [[a b] (partition 2 phrases)]
       [:div.flex.p-2
        [:input {:class "border p-2 rounded-md mr-4"
                 :name "phrases"
                 :value a}]
        [:input {:class "border p-2 rounded-md"
                 :name "phrases"
                 :value b}]])]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]}
      (edit req)))))
