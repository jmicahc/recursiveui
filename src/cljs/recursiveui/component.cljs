(ns recursiveui.component
  (:require [recursiveui.listeners :as listeners]
            [recursiveui.structure :as structure]
            [recursiveui.command :as command]
            [recursiveui.layout :as layout]
            [recursiveui.element :as elem :refer [conjoin]]
            [cljs.core.async :as async :refer [put! chan pipe]]
            [goog.events :as gevents :refer [listen unlisten]]))




(def resizable-flex-column
  (comp layout/layout-handler
        structure/flex-column
        (fn [x]
          (if (and (empty? (command/layout-nav x))
                   (pos? (peek (:path x))))
            (conjoin x {:tags #{:structure/sidebar-top
                                :sources/resize}
                        :layout/render? true})
            x))))


(def root-drag-handler*
  (memoize (fn [channel]
             (let [xform
                   (mapcat (fn [{:keys [perform-drag? client-x client-y] :as msg}]
                             (if perform-drag?
                               (let [prev (atom [client-x client-y])
                                     
                                     drag-fn (fn [e]
                                               (let [v @prev
                                                     x (.-clientX e)
                                                     y (.-clientY e)]
                                                 (put! channel
                                                       (assoc msg
                                                              :delta-x (- x (v 0))
                                                              :delta-y (- y (v 1))))
                                                 (reset! prev [x y])))]
                                 (listen js/window
                                         "mousemove"
                                         drag-fn)
                                 (listen js/window
                                         "mouseup"
                                         (fn [e]
                                           (unlisten js/window "mousemove" drag-fn)))
                                 [])
                               [msg])))
                   ch (chan 10 xform)]
               (pipe ch channel)
               ch))))



(def root-drag-handler
  (fn [{:keys [channel] :as x}]
    (assoc x :channel (root-drag-handler* channel))))
