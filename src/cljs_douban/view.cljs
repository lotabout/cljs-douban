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

;;; well, it seems that I can still hide the atom 'current-song'
;;; why the (with-meat func meta-data) works and not (fn [] ^{meta-data} ;;; body)?
(def song-player
  "display songs and controls"
  (let [current-song (atom {})]
    (with-meta
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
                                 (.pause player)))}]])
      {:component-did-mount
       (fn [this]
         (dom/listen! (sel1 :#player-audio) :ended #(go (reset! current-song (<! (model/end-song))))))})))

