(ns simpleui.flashcards3.web.controllers.cache
  (:require
    [clojure.java.io :as io]
    [clj-http.lite.client :as client])
  (:import
    java.io.File
    java.net.URL))

(def cache-dir (File. "cache"))
(.mkdir cache-dir)

(defn cache-file [src]
  (-> src
      (.substring (->> src URL. .getPath (.indexOf src)))
      (.replace "/" "")
      (.replace "?" "")
      (.replace "&" "")
      (.replace "=" "")
      (->> (File. cache-dir))))

(defn- web-stream [url]
  (:body
    (client/get url {:headers {"User-Agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"}
                     :as :stream})))

(defn cache [src]
  (let [f (cache-file src)]
    (if (.exists f)
      (io/input-stream f)
      (do
        (with-open [in (web-stream src)]
          (io/copy in f))
        (io/input-stream f)))))
