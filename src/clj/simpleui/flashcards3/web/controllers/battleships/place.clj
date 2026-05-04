(ns simpleui.flashcards3.web.controllers.battleships.place)

(defn- range-restricted [i1 i2 m]
  (range (max i1 0) (min i2 m)))
(defn- range2 [i1 i2 j1 j2 m n]
  (for [i (range-restricted i1 i2 m)
        j (range-restricted j1 j2 n)]
    [i j]))

(defn- place-once [grid i j horizontal? m n]
  (let [i2 (if horizontal? (+ i 2) (+ i 3))
        j2 (if horizontal? (+ j 3) (+ j 2))
        perimeter (range2 (dec i) i2 (dec j) j2 m n)
        available? #(nil? (get-in grid %))
        fill #(assoc-in %1 %2 true)
        battleship (if horizontal? [1 2] [2 1])]
    (if (every? available? perimeter)
      (assoc-in (reduce fill grid perimeter) [i j] battleship)
      grid)))

(defn- place-randomly [grid horizontal? m n]
  (place-once
   grid
   (rand-int (dec m))
   (rand-int (dec n))
   horizontal?
   m
   n))

(defn- place-battleships [m n]
  (reduce
    #(place-randomly %1 (odd? %2) m n)
   (->> nil (repeat n) vec (repeat m) vec)
   (range 30)))

(defn- placement-coords [grid]
  (mapcat
   (fn [i row]
     (keep-indexed
      (fn [j x]
        (when (vector? x) [i j]))
      row))
   (range)
   grid))

(defn- clear-excess [grid coords]
  (reduce #(assoc-in %1 %2 nil) grid coords))

(defn placement [m n]
  (let [placement1 (place-battleships m n)
        coords1 (placement-coords placement1)
        placement2 (place-battleships m n)
        coords2 (placement-coords placement2)
        n (min (count coords1) (count coords2))]
    [(->> coords1 shuffle (drop n) (clear-excess placement1))
     (->> coords2 shuffle (drop n) (clear-excess placement2))
     n]))
