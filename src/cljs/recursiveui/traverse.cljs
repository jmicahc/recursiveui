(ns recursiveui.traverse
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.element :as elem]
            [recursiveui.component :as component]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.types :as types :refer [base-element]]
            [recursiveui.data :as data]
            [cljs.core.async :refer [chan]]))



(defn render-nav [xf node]
  (with-paths xf node))



(defn render
  ([{:keys [tags] :as node}]
   (let [xf (transduce (map tag->fn) comp tags) 
         f (xf (fn [a b] b))]
     (f node (into base-element (render-nav (map render) node))))))


(defn init
  [{:keys [tags children] :as node}]
  (let [xf (transduce (map tag->fn) comp tags)
        f  (xf identity)]
    (f (assoc node :children (mapv init children)))))


