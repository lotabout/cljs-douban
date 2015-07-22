(ns ^:figwheel-always cljs-douban.view
  (:require [reagent.core :as reagent :refer [atom]]))

(defn channel-list [channels selected-channel]
  "channels and selected-channel are all reagent atoms"
  (fn []
    [:ul.channel-list
     (for [channel @channels]
       ^{:key (channel "channel_id")}
       [:li (channel "name")])]))
