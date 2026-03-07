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
  (mod (inc x) 3))

[:div.grid-rows-2.grid-cols-2]
[:div.grid-cols-3]
[:div.grid-cols-4]
[:div.grid-cols-5]
[:div.grid-cols-6]
[:div.grid-cols-7]
(defcomponent ^:endpoint panel [req ^:long enlargement]
  (let [slides (slideshow/get-slideshow-slides-notes query-fn slideshow_id)
        n (-> slides count (/ 2) long)
        enlargement (or enlargement 0)]
    [:div {:hx-target "this"}
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
            [:div.text-center.tracking-wider
             {:style {:font-size (str (inc enlargement) "em")}} note]])
         slides)])
     [:div {:style {:height "500px"}}]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../drop.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
