(ns simpleui.flashcards3.web.controllers.pdf
  (:require
    [clj-pdf.core :as pdf]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clj-http.lite.client :as client]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow])
  (:import
    javax.imageio.ImageIO
    java.awt.geom.AffineTransform
    java.awt.image.AffineTransformOp
    java.awt.image.BufferedImage
    java.io.ByteArrayOutputStream
    java.io.ByteArrayInputStream))

(defn- slurp-img [url]
  (->
   (client/get url {:headers {"User-Agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"}
                    :as :stream})
   :body
   ImageIO/read))

(def max-width 500)
(def max-height 842)
(defn- rot-scale [img]
  (let [rotate? (> (.getWidth img) (.getHeight img))
        short-dim (min (.getWidth img) (.getHeight img))
        long-dim (max (.getWidth img) (.getHeight img))
        x-scale (/ max-width short-dim)
        y-scale (/ max-height long-dim)
        scale (min x-scale y-scale)]
    (if (and (= 1 scale) (not rotate?))
      img
      (let [scale (double scale)
            out (BufferedImage.
                  (-> short-dim (* scale) long)
                  (-> long-dim (* scale) long)
                  (.getType img))
            transform (if rotate?
                        (AffineTransform. 0. scale scale 0. 0. 0.)
                        (AffineTransform. scale 0. 0. scale 0. 0.))]
        (-> (AffineTransformOp. transform AffineTransformOp/TYPE_BICUBIC)
            (.filter img out))
        out))))

(defn- img-el [[_ url]]
  (when-let [img (slurp-img url)]
    [:image
     (rot-scale img)]))

(defn- trim-lines [s]
  (->> (.split s "\n")
       (remove #(-> % .trim empty?))
       (string/join "\n")))

(defn- pdf [{:keys [notes slides]}]
  (let [out (ByteArrayOutputStream.)]
    (pdf/pdf
      [{}
       [:paragraph (trim-lines notes)]
       (pmap img-el slides)]
     out)
    (-> out .toByteArray ByteArrayInputStream.)))

(defn get-pdf [query-fn slideshow_id]
  (->> slideshow_id
       (slideshow/get-slideshow-details query-fn)
       pdf))
