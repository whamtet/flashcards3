(ns simpleui.flashcards3.web.views.grid
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.views.icons :as icons]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- get-src [x]
  (if (string? x)
    x
    (format "../../../api/local/%s" x)))

(defcomponent panel [req ^:long rows]
  (let [slides (slideshow/get-slideshow-slides query-fn slideshow_id)
        rows (or rows 1)]
    (if (empty? slides)
      "Empty"
      [:div.grid.grid-cols-2
       (for [[_ src] slides]
         [:div.pb-3
          [:img {:src (get-src src)}]
          (repeat rows [:hr {:class "mt-12 border border-black ml-3 w-4/5"}])])])))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
