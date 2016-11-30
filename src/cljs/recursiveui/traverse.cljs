(ns recursiveui.traverse
  (:require-macros [cljs.core.acync.macros :refer [go go-loop]])
  (:require [recursiveui.util :as util :refer [with-paths]]
            [recursiveui.element :as elem]
            [recursiveui.component :as component]
            [recursiveui.componentmap :as cmap :refer [tag->fn]]
            [recursiveui.listeners :as listeners]
            [recursiveui.types :as types :refer [base-element]]
            [recursiveui.data :as data]
            [cljs.core.async :as acync :refer [chan put!]]
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




(defn render
  [{:keys [element/attr
           element/style
           element/type
           children]
    :as node}]
  (into [(or type :div) (merge attr {:style style})]
        (map render children)))




(defn listen
  ([{:keys [tags] :as node}]
   (let [xf (reduce comp tags)]
     (if (empty? children) ch
         (pipe (acync/merge (map render children))
               (chan 10 xf))))))
