(ns recursiveui.event
  (:require [recursiveui.data :as data]
            [recursiveui.command :as command
             :refer [layout-resize-height
                     layout-resize-width
                     layout-drag
                     update!]]
            [goog.events :refer [listen unlisten]]))




(def delta-x
  (comp (map (fn [[a b]]
               (let [event-a (:event a)
                     event-b (:event b)
                     delta-x (- (.-clientX event-a) (.-clientX event-b))]
                 (assoc a :delta/dx delta-x))))
        (partition-all 2)))






(def delta-y
  (comp (map (fn [[a b]]
               (let [event-a (:event a)
                     event-b (:event b)
                     delta-y (- (.-clientY event-a) (.-clientY event-b))]
                 (assoc a :delta/dy delta-y))))
        (partition-all 2)))





(def delta-xy
  (comp (map (fn [[a b]]
               (let [event-a (:event a)
                     event-b (:event b)
                     delta-x (- (.-clientX event-a) (.-clientX event-b))
                     delta-y (- (.-clientY event-a) (.-clientY event-b))]
                 (assoc a
                        :delta/dx delta-x
                        :delta/dy delta-y))))
        (partition-all 2)))
