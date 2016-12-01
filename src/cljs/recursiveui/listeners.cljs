(ns recursiveui.listeners
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [chan put! <!]]
            [recursiveui.command :as command]
            [goog.events :as gevents :refer [listen unlisten]]))


(def channel (chan 10))



(defn layout-resize-source [node]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (.persist e)
            (aset e "eventname" :layout/resize))))




(defn layout-resize-handler
  [{:keys [layout/partition] :as node}]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (when (= (.-eventname e) :layout/resize)
              (put! channel
                    {:node node
                     :event e
                     :name :drag
                     :command (if (= partition :row)
                                command/layout-resize-height
                                command/layout-resize-width)})))))





(defn layout-resize-root-handler
  [node]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (.persist e)
            (when-let [f (case (.-eventname e)
                           :layout/resize-left   command/resize-root-left
                           :layout/resize-top    command/resize-root-top
                           :layout/resize-bottom command/resize-root-bottom
                           :layout/resize-right  command/resize-root-right
                           nil)]
              (put! channel
                    {:node node
                     :event e
                     :name :drag
                     :command f})))))




(defn layout-resize-left-source
  [node]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (aset e "eventname" :layout/resize-left))))




(defn layout-resize-right-source
  [node]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (aset e "eventname" :layout/resize-right))))




=(defn layout-resize-top-source
  [node]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (aset e "eventname" :layout/resize-top))))




(defn layout-resize-bottom-source
  [node]
  (update node
          :element/attr
          assoc
          :onMouseDown
          (fn [e]
            (aset e "eventname" :layout/resize-bottom))))




(defn perform-drag
  [{:keys [event] :as msg}]
  (let [prev (atom [(.-clientX event)
                    (.-clientY event)])]
    (letfn [(drag-listener [e]
              (let [x (.-clientX e)
                    y (.-clientY e)]
                (command/update! (assoc msg
                                        :delta/dx (- x (@prev 0))
                                        :delta/dy (- y (@prev 1))))
                (reset! prev [x y])))
            (unlisten-f [e]
              (unlisten js/window
                        "mousemove"
                        drag-listener))]
      (listen js/window "mouseup" unlisten-f)
      (listen js/window "mousemove" drag-listener))))




(defn init-chan []
  (go-loop []
    (let [{:keys [name] :as msg} (<! channel)]
      (case name
        :drag (perform-drag msg)
        nil)
      (recur))))



(init-chan)
