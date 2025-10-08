(ns simpleui.flashcards3.util
  (:require
    [simpleui.flashcards3.env :refer [dev? prod?]]
    [clojure.data.json :as json]))

(def read-str #(json/read-str % {:key-fn keyword}))

(defmacro defm [sym & rest]
  `(def ~sym (memoize (fn ~@rest))))

(defmacro defm-dev [sym & rest]
  `(def ~sym ((if dev? memoize identity) (fn ~@rest))))

(defmacro defm-prod [sym & rest]
  `(def ~sym ((if prod? memoize identity) (fn ~@rest))))

(defn map-first-last [f s]
  (let [j (-> s count dec)]
    (map-indexed
     (fn [i x]
       (f i (zero? i) (= i j) x))
     s)))
