(ns simpleui.flashcards3.web.controllers.pdf-battleships
  (:require
    [clojure.string :as string]
    [clojure.java.io :as io]
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.web.controllers.battleships.place :as place]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(util/defm-prod battleship [horizontal?]
  (->> (if horizontal? "12" "21")
       (format "public/ships/s%s.svg")
       io/resource
       slurp))

(defn- x-scale [n horizontal?]
  (/ (if horizontal? 9 6) n))

(defn- y-scale [horizontal?]
  (if horizontal? 1 0.8))

(defn- svg-battleship [m n]
  (let [x-gap (* 77 6 (/ 1 n))]
    (fn [[i j horizontal?]]
      [:svg {:translate [(+ 130 (* x-gap j)) (+ 392 (* 38 (+ i m -5)))] :scale [(x-scale n horizontal?) (y-scale horizontal?)]}
       (battleship horizontal?)])))

(defn- svg-key [i]
  [:svg {:translate [(+ 45 (* i 80)) 68] :scale 0.5}
   (battleship true)])

(defn- legend [n]
  (list
   (map svg-key (range n))
   [:pdf-table {:width-percent 85 :spacing-after 6 :cell-border false}
    [100]
    [[:pdf-cell {} " "]]]))

(defn- split-lines [^String s]
  (map #(.trim %) (.split (.trim s) "\n")))

(defn- pdf-cell [x]
  [:pdf-cell {:height 38} x])

(def left-col-width 20)
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

(defn- shorter [a b]
  (let [c (min (count a) (count b))]
    [(take c a) (take c b)]))

(defn pdf [{:keys [left1 top1 left2 top2]}]
  (let [out (ByteArrayOutputStream.)
        left1 (split-lines left1)
        left2 (split-lines left2)
        top1 (split-lines top1)
        top2 (split-lines top2)
        [left1 left2] (shorter left1 left2)
        [top1 top2] (shorter top1 top2)
        m (count left1)
        n (count top1)
        [placement1 placement2 num-ships] (place/placement m n)
        img (svg-battleship m n)
        legend (legend num-ships)
        table1 (pdf-table left1 top1)
        table2 (pdf-table left2 top2)]
    ;; produce PDF in another thread
    (pdf/pdf
     [{:size :a4
       :left-margin   15
       :right-margin  15
       :top-margin    0
       :bottom-margin 0
       :header false
       :footer false}

      [:paragraph {:spacing-after 8 :size 14} "My Friend (B)"]
      legend
      table1
      [:paragraph {:spacing-after 8 :size 14} "Me (SECRET!!!)"]
      table2
      (map img placement1)
      [:pagebreak]

      [:paragraph {:spacing-after 8 :size 14} "My Friend (A)"]
      legend
      table1
      [:paragraph {:spacing-after 8 :size 14} "Me (SECRET!!!)"]
      table2
      (map img placement2)

      ]
     out)
    (ByteArrayInputStream. (.toByteArray out))))
