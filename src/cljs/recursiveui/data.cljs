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
                :layout/partition :row
                :layout/magnitude 100
                :layout/active? true
                :layout/variable? true
                :layout/inner? true
                :style/backgroundColor "green"
                :children [{:tags [:structure/flex-column
                                   :structure/style]
                            :layout/partition :column
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :layout/magnitude 200
                            :style/backgroundColor "blue"}
                           {:tags [:structure/flex-column
                                   :structure/style
                                   :component/resize-layout]
                            :layout/partition :column
                            :layout/magnitude 150
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"}
                           {:tags [:structure/flex-column
                                   :structure/style
                                   :component/resize-layout]
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
                :layout/active? true
                :layout/inner? true
                :layout/magnitude 600
                :style/backgroundColor "orange"
                :layout/variable? true}
               {:tags [:structure/flex-root
                       :structure/style
                       :structure/border]
                :layout/partition :column
                :layout/active? true
                :layout/innter? false
                :layout/variable? true
                :layout/width 500
                :layout/height 700
                :layout/top 100
                :layout/left 100
                :style/backgroundColor "grey"
                :children [{:tags [:structure/flex-row
                                   :structure/style]
                            :layout/partition :row
                            :layout/magnitude 100
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "green"
                            :children [{:tags [:structure/flex-column
                                               :structure/style]
                                        :layout/partition :column
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :layout/magnitude 200
                                        :style/backgroundColor "blue"}
                                       {:tags [:structure/flex-column
                                               :structure/style
                                               :component/resize-layout]
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :style/backgroundColor "grey"}
                                       {:tags [:structure/flex-column
                                               :structure/style
                                               :component/resize-layout]
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
                            :layout/active? true
                            :layout/inner? true
                            :layout/magnitude 600
                            :style/backgroundColor "orange"
                            :layout/variable? true}]}]}))

