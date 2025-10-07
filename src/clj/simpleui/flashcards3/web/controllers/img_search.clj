(ns simpleui.flashcards3.web.controllers.img-search
  (:require
    [clj-http.lite.client :as client]
    [simpleui.flashcards3.util :as util :refer [defm]]))

(def api-key (System/getenv "IMG_API_KEY"))

(defn- get-src [{{:keys [medium large2x]} :src}]
  [medium large2x])

(defm get-pics [q]
  (->>
   (client/get "https://api.pexels.com/v1/search"
               {:query-params {:query q}
                :headers {"Authorization" api-key}})
   :body
    util/read-str
   :photos
    (map get-src)))

