(ns simpleui.flashcards3.web.controllers.reading
  (:require
    [simpleui.flashcards3.web.controllers.util :as util]))

(defn add-reading [query-fn reading_name]
  (when-not (query-fn :get-reading-name {:reading_name reading_name})
    (query-fn :insert-reading {:reading_name reading_name})))

(defn get-readings [query-fn]
  (->> (query-fn :get-readings {})
       (sort-by :reading_name util/compare-names)))

(defn reading-name [query-fn reading_id reading_name]
  (query-fn :reading-name {:reading_id reading_id :reading_name reading_name}))
(defn reading-details [query-fn reading_id details]
  (query-fn :reading-details {:reading_id reading_id :details details}))

(defn get-reading [query-fn reading_id]
  (query-fn :get-reading {:reading_id reading_id}))
(defn get-details [query-fn reading_id]
  (:details
    (get-reading query-fn reading_id)))
