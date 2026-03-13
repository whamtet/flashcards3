(ns simpleui.flashcards3.web.controllers.cache-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [simpleui.flashcards3.web.controllers.cache :as cache]))

(deftest cache-file-test []
  (testing "cache-file"
           (is (-> "https://www.google.com/search.png?hi=ok&old=man"
                   cache/cache-file
                   .getName
                   (= "searchhiokoldman.png")))))

