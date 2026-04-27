(ns simpleui.flashcards3.web.views.jtd
  (:require
    [simpleui.core :as simpleui]
    [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
    [simpleui.flashcards3.web.views.components :as components]
    [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- push-pop [xs x command]
  (case command
    "push" (conj xs x)
    "pop" (if (empty? xs) xs (pop xs))
    xs))

(def width "500px")
(def height "400px")
(def height2 "500px")
(def full-width "1000px")

(defn- img-disp [[_ src]]
  [:img {:class "print:hidden absolute"
         :style {:width width :height height :pointer-events "none"}
         :src (components/get-src src)}])

(defn- point [i [x y]]
  [:g
   [:circle {:cx (str x "%")
             :cy (str y "%")
             :r 4
             :fill "red"}]
   [:text {:x (str x "%")
           :y (str y "%")
           :dy "-0.5em"          ;; lift text slightly above the point
           :text-anchor "middle" ;; center horizontally
           :font-size 12
           :fill "black"}
    (str (inc i))]])

(defn- flex-item [is xs ys]
  (fn [j img]
    [:div.relative.border-b-2.border-black {:style {:width width :height height2}}
     (img-disp img)
     [:svg {:width width
            :height height
            :style "position:absolute;top:0;left:0;"
            :onclick (format "click(%s, event)" j)}
      (->> (map
            (fn [i x y]
              (when (= i j) [x y])) is xs ys)
           (filter identity)
           (map-indexed point))]]))

(defcomponent ^:endpoint preview [req
                                  ^:longs images
                                  ^:longs is
                                  ^:doubles xs
                                  ^:doubles ys
                                  ^:long i
                                  ^:double x
                                  ^:double y
                                  command]
  (let [images (or (not-empty images) (slideshow/jtd-images query-fn slideshow_id))
        srcs (slideshow/jtd query-fn slideshow_id images)
        is (push-pop (vec is) i command)
        xs (push-pop (vec xs) x command)
        ys (push-pop (vec ys) y command)]
    [:div {:hx-target "this"}
     (for [image images]
       [:input.preview.hidden {:name "images" :value image}])
     (for [i is]
       [:input.preview.hidden {:name "is" :value i}])
     (for [x xs]
       [:input.preview.hidden {:name "xs" :value x}])
     (for [y ys]
       [:input.preview.hidden {:name "ys" :value y}])
     [:form.hidden {:hx-post "preview:push"
                    :hx-include ".preview"}
      [:input#i {:name "i"}]
      [:input#x {:name "x"}]
      [:input#y {:name "y"}]
      [:input#push {:type "submit"}]]
     [:form.hidden {:hx-post "preview:pop"
                    :hx-include ".preview"}
      [:input#pop {:type "submit"}]]
     [:div.flex.flex-wrap {:style {:width full-width}}
      (map-indexed (flex-item is xs ys) srcs)]]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]
       :js ["../../jtd.js"]}
      (-> req (assoc :query-fn query-fn) preview)))))
