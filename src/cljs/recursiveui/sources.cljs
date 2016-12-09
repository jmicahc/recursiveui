(ns recursiveui.sources
  (:require [recursiveui.element :as element]))



(defn layout-resize-left [x]
  (element/event-source x :onMouseDown :layout-resize-left))



(defn layout-resize-right [x]
  (element/event-source x :onMouseDown :layout-resize-right))



(defn layout-resize-top [x]
  (element/event-source x :onMouseDown :layout-resize-top))



(defn layout-add-partition [x]
  (element/event-source x :onClick :layout-add-partition))



(defn layout-resize-bottom [x]
  (element/event-source x :onMouseDown :layout-resize-bottom))



(defn resize [x]
  (element/event-source x :onMouseDown :resize))



(defn drag [x]
  (element/event-source x :onMouseDown :drag))



(defn save-state [x]
  (element/event-source x :onClick :save-state))


(defn pretty-print-state [x]
  (element/event-source x :onClick :pretty-print-state))
