(ns simpleui.flashcards3.web.views.components)

(defn button [label]
  [:span.p-1
   [:button {:type "button"
             :class "bg-clj-blue py-1.5 px-3 rounded-lg text-white"}
    label]])

(defn button-warning [label] ;; see also warning below
  [:span.p-1
   [:button {:type "button"
             :class "bg-red-600 py-1.5 px-3 rounded-lg text-white"}
    label]])

(defn warning [msg]
  [:span {:class "bg-red-600 p-2 rounded-lg text-white"} msg])

(defn h1 [& contents]
  [:h1.text-3xl contents])

(defn h2 [& contents]
  [:h2.text-2xl contents])

(defn h3 [& contents]
  [:h3.text-xl contents])
