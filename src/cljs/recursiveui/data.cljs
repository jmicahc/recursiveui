(ns recursiveui.data
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async :refer
             [chan dropping-buffer]]))


(def mutate-chan (chan (dropping-buffer 100)))
(def debounced-mutate (chan (dropping-buffer 1)))
(def debounce-interval 100)


(def state
  (reagent/atom
   {:tags [:structure/style :structure/flex-root]
    :type :basic-element
    :component/id 1
    :layout/inner? false
    :layout/partition :column
    :layout/variable? true
    :layout/width 500
    :layout/active? true
    :layout/height 700
    :layout/top 0
    :layout/left 0
    :style/backgroundColor "red"
    :children [{:tags [:structure/flex-row
                       :structure/style]
                :component/id 2
                :layout/partition :row
                :layout/magnitude 100
                :layout/active? true
                :layout/variable? true
                :layout/inner? true
                :style/backgroundColor "green"
                :children [{:tags [:structure/flex-column
                                   :structure/style]
                            :component/id 3
                            :layout/partition :column
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :layout/magnitude 200
                            :style/backgroundColor "blue"}
                           {:tags [:component/resize-layout
                                   :structure/flex-column
                                   :structure/style]
                            :component/id 4
                            :layout/partition :column
                            :layout/magnitude 150
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"}
                           {:tags [:structure/flex-column
                                   :structure/style
                                   :component/resize-layout]
                            :component/id 5
                            :layout/partition :column
                            :layout/magnitude 150
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"}]}
               {:tags [:structure/flex-row
                       :structure/style
                       :component/resize-layout]
                :layout/partition :row
                :component/id 6
                :layout/active? true
                :layout/inner? true
                :layout/magnitude 600
                :style/backgroundColor "orange"
                :layout/variable? true}
               {:tags [:structure/flex-root
                       :structure/flex-root-action-bar
                       :component/layout-resize-root-left
                       :component/layout-resize-root-right
                       :component/layout-resize-root-bottom
                       :component/layout-resize-root-top
                       :structure/style
                       :structure/border]
                :component/id 7
                :layout/partition :column
                :layout/active? true
                :layout/inner? false
                :layout/variable? true
                :layout/width 500
                :layout/height 700
                :layout/top 100
                :layout/left 100
                :style/backgroundColor "grey"
                :children [{:tags [:structure/flex-row
                                   :structure/style]
                            :component/id 8
                            :layout/partition :row
                            :layout/magnitude 100
                            :layout/active? true
                            :layout/variable? false
                            :layout/inner? true
                            :style/backgroundColor "green"
                            :children [{:tags [:structure/flex-column
                                               :structure/style]
                                        :component/id 9
                                        :layout/partition :column
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :layout/magnitude 200
                                        :style/backgroundColor "blue"}
                                       {:tags [:structure/flex-column
                                               :structure/style
                                               :component/resize-layout]
                                        :component/id 10
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/active? true
                                        :layout/variable? false
                                        :layout/inner? true
                                        :style/backgroundColor "grey"}
                                       {:tags [:structure/flex-column
                                               :structure/style
                                               :component/resize-layout]
                                        :component/id 11
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :style/backgroundColor "grey"}]}
                           {:tags [:structure/flex-row
                                   :structure/style
                                   :component/resize-layout]
                            :component/id 12
                            :layout/partition :row
                            :layout/active? true
                            :layout/inner? true
                            :layout/magnitude 600
                            :style/backgroundColor "orange"
                            :layout/variable? true}]}]}))
