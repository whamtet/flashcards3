(ns simpleui.flashcards3.web.controllers.pdf-battleships
  (:require
    [clojure.string :as string]
    [clojure.java.io :as io]
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.web.controllers.battleships.place :as place]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(util/defm battleship [[i j]]
  (-> (format "ships/s%s%s.svg" i j)
      io/resource
      slurp))

(def margin 10)
(def table-width 100)
(def left-col-width 20)

(defn- svg [v]
  [:svg {:translate [200 200]}
   (battleship v)])

(defn- split-lines [^String s]
  (map #(.trim %) (.split (.trim s) "\n")))

(defn- table [left top]
  (vec
   (list*
    :table {:width table-width}
    (conj top "")
    (for [word left]
      (conj (map (constantly "") top) word)))))

(defn- pdf-cell [x]
  [:pdf-cell {:height 38} x])

(defn- proportions-row [n]
  (conj
   (repeat n (/ (- 100 left-col-width) n))
   left-col-width))

(defn- pdf-table [left top]
  (vec
   (concat
    [:pdf-table {:width-percent 100}
     (proportions-row (count top))
     (map pdf-cell
          (conj
            top
           ""))]
    (for [word left]
      (map pdf-cell
           (conj
            (repeat (count top) "")
            word))))))

(defn pdf [{:keys [left top pages]}]
  (let [out (ByteArrayOutputStream.)
        left (split-lines left)
        top (split-lines top)
        pages (Long/parseLong pages)
        m (count left)
        n (count top)
        placements (place/placement m n)
        table (pdf-table left top)]
    ;; produce PDF in another thread
    (pdf/pdf
     [{:size :a4
       :left-margin   margin
       :right-margin  margin
       :top-margin    0
       :bottom-margin 0
       :header false
       :footer false}

      (repeatedly
       pages
       #(list
         [:paragraph {:spacing-after 8 :size 14} "My Friend"]
         table
         [:paragraph {:spacing-after 8 :size 14} "Me (SECRET!!!)"]
         table
         [:pagebreak]))

      ]
     out)
    (ByteArrayInputStream. (.toByteArray out))))
