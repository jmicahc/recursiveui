(ns recursiveui.structure
  (:require [recursiveui.element :as elem :refer [tag style attr class]]
            [cljs.core.async :refer [chan put! <! take!]]
            [goog.events :refer [listen unlisten]]))





(defn flex-row
  [{:keys [layout/magnitude]
    :as node}]
  (style node
         :height magnitude
         :display "flex"
         :position "relative"
         :flexDirection "row"))





(defn flex-column
  [{:keys [layout/magnitude]
    :as node}]
  (style node
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
  (style node
         :position "absolute"
         :flexDirection flex-direction
         :width width
         :height height
         :top top
         :left left))






(defn sidebar-left
  "temporary"
  [node]
  (style node
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
  (style node
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
  (style node
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
  (style node
         :backgroundColor "brown"
         :position "absolute"
         :bottom "0px"
         :width "100%"
         :height "8px"
         :opacity "1"))

