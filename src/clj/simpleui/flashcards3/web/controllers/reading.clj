(ns simpleui.flashcards3.web.controllers.reading
  (:require
    [simpleui.flashcards3.web.controllers.util :as util]))

(defn add-reading [query-fn reading_name]
  (when-not (query-fn :get-reading-name {:reading_name reading_name})
    (query-fn :insert-reading {:reading_name reading_name})))

(defn get-readings [query-fn]
  (->> (query-fn :get-readings {})
       (sort-by :reading_name util/compare-names)))
