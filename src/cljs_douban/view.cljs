(ns ^:figwheel-always cljs-douban.view
    (:require [reagent.core :as reagent :refer [atom dom-node]]
              [cljs-douban.model :as model]
              [cljs.core.async :refer [<! chan >!]]
              [dommy.core :as dom :refer-macros [sel1 sel]])
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
       [:audio#player-audio
        {:autoPlay "true"
         :controls "true"
         :src (str (@current-song "url"))
         :on-ended #(go (reset! current-song (<! (model/end-song))))}]
       [:input {:type "button"
                :value "Next Song"
                :on-click #(go (reset! current-song (<! (model/skip-song))))}]
       [:input {:type "button"
                :value "Play/Stop"
                :on-click #(let [player (sel1 :#player-audio)]
                             (if (.-paused player)
                               (.play player)
                               (.pause player)))}]])))
