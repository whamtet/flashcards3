(ns simpleui.flashcards3.util
  (:require
    [simpleui.flashcards3.env :refer [dev?]]
    [clojure.data.json :as json]))

(def read-str #(json/read-str % {:key-fn keyword}))

(defmacro defm [sym & rest]
  `(def ~sym (memoize (fn ~@rest))))

(defmacro defm-dev [sym & rest]
  `(def ~sym ((if dev? identity memoize) (fn ~@rest))))
