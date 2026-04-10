(ns simpleui.flashcards3.web.controllers.word-search
  (:require
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.util :as util]))

(defn- longest-word [s]
  (util/max-by count (.split s " ")))
(defn- trim-note [^String note grid-size]
  (let [note (-> note .trim longest-word)]
    (when (< 0 (count note) (dec grid-size))
      (.toUpperCase note))))

(defn- nil-row [grid-size] (vec (repeat grid-size nil)))
(defn- nil-grid [grid-size] (vec (repeat grid-size (nil-row grid-size))))

(defn- rand-char []
  (rand-nth "ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
(defn- rand-direction []
  (rand-nth [[-1 1]
             [-1 1]
             [0 1]
             [1 1]
             [1 1]
             [1 0]]))
(defn- rand-start [id wc grid-size]
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

(defn- place-word-randomly [grid word grid-size]
  (fn [_]
    (let [[id jd] (rand-direction)]
      (place-word
       grid
       word
       (rand-start id (count word) grid-size)
       (rand-start jd (count word) grid-size)
       id
       jd))))

(defn- attempt-to-place-words [grid-size words]
  (reduce
   (fn [{:keys [grid words] :as unchanged} word]
     (if-let [new-grid (some (place-word-randomly grid word grid-size) (range 10))]
       {:grid new-grid
        :words (conj words word)}
       unchanged))
   {:grid (nil-grid grid-size)
    :words ()}
   words))

(defn- fill-remainder [grid]
  (for [row grid]
    (map #(or % (rand-char)) row)))

(defn ws-grid [query-fn slideshow_id grid-size]
  (let [{placed :grid words :words}
        (->> (slideshow/get-slideshow-notes query-fn slideshow_id)
             (keep #(trim-note % grid-size))
             distinct
             (attempt-to-place-words grid-size))]
    {:grid (fill-remainder placed)
     :words words}))
