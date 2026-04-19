(ns simpleui.flashcards3.web.views.play
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :refer [get-src] :as components]
    [simpleui.flashcards3.web.views.icons :as icons]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- other-randoms [curr max]
  (let [x (range max)]
    (shuffle
     (concat
      (take curr x)
      (drop (inc curr) x)))))

(defn- reveal-disp [next-href src2 src note]
  [:div
   [:div.flex.justify-center
    [:a {:href next-href}
     [:img {:src (get-src src)
            :src2 (get-src src2)
            :style {:height "80vh"}
            :onerror "fixSrc(event.target)"}]]]
   [:div.text-center.pt-8.opacity-0.transition-opacity.duration-500.text-xl
    {:_ "on click remove .opacity-0"}
    note]])

(defn- suffix [grid reveal]
  (cond
    grid (str "?grid=" grid)
    reveal "?reveal=true"
    :else ""))

[:div.grid-rows-2.grid-cols-2]
[:div.grid-rows-3.grid-cols-3]
[:div.grid-rows-4.grid-cols-4]
(defcomponent panel [req ^:long grid ^:long drop ^:longs randoms ^:boolean reveal]
  (let [slides (cond->> (slideshow/get-slideshow-slides query-fn slideshow_id)
                 drop (clojure.core/drop drop)
                 grid (partition-all (* grid grid)))
        last? (-> slides count dec (= step))
        next-href (if (or (empty? slides) last?)
                    (format "../../../edit/%s/" slideshow_id)
                    (format "../../../play/%s/%s/%s"
                            slideshow_id
                            (inc step)
                            (suffix grid reveal)))
        edit-href (format "../../../edit/%s/" slideshow_id)
        [random & randoms] (if (empty? randoms)
                             (other-randoms step (count slides))
                             randoms)
        random-href (when (> (count slides) 1)
                      (format "../../../play/%s/%s/%s"
                              slideshow_id
                              random
                              (suffix grid reveal)))]
    [:div#parent
     [:form.hidden {:hx-get random-href
                    :hx-target "#parent"}
      (for [random randoms]
        [:input {:name "randoms" :value random}])
      [:input#randomLink {:type "submit"}]]
     [:a#editLink.hidden {:href edit-href}]
     (cond
       (empty? slides)
       [:a {:href next-href}
        [:div.p-6.text-xl "Empty"]]
       grid
       [:a {:href next-href}
        [:div {:class (format "grid grid-rows-%s grid-cols-%s" grid grid)}
         (for [[src2 src] (nth slides step)]
           [:img {:src (get-src src)
                  :src2 (get-src src2)
                  :onerror "fixSrc(event.target)"}])]]
       :else
       (let [[src2 src] (nth slides step)
             note (nth (slideshow/get-slideshow-notes query-fn slideshow_id) step)]
         (if reveal
           (reveal-disp next-href src2 src note)
           [:a {:href next-href}
            [:div.flex.justify-center
             [:img {:src (get-src src)
                    :src2 (get-src src2)
                    :style {:max-width "1000px"}
                    :onerror "fixSrc(event.target)"}]]])))]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../../output.css"]
       :js ["../../../random.js"]
       :hyperscript? true}
      (-> req (assoc :query-fn query-fn) panel)))))
