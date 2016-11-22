(ns recursiveui.component
  (:require [recursiveui.event :as event]
            [recursiveui.structure :as structure]
            [recursiveui.element :as element]))




(def resize-bar-top
  (element/conjoin structure/sidebar-top
                   event/delta-y))





(def resize-bar-left
  (element/conjoin structure/layout-sidebar
                   event/delta-x))




(def resize-layout
  (element/conjoin structure/layout-sidebar
                   event/layout-resize-delta))



