(ns simpleui.flashcards3.web.controllers.slideshow)

(defn add-slideshow [query-fn slideshow_name]
  (query-fn :insert-slideshow {:slideshow_name slideshow_name}))

(defn- read-details [s]
  (if s
    (read-string s)
    []))

(defn get-slideshows [query-fn]
  (->> (query-fn :get-slideshows {})
       (sort-by :slideshow_name)
       (map #(update % :details read-details))))

(defn get-slideshow-details [query-fn slideshow_id]
  (-> (query-fn :get-slideshow {:slideshow_id slideshow_id})
      :details
      read-details))
(defn get-slideshow-name [query-fn slideshow_id]
  (:slideshow_name (query-fn :get-slideshow {:slideshow_id slideshow_id})))

(defn update-slideshow-name [query-fn slideshow_id slideshow_name]
  (query-fn :slideshow-name {:slideshow_id slideshow_id :slideshow_name slideshow_name}))

(defn- slideshow-details [query-fn slideshow_id details]
  (query-fn :slideshow-details {:slideshow_id slideshow_id :details (pr-str details)}))

(defn delete-slideshow [query-fn slideshow_id]
  (query-fn :slideshow-delete {:slideshow_id slideshow_id}))

(defn- update-slideshow [query-fn slideshow_id f & args]
  (as-> (get-slideshow-details query-fn slideshow_id) $
        (apply f $ args)
        (slideshow-details query-fn slideshow_id $)))

(defn conj-slideshow [query-fn slideshow_id x]
  (update-slideshow query-fn slideshow_id conj x))

(defn- move-up-v [v i]
  (assert (pos? i))
  (-> v
      (assoc (dec i) (v i))
      (assoc i (v (dec i)))))
(defn- del-v [v i]
  (vec
   (concat
    (subvec v 0 i)
    (subvec v (inc i)))))

(defn up-slideshow [query-fn slideshow_id i]
  (update-slideshow query-fn slideshow_id move-up-v i))
(defn down-slideshow [query-fn slideshow_id i]
  (update-slideshow query-fn slideshow_id move-up-v (inc i)))
(defn delete-slide [query-fn slideshow_id i]
  (update-slideshow query-fn slideshow_id del-v i))
