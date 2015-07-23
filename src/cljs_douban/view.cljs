(ns ^:figwheel-always cljs-douban.view
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs-douban.model :as model]
              [cljs.core.async :refer [<! chan >!]])
    (:require-macros [cljs.core.async.macros :refer [go]]))

(defn channel-list [channels selected-channel]
  "channels and selected-channel are all reagent atoms"
  (fn []
    [:ul.channel-list
     (for [channel @channels]
       ^{:key (channel "channel_id")}
       [:li (channel "name")])]))

(defn song-player []
  "display songs and controls"
  (let [current-song (atom {})]
    (fn []
      [:div#player
       [:p "title: " (@current-song "title")]
       [:audio#player-audio {:autoplay "true"
                             :controls "true"
                             :src (str (@current-song "url"))}]
       [:input {:type "button"
                :value "Next Song"
                :on-click #(go (reset! current-song (<! (model/next-song)))
                               (let [player (. js/document (getElementById "player-audio"))]
                                 (set! (.-autoplay player) true)))}]
       [:input {:type "button"
                :value "Play/Stop"
                :on-click #(let [player (. js/document (getElementById "player-audio"))]
                             (if (.-paused player)
                               (.play player)
                               (.pause player)))}]])))
