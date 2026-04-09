(ns simpleui.flashcards3.web.controllers.battleships.place)

(def battleships
  [[1 2]
   [1 3]
   [1 5]
   [2 1]
   [3 1]
   [5 1]])

(defn- v [i x]
  (vec (repeat i x)))
(defn- range2 [i1 i2 j1 j2]
  (for [i (range i1 i2) j (range j1 j2)]
    [i j]))
(defn- rand-start [m id]
  (rand-int (- (inc m) id)))

(defn- place-once [grid i1 j1 [id jd :as battleship]]
  (loop [grid grid
         [coord & coords] (range2 i1 (+ i1 id) j1 (+ j1 jd))
         [placement & placements] (conj (range 1 (* id jd)) battleship)]
    (if coord
      (when-not (get-in grid coord)
        (recur (assoc-in grid coord placement) coords placements))
      grid)))

(defn- place-randomly [m n grid [id jd :as battleship]]
  (place-once
   grid
   (rand-start m id)
   (rand-start n jd)
   battleship))

(defn- place-battleship [m n]
  (fn [{:keys [grid battleships] :as unchanged} battleship]
    (if-let [placed (->> #(place-randomly m n grid battleship)
                         repeatedly
                         (take 10)
                         (some identity))]
      {:grid placed
       :battleships (conj battleships (sort battleship))}
      unchanged)))

(defn- placement* [m n]
  (reduce
   (place-battleship m n)
   {:grid (v m (v n nil))
    :battleships ()}
   (shuffle battleships)))

(defn placement [m n]
  (update (placement* m n) :battleships frequencies))

(use 'clojure.pprint)
(pprint (placement 10 10))
