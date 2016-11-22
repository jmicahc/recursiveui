(ns recursiveui.traverse
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.types :as types :refer [base-element]]))



(defn render
  ([{:keys [tags] :as node}]
   (let [f (transduce (map (fn [tag]
                             (let [f ((tag->fn tag) :render identity)]
                               (f node))))
                      comp
                      tags)]
     (f (into base-element (with-paths (map render) node))))))




(defn init
  [{:keys [tags children] :as node}]
  (let [f (reduce (fn [f tag]
                    (if-let [g ((tag->fn tag) :init identity)]
                      (comp f g)
                      f))
                  identity
                  tags)]
    (f (assoc node :children (mapv init children)))))



