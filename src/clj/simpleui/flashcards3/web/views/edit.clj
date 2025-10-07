(ns simpleui.flashcards3.web.views.edit
    (:require
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.img-search :as img-search]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]))

(defcomponent ^:endpoint name-editor [req new-name command]
  (case command
    "update" (when (not-empty new-name)
               (slideshow/update-slideshow-name query-fn slideshow_id new-name)
               nil)
    "delete" (do
               (slideshow/delete-slideshow query-fn slideshow_id)
               {:hx-redirect "/"})
    (let [slideshow-name (slideshow/get-slideshow-name query-fn slideshow_id)]
      [:div.p-2.flex
       [:input {:class "p-2 rounded-md border mr-2"
                :hx-post "name-editor:update"
                :name "new-name"
                :value slideshow-name}]
       [:div {:hx-delete "name-editor:delete"
              :hx-confirm (format "Delete %s?" slideshow-name)}
        (components/button-warning "Delete")]])))

(defcomponent ^:endpoint image-search [req q]
  [:div {:class "p-2"
         :hx-target "this"}
   (when q
     [:div.flex.overflow-x-auto
      (for [[medium large] (img-search/get-pics q)]
        [:img {:class "mr-1"
               :src medium}])])
   [:form {:hx-get "image-search"}
    [:input {:class "p-2 rounded-md border"
             :placeholder "Search and hit enter..."
             :name "q"}]]])

(defcomponent ^:endpoint panel [req ^:prompt slideshow-name command]
  (case command
    [:div.p-2
     (name-editor req)
     [:hr.border-top]
     (image-search req)]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["/output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
