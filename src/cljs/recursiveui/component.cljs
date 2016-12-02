(ns recursiveui.component
  (:require [recursiveui.listeners :as listeners]
            [recursiveui.structure :as structure]
            [recursiveui.element :as elem :refer [conjoin]]))


(def resizable-flex-root
  (comp structure/flex-root
        listeners/layout-resize-root-handler
        listeners/layout-fullsize-action-handler
        listeners/layout-drag-handler
        (fn [x]
          (conjoin x
                   {:tags #{:structure/flex-root-action-bar
                            :listeners/layout-drag-source}
                    :traverse/render? true
                    :children [{:tags #{:structure/action-button
                                        :listeners/fullsize-action-source}
                                :traverse/render? true}]}
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
