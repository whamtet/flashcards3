(ns simpleui.flashcards3.web.controllers.pdf-jtd
  (:require
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(def margin 10)

(defn- index [[x y]]
  (if (< x 50)
    (if (< y 50) 0 2)
    (if (< y 50) 1 3)))

(defn group-coords [xs ys]
  (reduce
   (fn [v point]
     (update v (index point) conj point))
   [() () () ()]
   (map list xs ys)))

(prn
 (group-coords [25 75 25 75] [25 25 75 75]))

(defn svg->pdf []
  (let [out (ByteArrayOutputStream.)]
    ;; produce PDF in another thread
    (pdf/pdf
         [{:size :a4
           :orientation :landscape
           :left-margin   margin
           :right-margin  margin
           :top-margin    margin
           :bottom-margin margin}

          [:pdf-table {:width-percent 100}
           [50 50]
           [[:pdf-cell {:height 237}] [:pdf-cell {:height 237}]]
           [[:pdf-cell {:height 237}] [:pdf-cell {:height 237}]]]]
         out)
    (ByteArrayInputStream. (.toByteArray out))))
