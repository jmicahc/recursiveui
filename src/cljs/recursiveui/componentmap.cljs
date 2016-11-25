(ns recursiveui.componentmap
  (:require [recursiveui.component :as component]
            [recursiveui.data :as data]
            [recursiveui.event :as event]
            [recursiveui.structure :as structure]))



(def tag->fn
  {:structure/flex-root                 structure/flex-root
   :structure/flex-row                  structure/flex-row
   :structure/flex-column               structure/flex-column
   :structure/style                     structure/style-element
   :structure/sidebar-left              structure/sidebar-left
   :structure/sidebar-top               structure/sidebar-top
   :structure/border                    structure/border
   :component/layout-resize-root-left   component/layout-resize-root-left
   :component/layout-resize-root-right  component/layout-resize-root-right
   :component/layout-resize-root-bottom component/layout-resize-root-bottom
   :component/layout-resize-root-top    component/layout-resize-root-top
   :structure/flex-root-action-bar      component/flex-root-action-bar
   :component/resize-layout             component/resize-layout})
