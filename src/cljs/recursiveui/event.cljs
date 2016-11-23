(ns recursiveui.event
  (:require [recursiveui.data :as data]
            [recursiveui.element :as elem :refer [attr]]
            [recursiveui.command :as command
             :refer [layout-resize-height
                     layout-resize-width
                     layout-drag
                     update!]]
            [cljs.core.async :refer [chan pipe]]
            [goog.events :refer [listen unlisten]]))




(defn delta-event-fn [{:keys [event node] :as m}]
  (let [x (.-clientX event)
        y (.-clientY event)
        prev (:delta/prev node)
        value @prev]
    (reset! prev [x y])
    (assoc m
           :delta/dx (- x (value 0))
           :delta/dy (- y (value 1)))))




(defn delta-update [f]
  (fn [rf]
    (fn
      ([] (rf))
      ([node] (rf (assoc node :delta/prev (atom [0 0]))))
      ([node elem]
       (letfn [(drag-listener [event]
                 (->>  {:event event :node node}
                       delta-event-fn
                       (update! f)))
               (unlisten-f [e]
                 (unlisten js/window
                           "mousemove"
                           drag-listener))]
         (rf node
             (attr elem
                   :onMouseDown
                   (fn [e]
                     (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
                     (listen js/window "mouseup" unlisten-f)
                     (listen js/window "mousemove" drag-listener)))))))))




(def layout-resize-delta
  (let [render-delta-x ((delta-update layout-resize-width) (fn [a b] b))
        render-delta-y ((delta-update layout-resize-height) (fn [a b] b))]
    (fn [rf]
      (fn
        ([] (rf))
        ([node] (rf (assoc node :delta/prev (atom [0 0]))))
        ([node elem]
         (if (= partition :column)
           (render-delta-x node elem)
           (render-delta-y node elem)))))))
