(ns simpleui.flashcards3.web.views.jtd
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.pdf-jtd :as pdt-jtd]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- push-pop [xs x command]
  (case command
    "push" (conj xs x)
    "pop" (if (empty? xs) xs (pop xs))
    xs))

(defn- img [[_ src]]
  [:img {:class "w-1/2"
         :style {:height "50vh"}
         :src (components/get-src src)}])

(defcomponent ^:endpoint preview [req
                                  ^:longs images
                                  ^:doubles xs
                                  ^:doubles ys
                                  ^:double x
                                  ^:double y
                                  command]
  (let [images (or (not-empty images) (slideshow/jtd-images query-fn slideshow_id))
        srcs (slideshow/jtd query-fn slideshow_id images)
        xs (push-pop (vec xs) x command)
        ys (push-pop (vec ys) y command)]
    [:div {:hx-target "this"}
     (for [image images]
       [:input.hidden.preview {:name "images" :value image}])
     (for [x xs]
       [:input.hidden.preview {:name "xs" :value x}])
     (for [y ys]
       [:input.hidden.preview {:name "ys" :value y}])
     [:form.hidden {:hx-post "preview:push"
                    :hx-include ".preview"}
      [:input#x {:name "x"}]
      [:input#y {:name "y"}]
      [:input#push {:type "submit"}]]
     [:form.hidden {:hx-post "preview:pop"
                    :hx-include ".preview"}
      [:input#pop {:type "submit"}]]
     [:div#screen.flex.flex-wrap
      (map img srcs)]
     [:script "setTimeout(() => listen(), 100)"]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../jtd.js"]}
      (-> req (assoc :query-fn query-fn) preview)))))
