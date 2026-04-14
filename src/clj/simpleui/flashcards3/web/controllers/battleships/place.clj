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
  (fn [_]
    (place-once
     grid
     (rand-start m id)
     (rand-start n jd)
     battleship)))

(defn- place-battleship [m n]
  (fn [grid battleship]
    (or
     (some (place-randomly m n grid battleship) (range 10))
     grid)))

(defn- placement** [m n]
  (reduce
   (place-battleship m n)
   (v m (v n nil))
   (shuffle battleships)))

(defn placement* [m n]
  (->> (placement** m n)
       (mapcat
        (fn [i row]
          (map-indexed
           (fn [j battleship]
             (when (vector? battleship)
               [[i j] battleship]))
           row))
        (range))
       (into {})))

(defn placement [m n]
  (let [placement (placement* m n)]
    {:placement placement
     :battleships (->> placement vals (map sort) frequencies)}))
