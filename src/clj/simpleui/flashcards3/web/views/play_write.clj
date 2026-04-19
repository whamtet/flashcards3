(ns simpleui.flashcards3.web.views.play-write
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :refer [get-src] :as components]
    [simpleui.flashcards3.web.views.icons :as icons]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- inc-mod [x]
  (mod (inc x) 4))
(defn- font-size [x]
  (str (max 0.6 (dec x)) "em"))

[:div.grid.grid-rows-1.flex-1.min-h-0]
[:div.grid-cols-1]
[:div.grid-cols-2]
[:div.grid-cols-3]
[:div.grid-cols-4]
[:div.grid-cols-5]
[:div.grid-cols-6]
[:div.grid-cols-7]
(defn- half-column [enlargement slides]
  [:div.flex.flex-col {:style {:height "48vh"}}
   [:div {:class (format "grid grid-rows-1 grid-cols-%s flex-1 min-h-0" (count slides))}
    (for [[src2 src] (map first slides)]
      [:img {:class "max-h-full w-full object-contain"
             :src (get-src src)
             :src2 (get-src src2)
             :onerror "fixSrc(event.target)"
             :hx-get "panel"
             :hx-vals {:enlargement (inc-mod enlargement)}}])]
   (when (pos? enlargement)
     [:div {:class (format "grid grid-rows-1 grid-cols-%s" (count slides))}
      (for [note (map second slides)]
        [:div.text-center.tracking-wider
         {:style {:font-size (font-size enlargement)}} note])])])

(defcomponent ^:endpoint panel [req ^:long enlargement ^:boolean shuff]
  (let [slides (slideshow/get-slideshow-slides-notes query-fn slideshow_id)
        slides (if shuff (shuffle slides) slides)
        n (-> slides count (/ 2) Math/ceil long)
        enlargement (or enlargement 0)]
    [:div {:hx-target "this"}
     [:div#shuffle.hidden
      {:hx-get "panel"
       :hx-vals {:enlargement enlargement
                 :shuff true}}]
     (if (empty? slides)
       [:div.p-6.text-xl "Empty"]
       [:div
        (half-column enlargement (take n slides))
        (half-column enlargement (drop n slides))])
     [:div {:style {:height "500px"}}]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../play-write.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
