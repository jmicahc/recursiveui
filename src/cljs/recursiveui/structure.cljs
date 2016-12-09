(ns recursiveui.structure
  (:require [recursiveui.element :as elem :refer [tag style attr class conjoin]]
            [cljs.core.async :refer [chan put! <! take!]]
            [goog.events :refer [listen unlisten]]))





(defn flex-row
  [{:keys [layout/magnitude
           component/id]
    :as x}]
  (style x
         :height magnitude
         :display "flex"
         :position "relative"
         :flexDirection "row"))





(defn flex-column
  [{:keys [layout/magnitude
           component/id]
    :as x}]
  (style x
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
    :as x}]
  (style x
         :position "absolute"
         :flexDirection flex-direction
         :width width
         :height height
         :top top
         :left left))






(defn sidebar-left
  "temporary"
  [x]
  (style x
         :backgroundColor "brown"
         :position "absolute"
         :top 0
         :left 0
         :width "8px"
         :height "100%"
         :opacity "9px"))






(defn sidebar-top
  "temporary"
  [x]
  (style x
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
  [x]
  (style x
         :backgroundColor "brown"
         :position "absolute"
         :top "0px"
         :right "0px"
         :height "100%"
         :width "8px"
         :opacity "1"))





(defn sidebar-bottom
  "temporary"
  [x]
  (style x
         :backgroundColor "brown"
         :position "absolute"
         :bottom "0px"
         :width "100%"
         :height "8px"
         :opacity "1"))





(defn flex-root-action-bar
  "temporary"
  [x]
  (style x
         :width "100%"
         :height 40
         :top 3
         :left -3
         :position "absolute"
         :backgroundColor "grey"
         :borderColor "#181319"
         :border "solid"))




(defn action-button
  "temporary"
  [x]
  (style x
         :width 20
         :height 20
         :left 10
         :top 10
         :backgroundColor "red"
         :position "relative"))



(defn button
  "temporary"
  [x]
  (style x
         :width 20
         :height 20
         :right 10
         :bottom 10
         :backgroundColor "brown"
         :position "absolute"))


(defn text-button
  "temporary"
  [{:keys [element/inner-html] :as x}])
