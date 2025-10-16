(ns simpleui.flashcards3.web.views.play
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- other-random [curr max]
  (let [r (rand-int (dec max))]
    (if (< r curr) r (inc r))))

[:div.grid-rows-2.grid-cols-2]
[:div.grid-rows-3.grid-cols-3]
[:div.grid-rows-4.grid-cols-4]
(defcomponent panel [req ^:long grid]
  (let [slides (slideshow/get-slideshow-slides query-fn slideshow_id)
        slides (if grid
                 (partition-all (* grid grid) slides)
                 slides)
        last? (-> slides count dec (= step))
        next-href (if (or (empty? slides) last?)
                    "../../.."
                    (format "../../../play/%s/%s/%s"
                            slideshow_id
                            (inc step)
                            (if grid (str "?grid=" grid) "")))
        random-href (when (> (count slides) 1)
                      (format "../../../play/%s/%s/%s"
                              slideshow_id
                              (other-random step (count slides))
                              (if grid (str "?grid=" grid) "")))]
    [:div
     [:a#randomLink.hidden {:href random-href}]
     [:a {:href next-href}
      (cond
        (empty? slides)
        [:div.p-6.text-xl "Empty"]
        grid
        [:div {:class (format "grid grid-rows-%s grid-cols-%s" grid grid)}
         (for [[_ src] (nth slides step)]
           [:img {:src src}])]
        :else
        [:div.flex.justify-center
         [:img {:src (-> step slides second)}]])]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../../output.css"]
       :js ["../../../random.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
