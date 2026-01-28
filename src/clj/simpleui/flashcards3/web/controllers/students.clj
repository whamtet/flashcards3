(ns simpleui.flashcards3.web.controllers.students
  (:require
    [clojure.string :as string]))

(defn- after [s split]
  (last (.split s split)))

(def r #"(?m)^\d+\s+(\D+)")

(defn parse [text]
  (->>
   (after text "Note")
   (re-seq r)
   (map #(-> % second .trim))
   (string/join "\n")))
