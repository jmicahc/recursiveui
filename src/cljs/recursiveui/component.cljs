(ns recursiveui.component
  (:require [recursiveui.event :as event]
            [recursiveui.structure :as structure]
            [recursiveui.command :as command]
            [recursiveui.element :as element :refer [conjoin]]))



(def resize-bar-top
  (conjoin (comp structure/sidebar-top
                 (event/delta-update command/resize-root-top))))



(def resize-bar-left
  (conjoin (comp structure/layout-sidebar
                 (event/delta-update command/resize-root-left))))



(def resize-layout
  (conjoin (comp structure/layout-sidebar
                 event/layout-resize-delta)))




(def layout-resize-root-left
  (conjoin (comp structure/sidebar-left
                 (event/delta-update command/resize-root-left))))




(def layout-resize-root-right
  (conjoin (comp structure/sidebar-right
                 (event/delta-update command/resize-root-right))))




(def layout-resize-root-bottom
  (conjoin (comp structure/sidebar-bottom
                 (event/delta-update command/resize-root-bottom))))




(def layout-resize-root-top
  (conjoin (comp structure/sidebar-top
                 (event/delta-update command/resize-root-top))))



(def flex-root-action-bar
  (conjoin (comp structure/flex-root-action-bar
                 event/layout-resize-delta
                 (conjoin structure/drag-button))))
