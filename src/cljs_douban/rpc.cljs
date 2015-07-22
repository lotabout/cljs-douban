(ns cljs-douban.rpc
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :refer [<! chan >!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn login [username password]
  "return a chanel containing the response of login"
  (let [ch (chan)]
    (POST "http://www.douban.com/j/app/login"
          {:format :raw
           :params {"app_name" "radio_desktop_win"
                    "version" 100
                    "email" (str username)
                    "password" (str password)}
           :response-format :json
           :headers {"Content-Type" "application/x-www-form-urlencoded"}
           :handler (fn [response]
                      (go (>! ch response)))})
    ch))

(defn get-channel-list []
  "return a core.async channel containing the channel list"
  (let [ch (chan)]
    (GET "http://www.douban.com/j/app/radio/channels"
         {:response-format :json
          :handler (fn [response]
                     (go (>! ch response)))})
    ch))

(defn song-op [type & {:keys [user_id expire token sid h channel]
                       :as params
                       :or {user_id nil, expire nil, token, nil, sid nil, h nil, channel nil}}]
  "do operation on songs"
  (let [ch (chan)
        params (reduce conj
                       {"app_name" "radio_desktop_win", "version" 100, "type" type}
                       (map (fn [[k v]] [(name k) v]) (filter #(-> % val) params)))]
    (GET "http://www.douban.com/j/app/radio/people"
         {:response-format :json
          :params params
          :handler (fn [response]
                     (go (>! ch response)))})
    ch))
