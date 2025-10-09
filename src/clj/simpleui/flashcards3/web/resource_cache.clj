(ns simpleui.flashcards3.web.resource-cache
    (:require
      [clojure.java.io :as io]
      [simpleui.flashcards3.env :refer [dev?]]))

(def hash-resource
  ((if dev? identity memoize)
   (fn [src]
     (->> (.split src "/")
          last
          (str "public/")
          io/resource
          slurp
          hash))))

(defn cache-suffix [src]
  (->> src
       hash-resource
       (str src "?hash=")))
