(ns simpleui.flashcards3.core
  (:require
   [clojure.tools.logging :as log]
   [integrant.core :as ig]
   [simpleui.flashcards3.config :as config]
   [simpleui.flashcards3.env :refer [defaults]]

    ;; Edges
   [kit.edge.server.undertow]
   [simpleui.flashcards3.web.handler]

    ;; Routes
   [simpleui.flashcards3.web.routes.api]
    [simpleui.flashcards3.web.routes.ui]
    [kit.edge.db.sql.conman]
    [simpleui.flashcards3.migratus])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (fn [thread ex]
   (log/error {:what :uncaught-exception
               :exception ex
               :where (str "Uncaught exception on" (.getName thread))})))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!)))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/expand)
       (ig/init)
       (reset! system)))

(defn -main [& _]
  (start-app)
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (stop-app) (shutdown-agents)))))
