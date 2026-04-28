(ns simpleui.flashcards3.web.controllers.pdf-jtd
  (:require
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(def margin 10)
(def cell-height 240)

(defn- parse [s]
  (if (string? s)
    [(Long/parseLong s)]
    (map #(Long/parseLong %) s)))

(defn pdf [{:keys [is xs ys]}]
  (let [out (ByteArrayOutputStream.)
        is (parse is)
        xs (parse xs)
        ys (parse ys)]
    ;; produce PDF in another thread
    (pdf/pdf
         [{:size :a4
           :left-margin   margin
           :right-margin  margin
           :top-margin    margin
           :bottom-margin margin}

          [:pdf-table {:width-percent 100}
           [50 50]
           [[:pdf-cell {:height cell-height}] [:pdf-cell {:height cell-height}]]
           [[:pdf-cell {:height cell-height}] [:pdf-cell {:height cell-height}]]
           [[:pdf-cell {:height cell-height}] [:pdf-cell {:height cell-height}]]]

          [:svg {:translate [100 100]}
           "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
            <!DOCTYPE svg>
            <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"304\" height=\"290\">"
              [:path {:d "M2,111 h300 l-242.7,176.3 92.7,-285.3 92.7,285.3 z"
                      :style "fill:#FB2;stroke:#BBB;stroke-width:15;stroke-linejoin:round"}]
            "</svg>"]

          ]
         out)
    (ByteArrayInputStream. (.toByteArray out))))
