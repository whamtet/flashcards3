(ns simpleui.flashcards3.web.views.play-write
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

(defn- inc-mod [x]
  (mod (inc x) 5))
(defn- font-size [x]
  (str (max 0.5 (dec x)) "em"))

[:div.grid-rows-2.grid-cols-2]
[:div.grid-cols-3]
[:div.grid-cols-4]
[:div.grid-cols-5]
[:div.grid-cols-6]
[:div.grid-cols-7]
(defcomponent ^:endpoint panel [req ^:long enlargement ^:boolean shuff]
  (let [slides (slideshow/get-slideshow-slides-notes query-fn slideshow_id)
        slides (if shuff (shuffle slides) slides)
        n (-> slides count (/ 2) Math/ceil long)
        enlargement (or enlargement 1)]
    [:div {:hx-target "this"}
     [:div#shuffle.hidden
      {:hx-get "panel"
       :hx-vals {:enlargement enlargement
                 :shuff true}}]
     (if (empty? slides)
       [:div.p-6.text-xl "Empty"]
       [:div {:class (format "grid grid-rows-2 grid-cols-%s" n)}
        (map-indexed
         (fn [i [[src2 src] note]]
           [:div
            [:img {:src (get-src src)
                   :src2 (get-src src2)
                   :onerror "fixSrc(event.target)"
                   :hx-get "panel"
                   :hx-vals {:enlargement (inc-mod enlargement)}}]
            (when (pos? enlargement)
              [:div.text-center.tracking-wider
               {:style {:font-size (font-size enlargement)}} note])])
         slides)])
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
