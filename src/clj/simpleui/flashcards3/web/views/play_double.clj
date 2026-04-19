(ns simpleui.flashcards3.web.views.play-double
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :refer [get-src] :as components]
    [simpleui.flashcards3.web.views.icons :as icons]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- pluralize [[a b c]]
  (shuffle
   (if (zero? (rand-int 2))
     [[a a] [b] [c]]
     [[a a] [b b] [c]])))

(defn- img-div [i [[src2 src] x]]
  (if x
    [:div {:class "flex border-4 border-black w-1/3"}
     (repeat 2
             [:img {:class "max-h-full w-1/2 object-contain"
                    :src (get-src src)
                    :src2 (get-src src2)
                    :onerror "fixSrc(event.target)"}])]
    [:img {:class "max-h-full object-contain w-1/3"
           :src (get-src src)
           :src2 (get-src src2)
           :onerror "fixSrc(event.target)"}]))

(defn- text-div [inc]
  (fn [i _]
    [:div {:class "text-center tracking-wider text-3xl w-1/3"} (+ inc i)]))

(defn- half-column [inc slides]
  [:div.flex.flex-col {:style {:height "50vh"}}
   [:div.flex.flex-1.min-h-0 (map-indexed img-div slides)]
   [:div.flex (map-indexed (text-div inc) slides)]])

(defcomponent ^:endpoint panel [req]
  (let [slides (shuffle (slideshow/get-slideshow-slides query-fn slideshow_id))]
    (if (empty? slides)
      [:div.p-6.text-xl "Empty"]
      [:div
       (->> slides (take 3) pluralize (half-column 1))
       (->> slides (drop 3) pluralize (half-column 4))
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
