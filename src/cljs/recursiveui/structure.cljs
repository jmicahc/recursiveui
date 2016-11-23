(ns recursiveui.structure
  (:require [recursiveui.element :as elem
             :refer [attr style tag conjoin class]]
            [recursiveui.types :as types]
            [recursiveui.command :as command]
            [cljs.core.async :refer [chan]]))


(defn flex-row [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/magnitude] :as node} elem]
     (rf node (style elem
                     :height magnitude
                     :display "flex"
                     :position "relative"
                     :flexDiretion "row")))))



(defn flex-column [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/magnitude] :as node} elem]
     (rf node (style elem
                     :width magnitude
                     :display "flex"
                     :position "relative"
                     :flexDiretion "column")))))



(defn flex-root [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/width layout/height
              layout/top   layout/left
              layout/flex-direction]
       :as node} elem]
     (rf node (style elem
                     :width width
                     :height height
                     :top top
                     :left left
                     :dipslay "flex"
                     :position "absolute"
                     :flexDirection flex-direction)))))



(defn flex-root-action-bar [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (-> (class elem "layout-root-action-bar")
             (style :width "100%"
                    :height 40
                    :top -3
                    :left -3
                    :position "absolute"
                    :backgroundColor "grey"
                    :borderColor "#181319"
                    :border "solid"))))))




(defn sidebar-left [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [side-bar/backgroundColor
              side-bar/opacity
              side-bar/width]
       :or {width "15px"
            backgroundColor "brown"
            opacity 1}
       :as node} elem]
     (rf node
         (-> (class elem "sidebar-left")
             (style :backgroundColor backgroundColor
                    :position "absolute"
                    :top "0px"
                    :left "0px"
                    :width width
                    :height "100%"
                    :opacity opacity))))))


(defn sidebar-top [rf]
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
         (-> (class elem "sidebar-top")
             (style :backgroundColor backgroundColor
                    :position "absolute"
                    :left "0px"
                    :top "0px"
                    :height height
                    :botttom "6px"
                    :width "100%"
                    :opacity opacity))))))


(defn sidebar-right [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (-> (class elem "sidebar-right")
             (style :backgroundColor "brown"
                    :position "absolute"
                    :top "0px"
                    :right "0px"
                    :height "100%"
                    :width "15px"
                    :opacity "1"))))))



(defn sidebar-bottom [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (-> (class elem "sidebar-bottom")
             (style :backgroundColor "brown"
                    :position "absolute"
                    :bottom "0px"
                    :width "100%"
                    :height "10px"
                    :opacity "1"))))))



(defn layout-sidebar [rf]
  (let [render-sidebar-left (sidebar-left (fn [a b] b))
        render-sidebar-top (sidebar-top (fn [a b] b))]
    (fn
      ([] (rf))
      ([node] (rf node))
      ([{:keys [layout/partition] :as node} elem]
       (rf node
           (if (= partition :column)
             (render-sidebar-left node elem)
             (render-sidebar-top node elem)))))))




(defn drag-button [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/partition] :as node} elem]
     (-> (class elem "drag-button")
         (style :position "absolute"
                :top 10
                :left 10
                :width 15
                :height 20
                :backgroundColor "blue")
         (attr :onClick
               (fn [e]
                 (.stopPropagation e)
                 (command/update! command/layout-fullscreen
                                  {:node node})))))))




(defn border
  "temporary"
  [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node elem]
     (rf node
         (style elem
                :border "solid"
                :borderColor "#181319")))))



(defn style-element
  "temporary"
  [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [style/backgroundColor] :as node} elem]
     (rf node (style elem :backgroundColor backgroundColor)))))




