(ns simpleui.flashcards3.web.views.play-double
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.views.icons :as icons]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- get-src [x]
  (if (string? x)
    (str "../../api/cache?src=" x)
    (format "../../api/local/%s" x)))

(defn- pluralize* [s]
  (let [n (-> s count (* 0.5))]
    (->> s
         (map
          (fn [i x]
            (if (< i n) [x] [x x]))
          (range))
         shuffle)))

(defn- pluralize [s]
  (let [s (take 6 (shuffle s))
        n (-> s count (* 0.5) Math/ceil long)]
    [(pluralize* (take n s))
     (pluralize* (drop n s))]))

[:div.grid.grid-rows-1.flex-1.min-h-0]
[:div.grid-cols-1]
[:div.grid-cols-2]
[:div.grid-cols-3]
[:div.grid-cols-4]
[:div.grid-cols-5]
(defn- half-column [inc slides]
  (let [s2 (apply concat slides)]
    [:div.flex.flex-col {:style {:height "50vh"}}
     [:div {:class (format "grid grid-rows-1 grid-cols-%s flex-1 min-h-0" (count s2))}
      (for [[src2 src] s2]
        [:img {:class "max-h-full w-full object-contain"
               :src (get-src src)
               :src2 (get-src src2)
               :onerror "fixSrc(event.target)"}])]
     [:div {:class (format "grid grid-rows-1 grid-cols-%s" (count slides))}
      (map-indexed
       (fn [i slide]
         (if (= 2 (count slide))
           [:div.text-center.tracking-wider.text-4xl.col-span-2.text-white (+ i inc)]
           [:div.text-center.tracking-wider.text-4xl.text-white (+ i inc)]))
       slides)]]))

(defcomponent ^:endpoint panel [req ^:long enlargement ^:boolean shuff]
  (let [[s1 s2] (pluralize (slideshow/get-slideshow-slides query-fn slideshow_id))]
    (if (empty? s1)
      [:div.p-6.text-xl "Empty"]
      [:div
       (half-column 1 s1)
       (half-column (inc (count s1)) s2)
       [:div {:style {:height "500px"}}]])))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../play-write.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
