(ns simpleui.flashcards3.web.controllers.students-persist
  (:import
    java.io.File))

(def f (File. "students.edn"))

(defn- slurp-students []
  (if (.exists f)
    (-> f slurp read-string)
    {}))

(defn- spit-students [x]
  (assert (map? x))
  (->> x pr-str (spit f)))

(defn- update-students [f & args]
  (as-> (slurp-students) $
        (apply f $ args)
        (spit-students $)))

(defn- course-name [s]
  (second
   (re-find #"course_name=([^&]+)" s)))

(defn assoc-students [url students]
  (update-students assoc (course-name url) students))
