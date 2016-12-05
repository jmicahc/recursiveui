(ns recursiveui.component
  (:require [recursiveui.listeners :as listeners]
            [recursiveui.structure :as structure]
            [recursiveui.layout :as layout]
            [recursiveui.element :as elem :refer [conjoin]]
            [cljs.core.async :as async :refer [put! chan pipe]]
            [goog.events :as gevents :refer [listen unlisten]]))


(def resizable-flex-root
  (comp structure/flex-root
        layout/layout-handler
        layout/layout-root-handler
        (fn [x]
          (conjoin x
                   {:tags #{:structure/flex-root-action-bar}
                    :traverse/render? true
                    :children [{:tags #{:structure/action-button}
                                :traverse/render? true}]}
                   {:tags #{:structure/sidebar-top
                            :sources/layout-resize-top}
                    :traverse/render? true}
                   {:tags #{:structure/sidebar-left
                            :sources/layout-resize-left}
                    :traverse/render? true}
                   {:tags #{:structure/sidebar-right
                            :sources/layout-resize-right}
                    :traverse/render? true}
                   {:tags #{:structure/sidebar-bottom
                            :sources/layout-resize-bottom}
                    :traverse/render? true}))))





(def root-drag-handler*
  (memoize
   (fn [channel]
     (let [xform (mapcat (fn [{:keys [perform-drag? client-x client-y] :as msg}]
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
       (println "hello world")
       (pipe ch channel)
       ch))))


(def root-drag-handler
  (fn [x] (assoc  x :channel (root-drag-handler* (:channel x))))
  #_(fn [{:keys [channel] :as x}]
    (let [xform (mapcat (fn [{:keys [perform-drag? client-x client-y] :as msg}]
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
      (assoc x :channel ch))))
