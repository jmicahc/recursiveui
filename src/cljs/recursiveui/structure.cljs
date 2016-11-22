(ns recursiveui.structure
  (:require [recursiveui.element :as elem
             :refer [attr style tag conjoin]]))


(def flex-row
  {:render
   (fn [{:keys [layout/magnitude]}]
     (comp (attr :class "layout-row")
           (style :height magnitude
                  :display "flex"
                  :position "relative"
                  :flexDiretion "row")))})




(def flex-column
  {:render
   (fn [{:keys [layout/magnitude]}]
     (comp (attr :class "layout-column")
           (style :width magnitude
                  :display "flex"
                  :position "relative"
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
              backgroundColor "brown"
              opacity 1}
         :as node}]
     (comp (attr :class "sidebar-left")
           (style :backgroundColor backgroundColor
                  :position "absolute"
                  :top "0px"
                  :left "0px"
                  :width width
                  :height "100%"
                  :opacity opacity)))})



(def sidebar-top
  {:render
   (fn [{:keys [side-bar/backgroundColor
                side-bar/opacity
                side-bar/height]
         :or {height "12px"
              backgroundColor "#1D1D2A"
              opacity 0.7}
         :as node}]
     (comp (attr :class "sidebar-top")
           (style :backgroundColor backgroundColor
                  :position "absolute"
                  :left "0px"
                  :top "0px"
                  :height height
                  :botttom "6px"
                  :width "100%"
                  :opacity opacity)))})




(def layout-sidebar
  (let [render-sidebar-left (:render sidebar-left)
        render-sidebar-top  (:render sidebar-top)]
    {:render
     (fn [{:keys [layout/partition] :as node}]
       (comp (if (= partition :column)
               (render-sidebar-left node)
               (render-sidebar-top node))
             (attr :class "layout-sidebar")))}))




(def border
  "temporary"
  {:render
   (fn [node]
     (style :border "solid"
            :borderColor "#181319"))})



(def style-element
  "temporary"
  {:render
   (fn [{:keys [style/backgroundColor]}]
     (style :backgroundColor backgroundColor))})


