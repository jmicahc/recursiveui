(ns recursiveui.core
  (:require [reagent.core :as reagent]
            [recursiveui.data :as data]
            [recursiveui.event :as event :refer [drag]]
            [recursiveui.command :as command
             :refer [resize-grid]]
            [cljs.core.async :refer [chan <! >! take! mult put!]]))



(defonce debug?
  ^boolean js/goog.DEBUG)


(def base-element [:div {}])


(defn attr
  ([k v & kvs]
   (fn [elem]
     (apply update elem 1 assoc k v kvs))))



(defn style [k v & kvs]
  (fn [elem]
    (apply update-in elem [1 :style] assoc k v kvs)))



(defn tag [name]
  (fn [elem]
    (assoc elem 0 name)))



(defn html [s]
  (fn [elem]
    (conj elem s)))



(defn conjoin [f & fs]
  (let [g (apply comp f fs)]
    (fn [elem]
      (conj elem (g base-element)))))



(defn ipose [f & fs]
  (let [x ((apply comp f fs) base-element)]
    (fn [elem]
      (into (subvec elem 0 2)
            (interpose x)
            (subvec elem 2)))))



(defn style-element
  "temporary"
  [{:keys [style/backgroundColor]}]
  (style :backgroundColor backgroundColor))



(def layout-row
  (memoize (fn [{:keys [layout/magnitude]}]
             (style :height magnitude
                    :display "flex"
                    :flexDiretion "row"))))



(def layout-column
  (memoize (fn [{:keys [layout/magnitude]}]
             (style :width magnitude
                    :display "flex"
                    :flexDirection "column"))))



(def layout-root
  (memoize (fn [{:keys [style/width style/height
                        style/top  style/left
                        style/flex-direction]}]
             (style :width width
                    :height height
                    :top top
                    :left left
                    :dipslay "flex"
                    :position "absolute"
                    :flexDirection flex-direction))))



(def drag-x-source
  (let [drag-fn (drag (fn [{:keys [delta/dx path] :as m}]
                        (let [p (conj path :style/width)]
                          (swap! data/state update-in p + dx)
                          m)))]
    (fn [node] (event/vargs drag-fn :path (:path node)))))



(def drag-y-source
  (let [drag-fn (drag (fn [{:keys [delta/dy path] :as m}]
                        (let [p (conj path :style/height)]
                          (swap! data/state update-in p + dy)
                          m)))]
    (fn [node] (event/vargs drag-fn :path (:path node)))))




(def layout-resize-event-source
  (let [drag-fn (drag (fn [{:keys [delta/dx delta/dy path] :as m}]
                        (println "hello wrold" dx)
                        #_(swap! data/state update-in path resize-grid dx dy)
                        m))]
    (fn [node]
      (event/on :onClick (event/vargs drag-fn :path (:path node))))))




(def layout-resize-element
  (fn [{:keys [layout-resize-style/backgroundColor
               layout-resize-style/opacity
               layout-resize-style/width]
        :or {width "15px"
             backgroundColor "red"
             opacity 1}
        :as node}]
    (conjoin (layout-resize-event-source node)
             (attr :class "layout-resize-element")
             (style :backgroundColor backgroundColor
                    :width width
                    :height "100%"
                    :opacity opacity))))



(def layout-resize-element-left
  (memoize (fn [{:keys [layout-resize-element/width]
                 :or   {width 15}
                 :as   node}]
             (comp (layout-resize-element node)))))



(def layout-resize-element-right
  (memoize (fn [{:keys [layout-resize-element/height]
                 :or {height 15}
                 :as node}]
             (comp (layout-resize-element node)
                   (style :height height
                          :width "100%")))))




(defn layout-row-resizable
  [{:keys [layout-resize/width]
    :or   {width 20}
    :as   node}]
  (comp (layout-row node)
        (ipose (drag-x-source node)
               (layout-resize-element node)
               (style :width width
                      :backgroundColor "green"))))



(defn layout-column-resizable
  [{:keys [layout-resize/width]
    :or   {width 20}
    :as   node}]
  (comp (layout-column node)
        (ipose (drag-x-source node)
               (layout-resize-element node)
               (style :width width
                      :backgroundColor "black")
               (layout-column node))))


(defn tag->fn [tag]
  (case tag
    :layout/root   layout-root
    :layout/row    layout-row
    :layout/column layout-column
    :style/root    style-element
    :event/root    layout-resize-element-left))




(defn render
  ([{:keys [tags children path] :as node}]
   (let [f (transduce (comp (map #((tag->fn %) node)))
                      comp
                      tags)]
     (f (into base-element (map render children))))))


(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))


(defn root-component [state]
  (fn [] [:div {:id "root-elem"} (render @state)]))




(defn reload []
  (reagent/render [root-component data/state]
                  (.getElementById js/document "app")))



(defn ^:export main []
  (dev-setup)
  (swap! data/state command/init-paths)
  (reload))
