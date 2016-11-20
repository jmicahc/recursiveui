(ns recursiveui.data
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async :refer
             [chan dropping-buffer]]))


(def mutate-chan (chan (dropping-buffer 100)))
(def debounced-mutate (chan (dropping-buffer 1)))
(def debounce-interval 100)


(def state
  (reagent/atom
   {:tags [:structure/flex-root :structure/style]
    :layout/partition :column
    :layout/variable? true
    :id 1
    :layout/width 500
    :layout/height 700
    :layout/top 0
    :layout/left 0
    :style/backgroundColor "red"
    :children [{:tags [:structure/flex-row
                       :structure/style]
                :id 2
                :layout/partition :row
                :layout/magnitude 100
                :layout/variable? true
                :layout-resize-element/backgroundColor "red"
                :style/backgroundColor "green"
                :children [{:tags [:structure/flex-column
                                   :structure/style]
                            :layout/partition :column
                            :layout/variable? false
                            :id 4
                            :layout/magnitude 200
                            :style/backgroundColor "blue"}
                           {:tags [:structure/flex-column
                                   :structure/style
                                   :structure/sidebar-right]
                            :id 5
                            :layout/partition :column
                            :layout/magnitude 300
                            :layout/variable? true
                            :style/backgroundColor "grey"}]}
               {:tags [:structure/flex-row :event/root :structure/style]
                :layout-resize-element/backgroundColor "red"
                :layout/partition :row
                :layout/magnitude 600
                :style/backgroundColor "orange"
                :id 3
                :layout/variable? true}]}))

