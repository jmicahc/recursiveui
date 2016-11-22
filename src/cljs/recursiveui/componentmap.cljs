(ns recursiveui.componentmap
  (:require [recursiveui.signal :as signal]
            [recursiveui.component :as component]
            [recursiveui.data :as data]
            [recursiveui.event :as event]
            [recursiveui.structure :as structure]))



(defn tag->fn [tag]
  (case tag
    :structure/flex-root            structure/flex-root
    :structure/flex-row             structure/flex-row
    :structure/flex-column          structure/flex-column
    :structure/style                structure/style-element
    :structure/sidebar-left         structure/sidebar-left
    :structure/sidebar-top          structure/sidebar-top
    :structure/border               structure/border
    :component/resize-layout        component/resize-layout))


