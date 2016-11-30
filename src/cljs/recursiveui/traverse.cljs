(ns recursiveui.traverse
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.element :as elem]
            [recursiveui.component :as component]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.listeners :as listeners]
            [recursiveui.types :as types :refer [base-element]]
            [recursiveui.data :as data]
            [cljs.core.async :refer [chan put!]]
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



(def ids (atom {}))
(def update-ids! (memoize (fn [id path] (swap! ids assoc id path))))



(defn init-element
  [{:keys [element/style
           element/attr
           element/events
           element/type]
    :as node}]
  [(or type :div)
   (merge attr
          (reduce (fn [events [dom-event event-name]]
                    (assoc events
                           dom-event
                           (fn [e]
                             (put! listeners/event-channel
                                   {:node node
                                    :dom-event (keyword (.-name e))
                                    :name event-name}))))
                  {}
                  events)
          {:style style})])



(defn render
  ([ch root]
   (render nil ch root))
  ([parent ch {:keys [tags element/union?] :as node}]
   (let [x  (if union? (merge parent node) node)
         xf (transduce (map tag->fn) comp tags)
         f  (xf (fn [node ch elem] [node ch elem]))
         [node ch elem] (f x ch (into (init-element x)
                                      (render-nav (map #(render node ch %)) node)))]
     elem)))





(defn init
  [{:keys [tags children] :as node}]
  (let [xf (transduce (map tag->fn) comp tags)
        f  (xf identity)]
    (f (assoc node :children (mapv init children)))))


