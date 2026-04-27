(ns simpleui.flashcards3.web.controllers.slideshow.delete
  (:require
    [clojure.set :as set]
    [simpleui.flashcards3.web.controllers.local :as local]))

(defn read-details [s]
  (if s
    (let [{:keys [slides notes updated]} (read-string s)]
      {:slides slides
       :notes (if (vector? notes) notes (mapv (constantly "") slides))
       :updated updated})
    {:slides []
     :notes []}))

(defn- keep-long [[x]]
  (when (number? x) x))
(defn- all-stored [query-fn]
  (->> (query-fn :get-slideshows {})
       (mapcat #(->> % :details read-details :slides (keep keep-long)))
       set))

(defn- to-clean [query-fn]
  (set/difference (local/all-locals) (all-stored query-fn)))

(defn clean-files [query-fn]
  (doseq [to-clean (to-clean query-fn)]
    (local/delete to-clean)))
