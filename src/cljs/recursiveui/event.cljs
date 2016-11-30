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



(defn event-loop []
  (go-loop []
    (let [event (<! event-channel)]
      (println event)
      (recur))))
