(ns ^:figwheel-always cljs-douban.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [cljs.core.async :refer [<! chan >!]]
            [cljs-douban.rpc :as rpc]
            [cljs-douban.view :as view]
            [cljs-douban.model :as model])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn hello-world []
  [:h1 (:text @app-state)])
(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def username "test")
(def password "test")

#_(let [ch (rpc/login username password)]
  (go (let [response-map (<! ch)]
        (.log js/console (response-map "user_id"))
        (.log js/console (response-map "err"))
        (.log js/console (response-map "token"))
        (.log js/console (response-map "expire"))
        (.log js/console (response-map "r"))
        (.log js/console (response-map "user_name"))
        (.log js/console (response-map "email")))))

(def channels (atom nil))
(def selected-channel (atom nil))

;;; get channel list
(go (let [channel-list (<! (rpc/get-channel-list))]
      (reset! channels (channel-list "channels"))))

(defn main-app []
  (fn []
    [:div#main
     [view/channel-list channels selected-channel]
     [view/song-player]]))

(reagent/render-component [main-app] (. js/document (getElementById "app")))
