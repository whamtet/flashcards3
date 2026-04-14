(ns simpleui.flashcards3.web.controllers.pdf-battleships
  (:require
    [clojure.string :as string]
    [clojure.java.io :as io]
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.web.controllers.battleships.place :as place]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(util/defm-prod battleship [[i j]]
  (-> (format "ships/s%s%s.svg" i j)
      io/resource
      slurp))

(defn x-scale [n b]
  (* 6 (/ 1 n)
     (case b
       [1 2] 1.5
       [1 3] 1.5
       [1 4] 1.3
       1)))

(defn- y-scale [b]
  (case b
    [1 4] 0.8
    [1 3] 0.85
    [2 1] 0.8
    [3 1] 0.85
    [4 1] 0.7
    1))

(defn- svg-battleship [m n]
  (let [x-gap (* 78 6 (/ 1 n))]
    (fn [{[i j] :position b :battleship}]
      [:svg {:translate [(+ 130 (* x-gap j)) (+ 392 (* 38 (+ i m -5)))] :scale [(x-scale n b) (y-scale b)]}
       (battleship b)])))

(def key-scales {2 0.7
                 3 0.6
                 4 0.5})
(defn- svg-key [i length]
  [:svg {:translate [(+ 45 (* i 140)) 65] :scale (key-scales length)}
   (battleship [1 length])])

(defn- pdf-cells [items]
  (map #(vector :pdf-cell {} %) items))

(defn- legend [battleships]
  (list
   (map-indexed svg-key (keys battleships))
   [:pdf-table {:width-percent 85 :spacing-after 6 :cell-border false}
    (apply concat (repeat (count battleships) [200/9 100/9]))
    (pdf-cells
     (mapcat
      (fn [[_b frequency]]
        [""
         (str "x" frequency)])
      battleships))]))

(def margin 10)
(def left-col-width 20)

(defn- split-lines [^String s]
  (map #(.trim %) (.split (.trim s) "\n")))

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

(defn pdf [{:keys [left top]}]
  (let [out (ByteArrayOutputStream.)
        left (split-lines left)
        top (split-lines top)
        m (count left)
        n (count top)
        {:keys [placement1 placement2 freqs]} (place/placement m n)
        img (svg-battleship m n)
        legend (legend freqs)
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

      [:paragraph {:spacing-after 8 :size 14} "My Friend (B)"]
      legend
      table
      [:paragraph {:spacing-after 8 :size 14} "Me (SECRET!!!)"]
      table
      (map img placement1)
      [:pagebreak]

      [:paragraph {:spacing-after 8 :size 14} "My Friend (A)"]
      legend
      table
      [:paragraph {:spacing-after 8 :size 14} "Me (SECRET!!!)"]
      table
      (map img placement2)

      ]
     out)
    (ByteArrayInputStream. (.toByteArray out))))
