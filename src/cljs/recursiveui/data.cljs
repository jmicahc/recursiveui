(ns recursiveui.data
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async :refer
             [chan dropping-buffer]]))


(def mutate-chan (chan (dropping-buffer 100)))
(def debounced-mutate (chan (dropping-buffer 1)))
(def debounce-interval 100)


(def state
  (reagent/atom
   {:tags [:layout/root :style/root]
    :layout/partition :column
    :layout/term :var
    :id 1
    :style/width 500
    :style/height 700
    :style/top 0
    :style/left 0
    :style/backgroundColor "red"
    :children [{:tags [:layout/row :style/root]
                :id 2
                :layout/partition :row
                :layout/magnitude 100
                :layout/term :var
                :layout-resize-element/backgroundColor "red"
                :style/backgroundColor "green"
                :children [{:tags [:layout/column :style/root]
                            :layout/partition :column
                            :layout/term :constant
                            :id 4
                            :layout/magnitude 200
                            :style/backgroundColor "blue"}
                           {:tags [:layout/column :style/root]
                            :id 5
                            :layout/partition :column
                            :layout/magnitude 300
                            :layout/term :var
                            :style/backgroundColor "grey"}]}
               {:tags [:layout/row :event/root :style/root]
                :layout-resize-element/backgroundColor "red"x
                :layout/partition :row
                :layout/magnitude 600
                :style/backgroundColor "orange"
                :id 3
                :layout/term :var}]}))

