(ns recursiveui.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent]
            [recursiveui.data :as data :refer [root-channel]]
            [recursiveui.command :as command :refer [dispatch]]
            [recursiveui.traverse :refer [render]]
            [goog.dom :as gdom]
            [goog.events :as gevents]
            [clojure.pprint :refer [pprint]]
            [cljs.core.async :as async :refer [chan <!]]))



(defonce debug?
  ^boolean js/goog.DEBUG)



(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))


(def root-element (.getElementById js/document "app"))


(defn event-loop []
  (go-loop []
    (let [msg (<! root-channel)]
      (reagent/render (render root-channel (command/dispatch msg))
                      root-element)
      (recur))))


#_(do (dispatch {:event-name :save-state})
    (reagent/render (render root-channel
                            (dispatch {:node-path [:children 0 :children 2]
                                       :event-name :layout-resize-left
                                       :delta-x 30}))
                    root-element))



(defn window-width [] (.-innerWidth js/window))
(defn window-height [] (.-innerHeight js/window))
(defn window-listener [e]
  (swap! data/state
         update-in
         [:children 0]
         (fn [node]
           (command/layout-resize-root
            node
            (window-width)
            (window-height)))))


(gevents/unlisten js/window "resize" window-listener)
(gevents/listen js/window "resize" window-listener)


(defn ^:export main []
  (dev-setup)
  (window-listener nil)
  (event-loop)
  (reagent/render (render root-channel @data/state)
                  root-element))



(main)
