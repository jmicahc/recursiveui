(ns recursiveui.component
  (:require [recursiveui.listeners :as listeners]
            [recursiveui.structure :as structure]))


(def resizable-flex-root
  (comp structure/flex-root
        listeners/layout-resize-root-handler
        (fn [node]
          (update node
                  :children
                  conj
                  {:tags #{:structure/sidebar-top
                           :listeners/layout-resize-top-source}
                   :traverse/render? true}
                  {:tags #{:structure/sidebar-left
                           :listeners/layout-resize-left-source}
                   :traverse/render? true}
                  {:tags #{:structure/sidebar-right
                           :listeners/layout-resize-right-source}
                   :traverse/render? true}
                  {:tags #{:structure/sidebar-bottom
                           :listeners/layout-resize-bottom-source}
                   :traverse/render? true}))))
