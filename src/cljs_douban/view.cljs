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


(def current-song (atom {}))

(defn song-player-plain []
  "display songs and controls"
  (fn []
    [:div#player
     [:p "title: " (@current-song "title")]
     [:audio#player-audio
      {:autoPlay "true"
       :controls "true"
       :src (str (@current-song "url"))}]
     [:input {:type "button"
              :value "Next Song"
              :on-click #(go (reset! current-song (<! (model/skip-song))))}]
     [:input {:type "button"
              :value "Play/Stop"
              :on-click #(let [player (sel1 :#player-audio)]
                           (if (.-paused player)
                             (.play player)
                             (.pause player)))}]]))

;;; react do not support 'onended' event and thus should manually bind it.
;;; and this is not very 'reagent'-ish
;;; Also I had to put 'current-song' into global in order to refer to it.
(def song-player
  (with-meta song-player-plain
    {:component-did-mount
     (fn [this]
       (dom/listen! (sel1 :#player-audio) :ended #(go (reset! current-song (<! (model/end-song))))))}))
