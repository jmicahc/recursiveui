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
    :traverse/render? true
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
                       :structure/style
                       :structure/conjoin-button]
                :component/id 2
                :traverse/render? true
                :layout/partition :row
                :layout/magnitude 100
                :layout/min-magnitude 40
                :layout/max-magnitude 400
                :layout/active? true
                :layout/variable? true
                :layout/inner? true
                :style/backgroundColor "green"
                :children [{:tags [:structure/flex-column
                                   :structure/style]
                            :component/id 3
                            :traverse/render? true
                            :layout/partition :column
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :layout/magnitude 200
                            :layout/min-magnitude 50
                            :style/backgroundColor "blue"}
                           {:tags [:component/resize-layout
                                   :structure/flex-column
                                   :structure/style]
                            :component/id 4
                            :traverse/render? true
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"}
                           {:tags [:structure/flex-column
                                   :structure/style
                                   :component/resize-layout]
                            :component/id 5
                            :traverse/render? true
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"}
                           {:tags [:structure/flex-column
                                   :structure/style
                                   :component/resize-layout]
                            :component/id 5
                            :traverse/render? true
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"}]}
               {:tags [:structure/flex-row
                       :structure/style
                       :component/resize-layout]
                :layout/partition :row
                :component/id 6
                :traverse/render? true
                :layout/active? true
                :layout/inner? true
                :layout/magnitude 600
                :layout/min-magnitude 60
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
                :traverse/render? true
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
                            :traverse/render? true
                            :layout/partition :row
                            :layout/magnitude 100
                            :layout/min-magnitude 60
                            :layout/active? true
                            :layout/variable? false
                            :layout/inner? true
                            :style/backgroundColor "green"
                            :children [{:tags [:structure/flex-column
                                               :structure/style]
                                        :component/id 9
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :layout/magnitude 200
                                        :layout/max-magnitude 300
                                        :layout/min-magnitude 50
                                        :style/backgroundColor "blue"}
                                       {:tags [:structure/flex-column
                                               :structure/style
                                               :component/resize-layout]
                                        :component/id 10
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/min-magnitude 45
                                        :layout/active? true
                                        :layout/variable? false
                                        :layout/inner? true
                                        :style/backgroundColor "grey"}
                                       {:tags [:structure/flex-column
                                               :structure/style
                                               :component/resize-layout]
                                        :component/id 11
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/min-magnitude 50
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :style/backgroundColor "grey"}]}
                           {:tags [:structure/flex-row
                                   :structure/style
                                   :component/resize-layout]
                            :component/id 12
                            :traverse/render? true
                            :layout/partition :row
                            :layout/active? true
                            :layout/inner? true
                            :layout/magnitude 600
                            :layout/min-magnitude 60
                            :style/backgroundColor "green"
                            :layout/variable? true}]}]}))
