(ns simpleui.flashcards3.web.routes.open
  (:require
    [simpleui.flashcards3.web.middleware.exception :as exception]
    [simpleui.flashcards3.web.middleware.formats :as formats]
    [simpleui.flashcards3.web.views.fill :as fill]
    [simpleui.flashcards3.web.views.students :as students]
    [simpleui.flashcards3.web.views.intro :as intro]
    [simpleui.flashcards3.web.views.snl :as snl]
    [simpleui.flashcards3.web.controllers.snl :as controllers.snl]
    [simpleui.flashcards3.web.controllers.students :as controllers.students]
    [integrant.core :as ig]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]))

(defn route-data [opts]
  (merge
   opts
   {:muuntaja   formats/instance
    :middleware
    [;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/open :reitit/routes)

(defmethod ig/init-key :reitit.routes/open
  [_ opts]
  [["" (route-data opts) (intro/ui-routes opts)]
   ["/fill" (route-data opts) (fill/ui-routes opts)]
   ["/api/students" controllers.students/parse]
   ["/api/snl" controllers.snl/parse]
   ["/snl" (route-data opts) (snl/ui-routes opts)]
   ["/students" (route-data opts) (students/ui-routes opts)]])
