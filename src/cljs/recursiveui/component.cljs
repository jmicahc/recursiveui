(ns recursiveui.component
  (:require [recursiveui.event :as event]
            [recursiveui.structure :as structure]
            [recursiveui.command :as command]))




#_(defn resizable-layout-row [node]
  (merge-with comp
              (structure/flex-row node)
              (structure/sidebar-left node)
              (action )))
