(ns simpleui.flashcards3.web.controllers.battleships.place)

(defn- perimeter [horizontal?]
  (if horizontal?
    [[-1 0] [-1 1]
     [0 -1] [0 0] [0 1] [0 2]
     [1 0] [1 1]]
    [[-1 0]
     [0 -1] [0 0] [0 1]
     [1 -1] [1 0] [1 1]
     [2 0]]))

(defn- fill [m n]
  (fn [grid [i j]]
    (if (and (< -1 i m) (< -1 j n))
      (assoc-in grid [i j] true)
      grid)))

(defn- place-once [{:keys [grid battleships] :as data} i j horizontal? m n]
  (let [perimeter (map (fn [[k l]] [(+ i k) (+ j l)]) (perimeter horizontal?))
        available? #(nil? (get-in grid %))]
    (if (every? available? perimeter)
      {:grid (reduce (fill m n) grid perimeter)
       :battleships (conj battleships [i j horizontal?])}
      data)))

(defn- place-randomly [data horizontal? m n]
  (place-once data (rand-int (dec m)) (rand-int (dec n)) horizontal? m n))

(defn- place-battleships [m n]
  (->> 50
       range
       (reduce
        #(place-randomly %1 (odd? %2) m n)
        {:grid (->> nil (repeat n) vec (repeat m) vec)
         :battleships ()})
       :battleships))

(defn- place-battleships-well [m n i]
  (let [a (place-battleships m n)
        b (place-battleships m n)]
    (if (and (not= (set a) (set b)) (= (count a) (count b)))
      [a b (count a)]
      (if (pos? i)
        (recur m n (dec i))
        (let [n (min (count a) (count b))]
          [(take n a) (take n b) n])))))

(defn placement [m n]
  (place-battleships-well m n 5))
