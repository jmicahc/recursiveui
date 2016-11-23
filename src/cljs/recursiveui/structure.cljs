(ns recursiveui.structure
  (:require [recursiveui.element :as elem
             :refer [attr attr* style tag conjoin conjoin* class class* style*]]
            [recursiveui.types :as types]
            [recursiveui.command :as command]
            [cljs.core.async :refer [chan]]))


(def flex-row
  {:render
   (fn [{:keys [layout/magnitude]
         :as node}]
     (comp (class "layout-row layout-flex")
           (style :height magnitude
                  :display "flex"
                  :position "relative"
                  :flexDiretion "row")))})


(defn flex-row* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/magnitude] :as node} elem]
     (rf node (style* elem
                      :height magnitude
                      :display "flex"
                      :position "relative"
                      :flexDiretion "row")))))



(def flex-column
  {:render
   (fn [{:keys [layout/magnitude] :as node}]
     (comp (class "layout-column layout-flex")
           (style :width magnitude
                  :display "flex"
                  :position "relative"
                  :flexDirection "column")))})


(defn flex-column* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/magnitude] :as node} elem]
     (rf node (style* elem
                      :width magnitude
                      :display "flex"
                      :position "relative"
                      :flexDiretion "column")))))


(def flex-root
  {:render
   (fn [{:keys [layout/width layout/height
                layout/top   layout/left
                layout/flex-direction]
         :as node}]
     (comp (class "layout-root layout-flex")
           (style :width width
                  :height height
                  :top top
                  :left left
                  :dipslay "flex"
                  :position "absolute"
                  :flexDirection flex-direction)))})



(defn flex-root* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/width layout/height
              layout/top   layout/left
              layout/flex-direction]
       :as node} elem]
     (rf node (style* elem
                      :width width
                      :height height
                      :top top
                      :left left
                      :dipslay "flex"
                      :position "absolute"
                      :flexDirection flex-direction)))))



(def flex-root-action-bar
  {:render
   (fn [node]
     (comp  (class "layout-root-action-bar")
            (style :width "100%"
                   :height 40
                   :top -3
                   :left -3
                   :position "absolute"
                   :backgroundColor "grey"
                   :borderColor "#181319"
                   :border "solid")))})



(defn flex-root-action-bar* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (-> (class* elem "layout-root-action-bar")
             (style* :width "100%"
                     :height 40
                     :top -3
                     :left -3
                     :position "absolute"
                     :backgroundColor "grey"
                     :borderColor "#181319"
                     :border "solid"))))))



(def sidebar-left
  {:render
   (fn [{:keys [side-bar/backgroundColor
                side-bar/opacity
                side-bar/width]
         :or {width "15px"
              backgroundColor "red"
              opacity 0.7}
         :as node}]
     (comp (class "sidebar-left")
           (style :backgroundColor backgroundColor
                  :position "absolute"
                  :top "0px"
                  :left "0px"
                  :width width
                  :height "100%"
                  :opacity opacity)))})


(defn sidebar-left* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [side-bar/backgroundColor
              side-bar/opacity
              side-bar/width]
       :or {width "15px"
            backgroundColor "red"
            opacity 0.7}
       :as node} elem]
     (rf node
         (-> (class* elem "sidebar-left")
             (style* :backgroundColor backgroundColor
                     :position "absolute"
                     :top "0px"
                     :left "0px"
                     :width width
                     :height "100%"
                     :opacity opacity))))))






(def sidebar-top
  {:render
   (fn [{:keys [side-bar/backgroundColor
                side-bar/opacity
                side-bar/height]
         :or {height "12px"
              backgroundColor "#1D1D2A"
              opacity 0.7}
         :as node}]
     (comp (class "sidebar-top")
           (style :backgroundColor backgroundColor
                  :position "absolute"
                  :zIndex 10
                  :left "0px"
                  :top "0px"
                  :height height
                  :botttom "6px"
                  :width "100%"
                  :opacity opacity)))})


(defn sidebar-top* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [side-bar/backgroundColor
              side-bar/opacity
              side-bar/height]
       :or {height "12px"
            backgroundColor "#1D1D2A"
            opacity 0.7}
       :as node} elem]
     (rf node
         (-> (class* elem "sidebar-top")
             (style* :backgroundColor backgroundColor
                     :position "absolute"
                     :zIndex 10
                     :left "0px"
                     :top "0px"
                     :height height
                     :botttom "6px"
                     :width "100%"
                     :opacity opacity))))))



(def sidebar-right
  {:render
   (fn [node]
     (comp (class "layout-resize-root-right")
           (style :backgroundColor "red"
                  :position "absolute"
                  :top "0px"
                  :right "0px"
                  :height "100%"
                  :width "15px"
                  :opacity "0.7")))})



(defn sidebar-right* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (-> (class* elem "sidebar-right")
             (style* :backgroundColor "red"
                     :position "absolute"
                     :top "0px"
                     :right "0px"
                     :height "100%"
                     :width "15px"
                     :opacity "0.7"))))))




(def sidebar-bottom
  {:render
   (fn [node]
     (comp (class "layout-resize-root-bottom")
           (style :backgroundColor "red"
                  :position "absolute"
                  :bottom "0px"
                  :width "100%"
                  :height "10px"
                  :opacity "0.7")))})



(defn sidebar-bottom* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (-> (class* elem "sidebar-bottom")
             (style* :backgroundColor "red"
                     :position "absolute"
                     :bottom "0px"
                     :width "100%"
                     :height "10px"
                     :opacity "0.7"))))))



(def layout-sidebar
  (let [render-sidebar-left (:render sidebar-left)
        render-sidebar-top  (:render sidebar-top)]
    {:render
     (fn [{:keys [layout/partition] :as node}]
       (comp (if (= partition :column)
               (render-sidebar-left node)
               (render-sidebar-top node))
             (attr :class "layout-sidebar")))}))




(defn layout-sidebar* [rf]
  (let [render-sidebar-left (sidebar-left* (fn [a b] b))
        render-sidebar-top (sidebar-top* (fn [a b] b))]
    (fn
      ([] (rf))
      ([node] (rf node))
      ([{:keys [layout/partition] :as node} elem]
       (rf node
           (if (= partition :column)
             (render-sidebar-left node elem)
             (render-sidebar-top node elem)))))))




(def drag-button
  "temporary"
  {:render
   (fn [{:keys [layout/partition] :as node}]
     (comp (class "drag-button")
           (style :position "absolute"
                  :top 10
                  :left 10
                  :width 15
                  :height 20
                  :backgroundColor "blue")
           (attr  :onClick
                  (fn [e]
                    (.stopPropagation e)
                    (command/update! command/layout-fullscreen
                                     {:node node})))))})


(defn drag-button* [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/partition] :as node} elem]
     (-> (class* elem "drag-button")
         (style* :position "absolute"
                 :top 10
                 :left 10
                 :width 15
                 :height 20
                 :backgroundColor "blue")
           (attr* :onClick
                  (fn [e]
                    (.stopPropagation e)
                    (command/update! command/layout-fullscreen
                                     {:node node})))))))




(def border
  "temporary"
  {:render
   (fn [node]
     (style :border "solid"
            :borderColor "#181319"))})


(defn border*
  "temporary"
  [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (style* elem
                 :border "solid"
                 :borderColor "#181319")))))




(def style-element
  "temporary"
  {:render
   (fn [{:keys [style/backgroundColor]}]
     (style :backgroundColor backgroundColor))})



(defn style-element*
  "temporary"
  [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [style/backgroundColor] :as node} elem]
     (rf node (style* elem :backgroundColor backgroundColor)))))




