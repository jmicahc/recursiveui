(ns recursiveui.traverse
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent]
            [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.command :as command]
            [recursiveui.data :as data]
            [cljs.pprint :refer [pprint]]
            [cljs.core.async :as acync :refer [chan]]
            [goog.events :as gevents :refer [listen unlisten]]))




(defn render-nav [xf {:keys [channel] :as x}]
  (with-paths
    (comp (mapcat (fn [{:keys [traverse/render?]
                        :as node}]
                    (if render?
                      (list node)
                      (with-paths xf node))))
          xf)
    x))



(defn create-element
  [{:keys [element/style
           element/type
           element/attr]
    :as node}]
  [(or type :div) (assoc attr :style style)])



(defn render
  ([ch {:keys [tags] :as node}]
   (let [f (transduce (comp
                       #_(map (fn [tag] (println "tag" tag) tag))
                       (map tag->fn)) comp tags)
         x (f (assoc node
                     :node node
                     :channel ch))]
     (into (create-element x)
           (render-nav (map #(render (:channel x) %)) x)))))
