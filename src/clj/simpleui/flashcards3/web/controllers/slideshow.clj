(ns simpleui.flashcards3.web.controllers.slideshow
  (:require
    [simpleui.flashcards3.web.controllers.local :as local]))

(defn add-slideshow [query-fn slideshow_name]
  (when-not (query-fn :get-slideshow-name {:slideshow_name slideshow_name})
    (query-fn :insert-slideshow {:slideshow_name slideshow_name})))

(defn- read-details [s]
  (if s
    (let [{:keys [slides notes]} (read-string s)]
      {:slides slides
       :notes (if (vector? notes) notes (mapv (constantly "") slides))})
    {:slides []
     :notes []}))

(defn get-slideshows [query-fn]
  (->> (query-fn :get-slideshows {})
       (sort-by :slideshow_name)
       (map #(update % :details read-details))))

(defn get-slideshow-details [query-fn slideshow_id]
  (-> (query-fn :get-slideshow {:slideshow_id slideshow_id})
      :details
      read-details))
(defn get-slideshow-slides [query-fn slideshow_id]
  (:slides
    (get-slideshow-details query-fn slideshow_id)))
(defn get-slideshow-slides-edit [query-fn slideshow_id]
  (let [{:keys [slides notes]} (get-slideshow-details query-fn slideshow_id)]
    (map (fn [[medium] note] [medium note]) slides notes)))
(defn get-slideshow-slides-notes [query-fn slideshow_id]
  (let [{:keys [slides notes]} (get-slideshow-details query-fn slideshow_id)]
    (map list slides notes)))

(defn get-slideshow-name [query-fn slideshow_id]
  (:slideshow_name (query-fn :get-slideshow {:slideshow_id slideshow_id})))

(defn update-slideshow-name [query-fn slideshow_id slideshow_name]
  (query-fn :slideshow-name {:slideshow_id slideshow_id :slideshow_name slideshow_name}))

(defn- slideshow-details [query-fn slideshow_id details]
  (query-fn :slideshow-details {:slideshow_id slideshow_id :details (pr-str details)}))

(defn delete-slideshow [query-fn slideshow_id]
  (doseq [[slide] (get-slideshow-slides query-fn slideshow_id)]
    (when (number? slide)
      (local/delete slide)))
  (query-fn :slideshow-delete {:slideshow_id slideshow_id}))

(defn- update-slides [query-fn slideshow_id f & args]
  (as-> (get-slideshow-details query-fn slideshow_id) $
        (update $ :slides #(apply f % args))
        (update $ :notes #(apply f % args))
        (slideshow-details query-fn slideshow_id $)))

(defn conj-slideshow [query-fn slideshow_id x]
  (as-> (get-slideshow-details query-fn slideshow_id) $
        (update $ :slides conj x)
        (update $ :notes conj "")
        (slideshow-details query-fn slideshow_id $)))

(defn concat-slideshow [query-fn slideshow_id images]
  (as-> (get-slideshow-details query-fn slideshow_id) $
        (update $ :slides into (local/convert images))
        (update $ :notes into (repeat (count images) ""))
        (slideshow-details query-fn slideshow_id $)))

(defn slideshow-note [query-fn slideshow_id i note]
  (as-> (get-slideshow-details query-fn slideshow_id) $
        (update $ :notes assoc i note)
        (slideshow-details query-fn slideshow_id $)))

(defn- move-up-v [v i]
  (if (pos? i)
    (-> v
        (assoc (dec i) (v i))
        (assoc i (v (dec i))))
    (conj (subvec v 1) (v 0))))
(defn- move-down-v [v i]
  (if (< i (dec (count v)))
    (-> v
        (assoc i (v (inc i)))
        (assoc (inc i) (v i)))
    (vec
     (conj (butlast v) (peek v)))))

(defn- del-v [v i]
  (vec
   (concat
    (subvec v 0 i)
    (subvec v (inc i)))))

(defn up-slideshow [query-fn slideshow_id i]
  (update-slides query-fn slideshow_id move-up-v i))
(defn down-slideshow [query-fn slideshow_id i]
  (update-slides query-fn slideshow_id move-down-v i))
(defn delete-slide [query-fn slideshow_id i]
  (update-slides query-fn slideshow_id del-v i))
