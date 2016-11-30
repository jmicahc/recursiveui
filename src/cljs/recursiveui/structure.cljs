(ns recursiveui.structure
  (:require [recursiveui.element :as elem
             :refer [attr style tag conjoin class event]]
            [recursiveui.types :as types]
            [recursiveui.event :as event]
            [recursiveui.command :as command]
            [cljs.core.async :refer [chan]]))




(defn flex-row [rf]
  (fn
    ([] (rf))
    ([buff] (rf buff))
    ([buff {:keys [node name] :as msg}]
     (rf node
         (case name
           :render (assoc msg
                          :node
                          (style node
                                 :height (:layout/magnitude node)
                                 :position "relative"
                                 :flexDirection "row"))
           msg)))))




(defn flex-column [rf]
  (fn
    ([] (rf))
    ([buff] (rf buff))
    ([buff {:keys [name node] :as msg}]
     (case name
       :render (rf buff
                   (assoc msg
                          (style node
                                 :width (:layout/magnitude node)
                                 :display "flex"
                                 :flexDirection "column"))))
     (rf buff msg))))



(defn flex-root [rf]
  (fn
    ([] (rf))
    ([buff] (rf buff))
    ([buff {:keys [name node] :as msg}]
     (let [{:keys [layout/width layout/height
                   layout/top   layout/left
                   layout/flex-direction]} node]
       (case name
         :render (rf buff
                     (update msg
                             :node
                             style
                             :width width
                             :height height
                             :top top
                             :left left
                             :display "flex"
                             :flexDirection flex-Direction))
         (rf buff msg))))))
