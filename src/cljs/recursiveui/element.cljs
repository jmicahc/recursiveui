(ns recursiveui.element
  (:require [cljs.core.async :as async :refer [chan pipe put!]]))


(defn class [node s & ss]
  (assoc-in node
            [:element/attr :class]
            (apply str (interpose " " (cons s  ss)))))




(defn attr-mrg [node k v]
  (if (and (fn? v) (k node))
    (update node k comp v)
    (assoc node k v)))




(defn attr
  ([node] node)
  ([node k v]
   (update node :element/attr attr-mrg k v))
  ([node k1 v1 k2 v2]
   (attr (attr node k1 v1) k2 v2))
  ([node k1 v1 k2 v2 & kvs]
   (apply attr (attr (attr node k1 v2) k2 v2) kvs)))




(defn style [node k v & kvs]
  (apply update node :element/style assoc k v kvs))




(defn tag [node t]
  (assoc node :element/type t))




(defn conjoin [node x & xs]
  (apply update node :children conj x xs))



(def dom-events #{:onClick :onDoubleClick
                  :onMouseUp :onMouseDown :onMouseMove})


(defn event
  [{:keys [node]
    :as x} k xf & kxfs]
  (let [handlers (apply assoc {} k xf kxfs)
        dom-events (filter dom-events (cons k (take-nth 2 kxfs)))
        xform (fn [rf]
                (let [rfs (into {}
                                (map (fn [[k xf]] [k (xf rf)]))
                                handlers)]
                  (fn
                    ([] (rf))
                    ([buff] (rf buff))
                    ([buff {:keys [event-name] :as x}]
                     ((rfs event-name rf) buff x)))))
        transform (chan 100 xform)]
    (apply attr
           (update x :channel (fn [ch]
                                (pipe transform ch)
                                transform))
           (mapcat (fn [event-name]
                     [event-name
                      (fn [e]
                        (.stopPropagation e)
                        (.preventDefault e)
                        (put! transform
                              {:client-x   (.-clientX e)
                               :client-y   (.-cleintY e)
                               :event-name event-name
                               :source     (:node x)})
                        e)])
                   dom-events))))





(defn event-source
  [{:keys [channel node] :as x} dom-event event-name]
  (attr x
        dom-event
        (fn [e]
          (put! channel
                {:client-x (.-clientX e)
                 :client-y (.-clientY e)
                 :event-name event-name
                 :source node}))))
