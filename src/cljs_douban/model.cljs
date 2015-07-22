(ns cljs-douban.model
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :refer [<! chan >!]]
            [cljs-douban.rpc :as rpc])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce song-list (atom nil))
(defonce current-channel-id (atom 0))

(defn next-song []
  "get the next song information"
  (let [left (count @song-list)]
    (cond
      ;; fetch new playlist
      (<= left 1) (go
                    (reset! song-list
                             ((<! (rpc/song-op "n" :channel @current-channel-id))
                              "song")))
      ;; fetch new playlist while playing
      (<= left 2) (go (swap! song-list
                            #(into % ((<! (rpc/song-op "p" :channel @current-channel-id))
                                      "song"))))
  
      :else (swap! song-list pop)))
  (peek @song-list))


(defn skip-song []
  (rpc/song-op "s" :sid (peek @song-list))
  (next-song))

(defn end-song []
  (rpc/song-op "e" :sid (peek @song-list))
  (next-song))

(defn rate-song []
  (rpc/song-op "r" :sid (peek @song-list)))

(defn unrate-song []
  (rpc/song-op "u" :sid (peek @song-list)))
