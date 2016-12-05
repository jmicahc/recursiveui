(ns recursiveui.sources
  (:require [recursiveui.element :as element]))


(defn layout-resize-left [x]
  (element/event-source x :onMouseDown :layout-resize-left))



(defn layout-resize-right [x]
  (element/event-source x :onMouseDown :layout-resize-right))



(defn layout-resize-top [x]
  (element/event-source x :onMouseDown :layout-resize-top))



(defn layout-resize-bottom [x]
  (element/event-source x :onMouseDown :layout-resize-bottom))
