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



(defn build [xf]
  (xf (fn [a b c] [b c])))


(def ids (atom {}))
(def update-ids! (memoize (fn [id path] (swap! ids assoc id path))))


(defn render
  ([ch {:keys [tags] :as node}]
   (let [xf (transduce (map tag->fn) comp tags) 
         f (xf (fn [node ch elem] [node ch elem]))
         [node ch elem] (f node ch (into base-element (render-nav (map #(render ch %)) node)))]
     (when-let [id (:component/id node)]
       (update-ids! id (:path node)))
     elem)))



(defn init
  [{:keys [tags children] :as node}]
  (let [xf (transduce (map tag->fn) comp tags)
        f  (xf identity)]
    (f (assoc node :children (mapv init children)))))


