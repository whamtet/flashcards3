(ns simpleui.flashcards3.web.controllers.snl
  (:require
    [clojure.set :as set]
    [simpleui.flashcards3.web.htmx :refer [page-htmx]]
    [simpleui.flashcards3.web.views.icons :as icons]))

(def exclusions [#{0 2 5}
                 #{2 4 5}
                 #{1 2 3 4}
                 #{1 4}
                 #{1 3 6}
                 #{2 3 4 5 6}
                 #{1 3 5}
                 #{3 4}
                 #{6}])
(def full-row (set (range 7)))

(def pairs (mapcat
            (fn [i exclusions]
              (map #(list i %) (set/difference full-row exclusions)))
            (range)
            exclusions))

(def tl [[30 50]
         [161 188]
         [285 303]
         [423 429]
         [547 570]
         [683 715]
         [795 840]
         [790 966]
         [790 1067]])

(def br [[131 142]
         [258 265]
         [396 389]
         [520 528]
         [652 666]
         [768 799]
         [873 915]
         [873 1029]
         [873 1135]])

(def left-offsets (mapv first tl))
(def top-offsets (mapv second tl))
(def widths (mapv #(- (first %1) (first %2)) br tl))
(def heights (mapv #(- (second %1) (second %2)) br tl))

(defn- style [[i j]]
  {:left (str (left-offsets j) "px")
   :top (str (top-offsets i) "px")
   :width (str (widths j) "px")
   :height (str (heights i) "px")})

(defn- text-div [[pair phrase]]
  [:div.absolute {:style (style pair)} phrase])

(defn- disp2 [to-disp]
  [:div {:style {:transform "rotate(90deg);"}}
   [:div.relative {:style {:width "903px"}}
    [:img {:src "../snl.png"}]
    (map text-div to-disp)]])

(defn- disp [to-disp]
  (page-htmx
   {:css ["../output.css"]}
   (disp2 to-disp)))

(defn parse [{{:keys [phrases limit]} :params}]
  (let [phrases (-> phrases .trim (.split "\n"))
        limit (if (not-empty limit) (Long/parseLong (.trim limit)) 25)
        actual-phrases (->> phrases cycle (take limit))]
    (disp
     (map list (shuffle pairs) actual-phrases))))

