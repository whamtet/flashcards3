(ns simpleui.flashcards3.web.controllers.word-search
  (:require
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.util :as util]))

(def grid-size 10)

(defn- longest-word [s]
  (util/max-by count (.split s " ")))
(defn- trim-note [^String note]
  (let [note (-> note .trim longest-word)]
    (when (< 0 (count note) (dec grid-size))
      (.toUpperCase note))))

(def nil-row (vec (repeat grid-size nil)))
(def nil-grid (vec (repeat grid-size nil-row)))

(defn- rand-char []
  (rand-nth "ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
(defn- rand-direction []
  (rand-nth [[-1 1]
             [0 1]
             [1 1]
             [1 0]]))
(defn- rand-start [id wc]
  (rand-nth
   (case id
     -1 (range (dec wc) grid-size)
     0 (range 0 grid-size)
     1 (range 0 (- grid-size (dec wc))))))

(defn- place-word [grid word i j id jd]
  (loop [[c & todo] word
         i i
         j j
         grid grid]
    (if c
      (let [d (get-in grid [i j])]
        (when (or (not d) (= c d))
          (recur todo (+ i id) (+ j jd) (assoc-in grid [i j] c))))
      grid)))

(defn- place-word-randomly [grid word]
  (let [[id jd] (rand-direction)]
    (place-word
     grid
     word
     (rand-start id (count word))
     (rand-start jd (count word))
     id
     jd)))

(defn- attempt-to-place-word [grid word]
  (some (fn [_] (place-word-randomly grid word)) (range 10)))

(defn- attempt-to-place-words [words]
  (reduce
   (fn [{:keys [grid words] :as unchanged} word]
     (if-let [new-grid (attempt-to-place-word grid word)]
       {:grid new-grid
        :words (conj words word)}
       unchanged))
   {:grid nil-grid
    :words ()}
   words))

(defn- fill-remainder [grid]
  (for [row grid]
    (map #(or % (rand-char)) row)))

(defn ws-grid [query-fn slideshow_id]
  (let [{placed :grid words :words}
        (->> (slideshow/get-slideshow-notes query-fn slideshow_id)
             (keep trim-note)
             distinct
             attempt-to-place-words)]
    {:grid (fill-remainder placed)
     :words words}))
