(ns simpleui.flashcards3.web.controllers.students-persist
  (:require
    [java-time.api :as jt])
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

(defn- local-date-time [s]
  (when-let [match (re-find #"start_date_time=([^&]+)" s)]
    (->> match
         second
         (jt/local-date-time "ddMMyyyyHHmm")
         (jt/format "EEE HH:mm"))))

(defn- disp [s]
  (.trim
    (str (course-name s) " " (local-date-time s))))

(defn assoc-students [url students]
  (update-students assoc (disp url) students))

(defn get-classes []
  (keys (slurp-students)))

(defn get-students [class]
  ((slurp-students) class))

(defn delete-class [class]
  (update-students dissoc class))
