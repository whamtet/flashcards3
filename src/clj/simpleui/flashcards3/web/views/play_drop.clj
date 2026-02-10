(ns simpleui.flashcards3.web.views.play-drop
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.views.icons :as icons]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- get-src [x]
  (if (string? x)
    x
    (format "../../api/local/%s" x)))

[:div.grid-rows-2.grid-cols-2]
[:div.grid-rows-3.grid-cols-3]
[:div.grid-rows-4.grid-cols-4]
(defcomponent ^:endpoint panel [req ^:long grid ^:longs exclusions ^:long exclusion ^:boolean drop]
  (let [slides (slideshow/get-slideshow-slides query-fn slideshow_id)
        exclusions (cond-> (seq exclusions)
                     drop rest
                     exclusion (conj exclusion))
        exclude? (set exclusions)
        grid (or grid 3)]
    [:div {:hx-target "this"}
     [:div#drop.hidden {:hx-post "panel"
                        :hx-include ".exclusions"
                        :hx-vals {"drop" true}}]
     (for [exclusion exclusions]
       [:input.exclusions {:type "hidden" :name "exclusions" :value exclusion}])
     (if (empty? slides)
       [:div.p-6.text-xl "Empty"]
       [:div {:class (format "grid grid-rows-%s grid-cols-%s" grid grid)}
        (keep-indexed
         (fn [i [_ src]]
           (when-not (exclude? i)
             [:img.cursor-pointer {:src (get-src src)
                                   :hx-post "panel"
                                   :hx-include ".exclusions"
                                   :hx-vals {"exclusion" i}}]))
         slides)])]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../drop.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
