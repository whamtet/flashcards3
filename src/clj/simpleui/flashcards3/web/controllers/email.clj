(ns simpleui.flashcards3.web.controllers.email
  (:require
    [cognitect.aws.client.api :as aws]))

(def ses (aws/client {:api :sesv2
                      :region "ap-southeast-2"}))

(defn send-mail [from to subject body]
  (aws/invoke ses
              {:op :SendEmail
               :request {:FromEmailAddress from
                         :Destination {:ToAddresses [to]}
                         :Content {:Simple {:Subject {:Data subject}
                                            :Body {:Text {:Data body}}}}}}))

(defn send-params [params]
  (send-mail "contact@acastream.uk"
             "matthew@molloy.link"
             "ACASTREAM FORM SUBMISSION"
             (pr-str params)))
