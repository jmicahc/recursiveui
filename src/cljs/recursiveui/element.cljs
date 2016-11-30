(ns recursiveui.element
  (:require [cljs.core.async :refer [put! pipe chan close!]]))


(def base-element [:div {}])


(defn style [node k v & kvs]
  (apply update
         node
         :element/style
         assoc
         k v kvs))



(def dom-event? #{:onClick :onMouseDown :onMouseMove :onDoubleClick :onMouseUp})


