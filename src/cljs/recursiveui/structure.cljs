(ns recursiveui.structure
  (:require [recursiveui.element :as elem
             :refer [attr style tag conjoin]]))


(def flex-row
  {:render
   (fn [{:keys [layout/magnitude]}]
     (comp (attr :class "layout-row")
           (style :height magnitude
                  :display "flex"
                  :flexDiretion "row")))})




(def flex-column
  {:render
   (fn [{:keys [layout/magnitude]}]
     (comp (attr :class "layout-column")
           (style :width magnitude
                  :display "flex"
                  :flexDirection "column")))})




(def flex-root
  {:render
   (fn [{:keys [layout/width layout/height
                layout/top   layout/left
                layout/flex-direction]}]
     (comp (attr :class "layout-root")
           (style :width width
                  :height height
                  :top top
                  :left left
                  :dipslay "flex"
                  :position "absolute"
                  :flexDirection flex-direction)))})



(def sidebar-left
  {:render
   (fn [{:keys [side-bar/backgroundColor
                side-bar/opacity
                side-bar/width]
         :or {width "15px"
              backgroundColor "brown
"
              opacity 1}
         :as node}]
     (comp (attr :class "side-bar")
           (style :backgroundColor backgroundColor
                  :width width
                  :height "100%"
                  :opacity opacity)))})



(def sidebar-right
  {:render
   (fn [{:keys [side-bar/backgroundColor
                side-bar/opacity
                side-bar/width]
         :or {width "12px"
              backgroundColor "#1D1D2A"
              opacity 0.7}
         :as node}]
     (comp (attr :class "side-bar")
           (style :backgroundColor backgroundColor
                  :position "relative"
                  :width width
                  :right "6px"
                  :height "100%"
                  :opacity opacity)))})




(def style-element
  "temporary"
  {:render
   (fn [{:keys [style/backgroundColor]}]
     (style :backgroundColor backgroundColor))})


