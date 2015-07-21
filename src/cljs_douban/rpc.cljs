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
