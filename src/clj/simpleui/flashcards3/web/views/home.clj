(ns simpleui.flashcards3.web.views.home
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defn- slideshow-disp [{:keys [slideshow_id slideshow_name]}]
  [:div.p-2.text-2xl.flex.items-center
   [:a.text-clj-blue.mr-2 {:href (format "/edit/%s/" slideshow_id)}
    slideshow_name]
   [:a {:href (format "/play/%s/0" slideshow_id)}
    icons/play-circle]])

(defcomponent ^:endpoint panel [req ^:prompt slideshow-name command]
  (case command
    "new" (when slideshow-name
            (slideshow/add-slideshow query-fn slideshow-name)
            :refresh)
    [:div.p-2
     [:div {:class "my-1"
            :hx-post "panel:new"
            :hx-prompt "New Slideshow Name"}
      (components/button "New Slideshow")]
     (map slideshow-disp (slideshow/get-slideshows query-fn))]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["/output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
