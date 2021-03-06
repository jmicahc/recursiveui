(ns recursiveui.core
  (:require [reagent.core :as reagent]
            [recursiveui.data :as data]
            [recursiveui.event :as event]
            [recursiveui.command :as command
             :refer [layout-resize-height]]
            [recursiveui.util :refer [with-paths]]
            [recursiveui.signal :as signal]
            [recursiveui.component :as component]
            [recursiveui.traverse :refer [render init]]
            [cljs.core.async :refer [chan <! >! take! mult put!]]
            [goog.dom :as gdom]
            [goog.events :as gevents]
            [clojure.pprint :refer [pprint]]))



(defonce debug?
  ^boolean js/goog.DEBUG)



(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))



(defn root-component [state]
  (fn [] [:div {:id "root-elem"} (render @state)]))




(defn reload []
  (reagent/render [root-component data/state]
                  (.getElementById js/document "app")))





(defn window-width [] (.-innerWidth js/window))
(defn window-height [] (.-innerHeight js/window))



(defn window-listener [e]
  (let [{:keys [layout/width
                layout/height]
         :as root} @data/state]
    (command/update! command/layout-resize-root 
                     {:node root
                      :delta/dx (- (window-width) width)
                      :delta/dy (- (window-height) height)})))


(gevents/unlisten js/window "resize" window-listener)
(gevents/listen js/window "resize" window-listener)



(defn ^:export main []
  (dev-setup)
  (swap! data/state init)
  (window-listener nil)
  (reload))
