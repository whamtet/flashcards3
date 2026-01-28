(ns simpleui.flashcards3.web.views.edit
    (:require
      [clojure.string :as string]
      [simpleui.core :as simpleui]
      [simpleui.flashcards3.web.controllers.img-search :as img-search]
      [simpleui.flashcards3.web.controllers.slideshow :as slideshow]
      [simpleui.flashcards3.web.controllers.local :as local]
      [simpleui.flashcards3.web.views.components :as components]
      [simpleui.flashcards3.web.views.icons :as icons]
      [simpleui.flashcards3.web.htmx :refer [page-htmx defcomponent]]
      [simpleui.flashcards3.util :as util]))

(defcomponent ^:endpoint name-editor [req new-name command]
  (case command
    "update" (when (not-empty new-name)
               (slideshow/update-slideshow-name query-fn slideshow_id new-name)
               nil)
    "delete" (do
               (slideshow/delete-slideshow query-fn slideshow_id)
               {:hx-redirect "../.."})
    (let [slideshow-name (slideshow/get-slideshow-name query-fn slideshow_id)]
      [:div.p-2.flex.items-center
       [:a.mr-2 {:href "../.."} icons/home]
       [:input {:class "p-2 rounded-md border mr-2"
                :hx-post "name-editor:update"
                :name "new-name"
                :value slideshow-name}]
       [:div {:class "mr-2"
              :hx-delete "name-editor:delete"
              :hx-confirm (format "Delete %s?" slideshow-name)}
        (components/button-warning "Delete")]
       [:a {:class "mr-2"
            :href (format "../../api/pdf/%s" slideshow_id)
            :target "_blank"}
        (components/button "Download PDF")]
       [:a {:class "mr-2"
            :href (format "../../grid/%s/" slideshow_id)
            :target "_blank"}
        (components/button "Grid Printout")]
       [:a {:href "../../white.html"
            :target "_blank"}
        (components/button "White Screen")]])))

(defn- get-src [x]
  (if (string? x)
    x
    (format "../../api/local/%s" x)))

(defcomponent ^:endpoint image-order [req command medium large ^:long i ^:array images]
  (case command
    "concat" (slideshow/concat-slideshow query-fn slideshow_id images)
    "conj" (slideshow/conj-slideshow query-fn slideshow_id [(or medium large) large])
    "up" (slideshow/up-slideshow query-fn slideshow_id i)
    "down" (slideshow/down-slideshow query-fn slideshow_id i)
    "del" (slideshow/delete-slide query-fn slideshow_id i)
    nil)
  [:div#images
   [:div.flex.m-2
    [:form {:hx-post "image-order:conj"
            :hx-target "#images"}
     [:input {:class "p-2 rounded-md border mr-2 w-96"
              :placeholder "Direct URL"
              :name "large"}]]
    [:input {:hx-post "image-order:concat"
             :hx-encoding "multipart/form-data"
             :hx-target "#images"
             :type "file"
             ;; corresponds to java 21
             :accept (string/join ", " local/supported-types)
             :multiple true
             :name "images"}]]
   (util/map-first-last
    (fn [i first? last? [medium]]
      [:div.flex.items-center.mb-1
       [:span.text-xl.mr-1 (format "%s)" (inc i))]
       (when-not first?
         [:div {:class "cursor-pointer border rounded-md mr-2 p-2"
                :hx-post "image-order:up"
                :hx-target "#images"
                :hx-vals {:i i}}
          icons/arrow-up])
       (when-not last?
         [:div {:class "cursor-pointer border rounded-md mr-2 p-2"
                :hx-post "image-order:down"
                :hx-target "#images"
                :hx-vals {:i i}}
          icons/arrow-down])
       [:a {:class "mr-2"
            :href (format "../../play/%s/%s/" slideshow_id i)}
        [:img {:class "max-h-96"
               :src (get-src medium)}]]
       [:div {:class "cursor-pointer border rounded-md p-2"
              :hx-post "image-order:del"
              :hx-target "#images"
              :hx-confirm "Delete pic?"
              :hx-vals {:i i}}
        icons/trash]])
    (slideshow/get-slideshow-slides query-fn slideshow_id))])

(defcomponent ^:endpoint image-search [req q]
  [:div {:class "p-2"
         :hx-target "this"}
   (when q
     [:div.flex.overflow-x-auto.mb-2
      (for [[medium large] (img-search/get-pics q)]
        [:img {:class "mr-1 cursor-pointer"
               :src medium
               :hx-post "image-order:conj"
               :hx-target "#images"
               :hx-vals {:medium medium :large large}
               }])])
   [:form {:hx-get "image-search"}
    [:input {:class "p-2 rounded-md border"
             :placeholder "Search photos..."
             :name "q"}]]])

(defcomponent ^:endpoint icon-search [req q]
  [:div {:class "p-2"
         :hx-target "this"}
   (when q
     [:div.flex.overflow-x-auto.mb-2
      (for [[medium large] (img-search/get-icons q)]
        [:img {:class "mr-1 cursor-pointer"
               :src medium
               :hx-post "image-order:conj"
               :hx-target "#images"
               :hx-vals {:medium medium :large large}
               }])])
   [:form {:hx-get "icon-search"}
    [:input {:class "p-2 rounded-md border"
             :placeholder "Search icons..."
             :name "q"}]]])

(defcomponent ^:endpoint notes [req notes command]
  (case command
    "update" (do
               (slideshow/update-slideshow-notes query-fn slideshow_id notes)
               nil)
    [:textarea {:class "w-4/5 ml-2 mt-2 p-2 rounded-md border"
                :placeholder "Notes..."
                :name "notes"
                :rows 10
                :hx-post "notes:update"}
     (slideshow/get-slideshow-notes query-fn slideshow_id)]))

(defcomponent ^:endpoint panel [req ^:prompt slideshow-name command]
  (case command
    [:div.p-2
     (name-editor req)
     [:hr.border-top]
     (image-order req)
     (image-search req)
     (icon-search req)
     [:hr.border-top]
     (notes req)]))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (page-htmx
      {:css ["../../output.css"]}
      (-> req (assoc :query-fn query-fn) panel)))))
