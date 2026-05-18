(ns simpleui.flashcards3.web.controllers.blooket
  (:require
    [clojure.string :as string]
    [clojure.java.io :as io]))

(defn- csv-row [row]
  (->> row (map #(str \" % \")) (string/join ",")))
(defn- csv-rows* [grid]
  (->> grid (map csv-row) (string/join "\r\n")))

;; just in case
(def header (-> "blooket.csv" io/resource slurp .trim))

(defn- csv-rows [grid]
  (str header "\r\n" (csv-rows* grid)))

(def time-limit 15)

(defn phrase-row [answers]
  (fn [i question answer]
    (let [other-answers (->> answers (remove #(= answer %)) shuffle (take 3))
          insert-index (rand-int 4)]
      (concat
       [(inc i) question]
       (take insert-index other-answers)
       [answer]
       (drop insert-index other-answers)
       [15 (inc insert-index)]))))

(defn- split-lines [^String s]
  (map #(.trim %) (.split (.trim s) "\n")))

(defn- shuffle-together [a b]
  (let [x (shuffle (map list a b))]
    [(map first x) (map second x)]))

(defn csv [{:keys [questions answers]}]
  (let [questions (split-lines questions)
        answers (split-lines answers)
        [questions answers] (shuffle-together questions answers)]
    (csv-rows
     (map (phrase-row answers) (range) questions answers))))
