(ns simpleui.flashcards3.web.controllers.pdf-battleships
  (:require
    [clojure.java.io :as io]
    [clj-pdf.core :as pdf]
    [simpleui.flashcards3.web.controllers.battleships.place :as place]
    [simpleui.flashcards3.util :as util])
  (:import [java.io File
            ByteArrayOutputStream ByteArrayInputStream]))

(def battleships ["2" "3" "5"])
(defn- p [x]
  (prn 'x x)
  x)

(def slurped
  (for [battleship battleships]
    (->> battleship
         (format "ships/s%s.svg")
         io/resource
         slurp)))

(def margin 10)

(defn- svg [f]
  [:svg {} f])

(defn pdf [left top]
  (let [out (ByteArrayOutputStream.)]
    ;; produce PDF in another thread
    (pdf/pdf
         [{:size :a4
           :orientation :landscape
           :left-margin   margin
           :right-margin  margin
           :top-margin    margin
           :bottom-margin margin}

          (svg (first slurped))

          ]
         out)
    (-> out .toByteArray alength prn)
    (ByteArrayInputStream. (.toByteArray out))))
