(ns simpleui.flashcards3.web.routes.ui
  (:require
   [simpleui.flashcards3.web.middleware.exception :as exception]
   [simpleui.flashcards3.web.middleware.formats :as formats]
   [simpleui.flashcards3.web.views.edit :as edit]
   [simpleui.flashcards3.web.views.grid :as grid]
   [simpleui.flashcards3.web.views.home :as home]
   [simpleui.flashcards3.web.views.play :as play]
   [simpleui.flashcards3.web.views.fill :as fill]
   [simpleui.flashcards3.web.views.students :as students]
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

(defn route-data [opts]
  (merge
   opts
   {:muuntaja   formats/instance
    :middleware
    [;; Default middleware for ui
    ;; query-params & form-params
      parameters/parameters-middleware
      ;; encoding response body
      muuntaja/format-response-middleware
      ;; exception handling
      exception/wrap-exception]}))

(derive :reitit.routes/ui :reitit/routes)

(defmethod ig/init-key :reitit.routes/ui
  [_ opts]
  [["" (route-data opts) (home/ui-routes opts)]
   ["/fill" (route-data opts) (fill/ui-routes opts)]
   ["/students" (route-data opts) (students/ui-routes opts)]
   ["/edit/:slideshow_id" (route-data opts) (edit/ui-routes opts)]
   ["/grid/:slideshow_id" (route-data opts) (grid/ui-routes opts)]
   ["/play/:slideshow_id/:step" (route-data opts) (play/ui-routes opts)]])
