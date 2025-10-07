(ns simpleui.flashcards3.web.controllers.slideshow)

(defn add-slideshow [query-fn slideshow_name]
  (query-fn :insert-slideshow {:slideshow_name slideshow_name}))

(defn- read-string-safe [s]
  (when s (read-string s)))

(defn get-slideshows [query-fn]
  (map #(update % :details read-string-safe)
       (query-fn :get-slideshows {})))

(defn get-slideshow [query-fn slideshow_id]
  (update (query-fn :get-slideshow {:slideshow_id slideshow_id}) :details read-string-safe))
(defn get-slideshow-name [query-fn slideshow_id]
  (:slideshow_name (query-fn :get-slideshow {:slideshow_id slideshow_id})))

(defn update-slideshow-name [query-fn slideshow_id slideshow_name]
  (query-fn :slideshow-name {:slideshow_id slideshow_id :slideshow_name slideshow_name}))

(defn slideshow-details [query-fn slideshow_id details]
  (query-fn :slideshow-details {:slideshow_id slideshow_id :details (pr-str details)}))

(defn delete-slideshow [query-fn slideshow_id]
  (query-fn :slideshow-delete {:slideshow_id slideshow_id}))
