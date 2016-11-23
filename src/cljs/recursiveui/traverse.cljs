(ns recursiveui.traverse
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.element :as elem]
            [recursiveui.component :as component]
            [recursiveui.componentmap :as cmap :refer [tag->fn tag->fn*]]
            [recursiveui.types :as types :refer [base-element]]
            [recursiveui.data :as data]
            [cljs.core.async :refer [chan]]))



;; Capture the event at the document root.
;; If it has a channel, dispatch to it.
;; otherwise do nothing.


(defn render-nav [xf node]
  (with-paths xf node))


(defn component-fn [node tags fn-name]
  (transduce (map (fn [tag]
                    (let [f ((tag->fn tag) fn-name identity)]
                      (f node))))
             comp
             tags))

(defn render
  ([{:keys [tags component/id] :as node}]
   (let [f (component-fn node tags :render)]
     (f (into base-element (render-nav (map render) node))))))


(defn render*
  ([{:keys [tags] :as node}]
   (let [xf (transduce (map tag->fn*) comp tags) 
         f (xf (fn [a b] b))]
     (f node (into base-element (render-nav (map render*) node))))))




(defn init
  [{:keys [tags children] :as node}]
  (let [f (reduce (fn [f tag]
                    (if-let [g ((tag->fn tag) :init identity)]
                      (comp f g)
                      f))
                  identity
                  tags)]
    (f (assoc node :children (mapv init children)))))



(defn init*
  [{:keys [tags children] :as node}]
  (let [xf (transduce (map tag->fn*) comp tags)
        f  (xf identity)]
    (f (assoc node :children (mapv init* children)))))


