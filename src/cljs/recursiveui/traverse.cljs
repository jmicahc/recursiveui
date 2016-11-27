(ns recursiveui.traverse
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.element :as elem]
            [recursiveui.component :as component]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.types :as types :refer [base-element]]
            [recursiveui.data :as data]
            [cljs.core.async :refer [chan]]
            [cljs.pprint :refer [pprint]]))


(defn flat-by [pred]
  (mapcat (fn [node]
            (if (pred node)
              (list node)
              (:children node)))))

#_(defn with-paths
  ([node] (with-paths (map identity) node))
  ([xf node]
   (let [path (or (:path node) [])]
     (into []
           (comp (map-indexed
                  (fn [idx node]
                    (assoc node :path (conj path :children idx))))
                 xf)
           (:children node)))))


(defn render-nav [xf node]
  (with-paths
    (comp (mapcat (fn [{:keys [traverse/render?
                               component/id
                               path]
                        :as node}]
                    (if render? (list node) (with-paths node))))
          xf)
    node))


(def ids (atom {}))
(def update-ids! (memoize (fn [id path] (swap! ids assoc id path))))



(defn render
  ([ch {:keys [tags] :as node}]
   (let [xf (transduce (map tag->fn) comp tags)
         f  (xf (fn [node ch elem] [node ch elem]))
         [node ch elem] (f node ch
                           (into base-element
                                 (render-nav (map #(render ch %)) node)))]
     #_(when-let [id (:component/id node)]
       (update-ids! id (:path node)))
     elem)))




(defn init
  [{:keys [tags children] :as node}]
  (let [xf (transduce (map tag->fn) comp tags)
        f  (xf identity)]
    (f (assoc node :children (mapv init children)))))


