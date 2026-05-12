(ns simpleui.flashcards3.web.views.memory
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.memory :as memory]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- grid-attrs [width]
  {:class "grid"
   :style {:grid-template-rows (format "repeat(%s, minmax(0, 1fr))" (dec width))
           :grid-template-columns (format "repeat(%s, minmax(0, 1fr))" width)
           :height "100vh"}})

(defcomponent ^:endpoint panel [req ^:edn permutation ^:longs revealed ^:long click ^:long width]
  (let [[width permutation] (if width
                              [width permutation]
                              (memory/permutation query-fn slideshow_id))
        [a b] revealed
        [permutation revealed]
        (cond
          (and a b (= (permutation a) (permutation b)))
          [(assoc permutation a nil b nil) #{}]
          (and a b)
          [permutation #{}]
          click
          [permutation (conj (set revealed) click)]
          :else
          [permutation #{}])
        ]
    [:div {:hx-target "this"}
     [:input.include.hidden {:name "width" :value width}]
     [:input.include.hidden {:name "permutation" :value (pr-str permutation)}]
     (for [r revealed]
       [:input.include.hidden {:name "revealed" :value r}])
     [:div (grid-attrs width)
      (map-indexed
       (fn [i [_ src]]
         [:div.border.flex.items-center.justify-center.text-3xl
          (when src
            {:hx-post "panel"
             :hx-include ".include"
             :hx-vals {:click i}})
          (when src
            (if (revealed i)
              [:img {:class "w-full h-full object-contain"
                     :src (components/get-src src)}]
              [:span "?"]))])
       (memory/images query-fn slideshow_id permutation))]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
