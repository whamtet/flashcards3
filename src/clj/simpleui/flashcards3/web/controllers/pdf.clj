(ns simpleui.flashcards3.web.controllers.pdf
  (:require
    [clj-pdf.core :as pdf]
    [clojure.java.io :as io]
    [clj-http.lite.client :as client]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow])
  (:import
    javax.imageio.ImageIO
    java.awt.geom.AffineTransform
    java.awt.image.BufferedImage
    java.io.ByteArrayOutputStream
    java.io.ByteArrayInputStream))

(defn- slurp-img [url]
  (->
   (client/get url {:headers {"User-Agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"}
                    :as :stream})
   :body
   ImageIO/read))

(defn- rot90 [img]
  (let [rotated (BufferedImage. (.getHeight img) (.getWidth img) (.getType img))
        transform (doto (AffineTransform.)
                        (.translate (.getHeight img) 0)
                        (.rotate (Math/toRadians 90)))
        g2d (.createGraphics rotated)]
    (.drawImage g2d img transform nil)
    (.dispose g2d)
    rotated))

(defn- rotate-if-needed [img]
  (if (> (.getWidth img) (.getHeight img))
    (rot90 img)
    img))

(defn- img-el [[_ url]]
  [:image
   {}
   (rotate-if-needed (slurp-img url))])

(defn- pdf [details]
  (let [out (ByteArrayOutputStream.)]
    (pdf/pdf
      [{}
       (map img-el details)]
     out)
    (-> out .toByteArray ByteArrayInputStream.)))

(defn get-pdf [query-fn slideshow_id]
  (->> slideshow_id
       (slideshow/get-slideshow-details query-fn)
       pdf))
