(ns simpleui.flashcards3.web.controllers.slideshow)

(defn add-slideshow [query-fn slideshow-name]
  (query-fn :insert-slideshow {:slideshow-name slideshow-name}))

(defn- read-string-safe [s]
  (when s (read-string s)))

(defn get-slideshows [query-fn]
  (map #(update % :details read-string-safe)
       (query-fn :get-slideshow {})))

(defn slideshow-name [query-fn slideshow_id slideshow-name]
  (query-fn :slideshow-name {:slideshow_id slideshow_id :slideshow-name slideshow-name}))

(defn slideshow-details [query-fn slideshow_id details]
  (query-fn :slideshow-details {:slideshow_id slideshow_id :details (pr-str details)}))
