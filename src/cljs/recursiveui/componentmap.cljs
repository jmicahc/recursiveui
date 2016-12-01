(ns recursiveui.componentmap
  (:require [recursiveui.component :as component]
            [recursiveui.data :as data]
            [recursiveui.event :as event]
            [recursiveui.structure :as structure]
            [recursiveui.listeners :as listeners]))


(def tag->fn
  {:structure/flex-root                   structure/flex-root
   :structure/flex-row                    structure/flex-row
   :structure/flex-column                 structure/flex-column
   :structure/sidebar-left                structure/sidebar-left
   :structure/sidebar-top                 structure/sidebar-top
   :structure/sidebar-right               structure/sidebar-right 
   :structure/sidebar-bottom              structure/sidebar-bottom
   :component/resizable-flex-root         component/resizable-flex-root
   :listeners/layout-resize-handler       listeners/layout-resize-handler
   :listeners/layout-resize-source        listeners/layout-resize-source
   :listeners/layout-resize-root-handler  listeners/layout-resize-root-handler
   :listeners/layout-resize-left-source   listeners/layout-resize-left-source
   :listeners/layout-resize-right-source  listeners/layout-resize-right-source
   :listeners/layout-resize-top-source    listeners/layout-resize-top-source
   :listeners/layout-resize-bottom-source listeners/layout-resize-bottom-source})
