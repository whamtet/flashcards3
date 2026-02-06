(ns simpleui.flashcards3.web.views.fill
  (:require
    [clojure.string :as string]
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.env :refer [prod?]]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- append-offset [offsets i1 i2]
  (loop [[[t1 t2] & todo] (partition 2 offsets)
         i1 i1
         i2 i2]
    (if t1 ;; still have to check
      (let [i2 (if (<= i2 t2)
                 (min i2 (dec t1))
                 i2)
            i1 (if (<= t1 i1)
                 (max i1 (inc t2))
                 i1)]
        (recur todo i1 i2))
      (if (< i1 i2)
        (conj offsets i2 i1)
        offsets))))

(defn- drop-offset [offsets]
  (drop 2 offsets))

(def superscripts ["¹" "²" "³" "⁴" "⁵" "⁶" "⁷" "⁸" "⁹" "¹⁰"])

(defn- _ [i]
  (string/join (repeat i "_")))

(defn- white-out-string [s offsets]
  (->> offsets
       (partition 2)
       (sort-by first)
       (map list superscripts)
       (reduce
        (fn [s [superscript [i1 i2]]]
          (str
           (.substring s 0 i1)
           superscript
           (_ (- i2 i1))
           (when (< i2 (count s)) (.substring s i2))))
        s)))

(defcomponent ^:endpoint select [req text ^:longs offsets ^:long i1 ^:long i2 command]
  (let [offsets
        (case command
          "append" (append-offset offsets i1 i2)
          "drop" (drop-offset offsets)
          offsets)]
    [:div.p-2 {:hx-target "this"}
     [:form {:hx-post "edit"
             :hx-confirm (when prod? "Back to Edit?")}
      (components/hiddens "text" text)
      (components/submit "Edit")]
     [:form.hidden {:hx-post "select:append"}
      (components/hiddens "text" text
                          "offsets" offsets
                          "i1" "id"
                          "i2" "id")
      (components/submit-hidden "append")]
     [:form.hidden {:hx-post "select:drop"}
      (components/hiddens "text" text
                          "offsets" offsets)
      (components/submit-hidden "drop")]
     [:pre#text-disp.mt-4 (white-out-string text offsets)]
     [:script "listenTextDisp();"]]))

(defcomponent ^:endpoint edit [req text]
  select
  [:form {:class "p-2"
          :hx-post "select"}
   (components/submit "Highlight")
   [:textarea {:class "w-full rounded-md border mt-2 p-2 update"
               :style {:height "80vh"}
               :name "text"}
    "The quick brown fox droppped over the lazy brown dog

    ABCDEFGHIJKLMNOP"]])

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../output.css"]
       :js ["../fill.js"]}
      (edit req)))))
