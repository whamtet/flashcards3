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
    "pop" (pop xs)
    xs))

(defcomponent ^:endpoint preview [req
                                  ^:longs images
                                  ^:floats xs
                                  ^:floats ys
                                  ^:float x
                                  ^:float y
                                  command]
  (let [images (slideshow/jtd query-fn slideshow_id images)
        xs (push-pop (or xs []) x command)
        ys (push-pop (or ys []) y command)]
    [:div {:hx-target "this"}
     [:form.hidden {:hx-post "preview:push"}
      [:input#x ]]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../jtd.js"]}
      (-> req (assoc :query-fn query-fn) preview)))))
