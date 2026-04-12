(ns simpleui.flashcards3.web.controllers.pdf-snl
  (:require
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]
           javax.imageio.ImageIO))

(def margin 20)

(def exclusions [#{0 2 5}
                 #{2 4 5}
                 #{1 2 3 4}
                 #{1 4}
                 #{1 3 6}
                 #{2 3 4 5 6}
                 #{1 3 5}
                 #{3 4}
                 #{6}])
(def full-row (set (range 7)))

(def pairs (mapcat
            (fn [i exclusions]
              (map #(list i %) (set/difference full-row exclusions)))
            (range)
            exclusions))

(def left-offsets [0 77 154 231 308 386 460])
(def top-offsets [0 80 150 227 320 402 480 553 620])

(def line-limit 10)
(defn- split-lines [^String s]
  (let [[a & rest] (.split (.trim s) " ")]
    (reduce
     (fn [v s2]
       (let [s1 (peek v)]
         (if (< (+ (count s1) 1 (count s2)) line-limit)
           (conj (pop v) (str s1 " " s2))
           (conj v s2))))
     [a]
     rest)))

(def line-height 10)

(defn- draw-word [g2d [[i j] phrase]]
  (let [x (left-offsets j)
        y (top-offsets i)]
    (->> phrase
         split-lines
         (map-indexed
          (fn [i line]
            (->> i
                 (* line-height)
                 (+ y)
                 (.drawString g2d line x))))
         dorun)))

(defn pdf [{{:keys [phrases limit]} :params}]
  (let [out (ByteArrayOutputStream.)
        phrases (-> phrases .trim (.split "\n"))
        limit (if (not-empty limit) (Long/parseLong (.trim limit)) 25)
        actual-phrases (->> phrases cycle (take limit))
        pairs (map list (shuffle pairs) actual-phrases)]
    ;; produce PDF in another thread
    (pdf/pdf
     [{:size :a4
       :orientation :portrait
       :left-margin   margin
       :right-margin  margin
       :top-margin    margin
       :bottom-margin margin}

      [:graphics {:translate [40 100]}
       (fn [g2d]
         (doseq [pair pairs]
           (draw-word g2d pair)))]

      [:image
       {:xscale 0.95
        :yscale 0.95}
       (with-open [in (-> "public/snl.png" io/resource io/input-stream)]
         (ImageIO/read in))]

      ]
     out)
    (ByteArrayInputStream. (.toByteArray out))))
