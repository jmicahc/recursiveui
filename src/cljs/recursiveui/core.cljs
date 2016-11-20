(ns recursiveui.core
  (:require [reagent.core :as reagent]
            [recursiveui.data :as data]
            [recursiveui.event :as event]
            [recursiveui.command :as command
             :refer [resize-grid]]
            [recursiveui.util :refer [map-paths]]
            [recursiveui.structure :as structure]
            [recursiveui.element :as elem :refer [base-element]]
            [recursiveui.signal :as signal]
            [cljs.core.async :refer [chan <! >! take! mult put!]]
            [goog.dom :as gdom]
            [goog.events :as gevents]
            [clojure.pprint :refer [pprint]]))



(defonce debug?
  ^boolean js/goog.DEBUG)



(defn tag->fn [tag]
  (case tag
    :structure/flex-root          structure/flex-root
    :structure/flex-row           structure/flex-row
    :structure/flex-column        structure/flex-column
    :structure/style              structure/style-element
    :structure/sidebar-left       structure/sidebar-left
    :structure/sidebar-right      structure/sidebar-right
    :event/root                   event/delta))




(defn render
  ([{:keys [tags] :as node}]
   (let [f (transduce (map (fn [tag]
                             (let [f ((tag->fn tag) :render identity)]
                               (f node))))
                      comp
                      tags)]
     (f (into base-element (map-paths render node))))))


(defn init-paths
  ([node] (init-paths [] node))
  ([path {:keys [children] :as node}]
   (assoc node
          :path path
          :children (mapv (fn [idx child]
                            (init-paths (conj path :children idx)
                                        child))
                          (range)
                          children))))


(defn init
  [{:keys [tags children] :as node}]
  (let [f (reduce (fn [f tag]
                    (if-let [g ((tag->fn tag) :init identity)]
                      (comp f g)
                      f))
                  identity
                  tags)]
    (f (assoc node :children (mapv init children)))))



(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))


(defn root-component [state]
  (fn [] [:div {:id "root-elem"} (render @state)]))




(defn reload []
  (reagent/render [root-component data/state]
                  (.getElementById js/document "app")))





(defn window-width [] (.-innerWidth js/window))
(defn window-height [] (.-innerHeight js/window))



(defn window-listener [e]
  (swap! data/state
         (fn [{:keys [layout/width
                      layout/height]
               :as root-node}]
           (command/update-layout-root-size
            root-node
            (- (window-width) width)
            (- (window-height) height)))))

(gevents/unlisten js/window "resize" window-listener)
(gevents/listen js/window "resize" window-listener)



(defn ^:export main []
  (dev-setup)
  (swap! data/state init)
  (window-listener nil)
  (reload))
