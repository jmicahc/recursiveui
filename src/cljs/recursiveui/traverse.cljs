(ns recursiveui.traverse
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.data :as data]
            [cljs.pprint :refer [pprint]]))




(defn render-nav [xf node]
  (with-paths
    (comp (mapcat (fn [{:keys [traverse/render?]
                        :as node}]
                    (if render?
                      (list node)
                      (with-paths node))))
          xf)
    node))




(defn create-element
  [{:keys [element/style
           element/type
           element/attr]
    :as node}]
  [(or type :div)
   (assoc attr :style style)])





(defn render
  [{:keys [tags test]
    :as node}]
  (let [f (transduce (map tag->fn) comp tags)
        x (f (assoc node :node node))]
    (into (create-element x)
          (render-nav (map render) x))))
