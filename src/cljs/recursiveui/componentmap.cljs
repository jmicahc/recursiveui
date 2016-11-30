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
   :structure/conjoin-button            structure/conjoin-button
   :structure/layout-sidebar            structure/layout-sidebar
   :structure/border                    structure/border
   :structure/flex-root-action-bar      structure/flex-root-action-bar})
