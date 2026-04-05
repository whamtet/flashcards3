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

(defn- gt [a b]
  (pos? (compare a b)))

(defn max-by [f [a & rest]]
  (first
   (reduce
    (fn [[x1 y1] x2]
      (let [y2 (f x2)]
        (if (gt y1 y2)
          [x1 y1]
          [x2 y2])))
    [a (f a)]
    rest)))
