(ns simpleui.flashcards3.web.controllers.pdf-icons
  (:require
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(def icon-files
  (->> "heroicons"
       File.
       file-seq
       (filter #(-> % .getName (.endsWith ".svg")))))

(defn short-name [^File f]
  (let [s (.getName f)]
    (.substring s 0 (- (count s) 4))))

(def name->f
  (util/zipmap-by short-name icon-files))

(def margin 10)

(defn- svg [x y img]
  (when-let [f (name->f img)]
    [:svg {:translate [x y]} f]))

(defn- svgs [x y imgs]
  (keep-indexed
   (fn [i img]
     (svg x (+ y (* i 40)) img))
   imgs))

(defn- svgs-horizontal [imgs]
  (keep-indexed
   (fn [i img]
     (svg (+ 13 (* i 240)) 14 img))
   imgs))

(defn svg->pdf [images]
  (let [out (ByteArrayOutputStream.)]
    ;; produce PDF in another thread
    (pdf/pdf
         [{:size :a4
           :orientation :landscape
           :left-margin   margin
           :right-margin  margin
           :top-margin    margin
           :bottom-margin margin}

          ;; key
          (svgs-horizontal (distinct images))

          (svgs 50 80 images)
          (svgs 50 317 images)

          (svgs 470 80 images)
          (svgs 470 317 images)

          [:pdf-table {:width-percent 100}
           [50 50]
           [[:pdf-cell {:height 237}] [:pdf-cell {:height 237}]]
           [[:pdf-cell {:height 237}] [:pdf-cell {:height 237}]]]]
         out)
    (ByteArrayInputStream. (.toByteArray out))))
