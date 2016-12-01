(ns recursiveui.structure
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [recursiveui.types :as types]
            [recursiveui.event :as event]
            [recursiveui.command :as command]
            [recursiveui.listeners :as listeners]
            [cljs.core.async :refer [chan put! <! take!]]
            [goog.events :refer [listen unlisten]]))



(defn flex-row
  [{:keys [layout/magnitude]
    :as node}]
  (update node
          :element/style
          assoc
          :height magnitude
          :display "flex"
          :position "relative"
          :flexDirection "row"))





(defn flex-column
  [{:keys [layout/magnitude]
    :as node}]
  (update node
          :element/style
          assoc
          :width magnitude
          :display "flex"
          :position "relative"
          :flexDirection "column"))




(defn flex-root
  [{:keys [layout/top
           layout/left
           layout/width
           layout/height
           layout/flex-direction]
    :as node}]
  (update node
          :element/style
          assoc
          :position "absolute"
          :flexDirection flex-direction
          :width width
          :height height
          :top top
          :left left))



(defn sidebar-left
  "temporary"
  [node]
  (update node
          :element/style
          assoc
          :backgroundColor "brown"
          :position "absolute"
          :top 0
          :left 0
          :width "8px"
          :height "100%"
          :opacity "9px"))



(defn sidebar-top
  "temporary"
  [node]
  (update node
          :element/style
          assoc
          :backgroundColor "brown"
          :position "absolute"
          :left "0px"
          :top "0px"
          :height "9px"
          :botttom "6px"
          :width "100%"
          :opacity 1))




(defn sidebar-right
  "temporary"
  [node]
  (update node
          :element/style
          assoc
          :backgroundColor "brown"
          :position "absolute"
          :top "0px"
          :right "0px"
          :height "100%"
          :width "8px"
          :opacity "1"))




(defn sidebar-bottom
  "temporary"
  [node]
  (update node
          :element/style
          assoc
          :backgroundColor "brown"
          :position "absolute"
          :bottom "0px"
          :width "100%"
          :height "8px"
          :opacity "1"))
