(ns simpleui.flashcards3.web.views.play
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- other-randoms [curr max]
  (let [x (range max)]
    (shuffle
     (concat
      (take curr x)
      (drop (inc curr) x)))))

(defn- get-src [x]
  (if (string? x)
    x
    (format "../../../api/local/%s" x)))

[:div.grid-rows-2.grid-cols-2]
[:div.grid-rows-3.grid-cols-3]
[:div.grid-rows-4.grid-cols-4]
(defcomponent panel [req ^:long grid ^:long drop ^:longs randoms]
  (let [slides (cond->> (slideshow/get-slideshow-slides query-fn slideshow_id)
                 drop (clojure.core/drop drop)
                 grid (partition-all (* grid grid)))
        last? (-> slides count dec (= step))
        next-href (if (or (empty? slides) last?)
                    (format "../../../edit/%s/" slideshow_id)
                    (format "../../../play/%s/%s/%s"
                            slideshow_id
                            (inc step)
                            (if grid (str "?grid=" grid) "")))
        edit-href (format "../../../edit/%s/" slideshow_id)
        [random & randoms] (if (empty? randoms)
                             (other-randoms step (count slides))
                             randoms)
        random-href (when (> (count slides) 1)
                      (format "../../../play/%s/%s/%s"
                              slideshow_id
                              random
                              (if grid (str "?grid=" grid) "")))]
    [:div#parent
     [:form.hidden {:hx-get random-href
                    :hx-target "#parent"}
      (for [random randoms]
        [:input {:name "randoms" :value random}])
      [:input#randomLink {:type "submit"}]]
     [:a#editLink.hidden {:href edit-href}]
     [:a {:href next-href}
      (cond
        (empty? slides)
        [:div.p-6.text-xl "Empty"]
        grid
        [:div {:class (format "grid grid-rows-%s grid-cols-%s" grid grid)}
         (for [[_ src] (nth slides step)]
           [:img {:src (get-src src)}])]
        :else
        [:div.flex.justify-center
         [:img {:src (-> slides (nth step) second get-src)}]])]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../../output.css"]
       :js ["../../../random.js"]}
      (-> req (assoc :query-fn query-fn) panel)))))
