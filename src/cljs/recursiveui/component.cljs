(ns recursiveui.component
  (:require [recursiveui.event :as event]
            [recursiveui.structure :as structure]
            [recursiveui.command :as command]
            [recursiveui.element :as element :refer [children ccomp conjoin*]]))



(def resize-bar-top
  (element/conjoin structure/sidebar-top
                   event/delta-y))


(def resize-bar-top*
  (conjoin* (comp structure/sidebar-top*
                  (event/delta-update* command/resize-root-top))))


(def resize-bar-left
  (element/conjoin structure/layout-sidebar
                   (event/delta-update* command/resize-root-left)))


(def resize-bar-left*
  (conjoin* (comp structure/layout-sidebar*
                  (event/delta-update* command/resize-root-left))))



(def resize-layout
  (element/conjoin structure/layout-sidebar
                   event/layout-resize-delta))



(def resize-layout*
  (conjoin* (comp structure/layout-sidebar*
                  event/layout-resize-delta*)))




(def layout-resize-root-left
  (element/conjoin structure/sidebar-left
                   (event/delta-update command/resize-root-left)))



(def layout-resize-root-left*
  (conjoin* (comp structure/sidebar-left*
                  (event/delta-update* command/resize-root-left))))



(def layout-resize-root-right
  (element/conjoin structure/sidebar-right
                   (event/delta-update command/resize-root-right)))



(def layout-resize-root-right*
  (conjoin* (comp structure/sidebar-right*
                  (event/delta-update* command/resize-root-right))))



(def layout-resize-root-bottom
  (element/conjoin structure/sidebar-bottom*
                   (event/delta-update* command/resize-root-bottom)))



(def layout-resize-root-bottom*
  (conjoin* (comp structure/sidebar-bottom*
                  (event/delta-update* command/resize-root-bottom))))



(def layout-resize-root-top
  (element/conjoin structure/sidebar-top
                   (event/delta-update command/resize-root-top)))


(def layout-resize-root-top*
  (conjoin* (comp structure/sidebar-top*
                  (event/delta-update* command/resize-root-top))))


#_(def layout-resize-root-top
  {:render (fn [node]
             (children (comp (structure/sidebar-top* node)
                             ((event/delta-update* command/resize-root-top) node))
                       (comp (structure/sidebar-bottom* node)
                             ((event/delta-update* command/resize-root-bottom) node))
                       (comp (structure/sidebar-left* node)
                             ((event/delta-update* command/resize-root-left) node))
                       (comp (structure/sidebar-right* node)
                             ((event/delta-update* command/resize-root-right) node))))})





#_(def layout-resize-root-top
  (children (ccomp structure/sidebar-top*
                   (event/delta-update* command/resize-root-top))
            (ccomp structure/sidebar-bottom*
                   (event/delta-update* command/resize-root-bottom))
            (ccomp structure/sidebar-left*
                   (event/delta-update* command/resize-root-left))
            (ccomp structure/sidebar-right*
                   (event/delta-update* command/resize-root-right))))




(def flex-root-action-bar
  (element/conjoin structure/flex-root-action-bar
                   event/layout-drag-delta
                   (element/conjoin structure/drag-button)))




(def flex-root-action-bar*
  (conjoin* (comp structure/flex-root-action-bar*
                  event/layout-resize-delta*
                  (conjoin* structure/drag-button*))))
