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

(defn- skip-images [images skip-i]
  (if skip-i
    (concat (take skip-i images) (drop (inc skip-i) images))
    images))

(defn update-i [skip-i]
  (fn [i x y]
    (cond
      (< i skip-i) [i x y]
      (< skip-i i) [(dec i) x y])))
(defn- third [x]
  (x 2))
(defn- update-preview [is xs ys skip-i]
  (if skip-i
    (let [joined (filter identity (map (update-i skip-i) is xs ys))]
      [(map first joined) (map second joined) (map third joined)])
    [is xs ys]))

(def width "350px")
(def height "300px")
(def height2 "350px")
(def full-width "700px")

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
            :oncontextmenu (format "rightclick(%s, event)" j)
            :onclick (format "click(%s, event)" j)}
      (->> (map
            (fn [i x y]
              (when (= i j) [x y])) is xs ys)
           (filter identity)
           (map-indexed point))]
     [:div.absolute.left-2.top-2.text-2xl (inc j)]]))

(defcomponent ^:endpoint preview [req
                                  ^:longs images
                                  ^:longs is
                                  ^:doubles xs
                                  ^:doubles ys
                                  ^:long skip-i
                                  ^:long i
                                  ^:double x
                                  ^:double y
                                  command]
  (let [images (or (not-empty images) (slideshow/jtd-images query-fn slideshow_id))
        images (skip-images images skip-i)
        srcs (slideshow/jtd query-fn slideshow_id images)
        words (slideshow/jtd-words query-fn slideshow_id images)
        is (push-pop (vec is) i command)
        xs (push-pop (vec xs) x command)
        ys (push-pop (vec ys) y command)
        [is xs ys] (update-preview is xs ys skip-i)]
    [:div {:hx-target "this"}
     [:form.hidden {:method "POST"
                    :target "_blank"
                    :action "../../api/pdf-jtd"}
      [:input {:name "words" :value (->> words (take 6) vec pr-str)}]
      (for [image images]
        [:input.preview {:name "images" :value image}])
      (for [i is]
        [:input.preview {:name "is" :value i}])
      (for [x xs]
        [:input.preview {:name "xs" :value x}])
      (for [y ys]
        [:input.preview {:name "ys" :value y}])
      [:input#pdf {:type "submit"}]]
     [:form.hidden {:hx-post "preview:push"
                    :hx-include ".preview"}
      [:input#i {:name "i"}]
      [:input#x {:name "x"}]
      [:input#y {:name "y"}]
      [:input#push {:type "submit"}]]
     [:form.hidden {:hx-post "preview:pop"
                    :hx-include ".preview"}
      [:input#pop {:type "submit"}]]
     [:form.hidden {:hx-delete "preview:skip"
                    :hx-include ".preview"}
      [:input#skip-i {:name "skip-i"}]
      [:input#skip {:type "submit"}]]
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
