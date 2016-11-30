(ns recursiveui.data
  (:require [reagent.core :as reagent]
            [cljs.core.async :as async :refer
             [chan dropping-buffer]]))


(def state
  (reagent/atom
   {:tags #{:structure/style
            :structure/flex-root}
    :element/type :div
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
    :children [{:tags #{:structure/flex-row}
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
                :children [{:tags #{:structure/conjoin-button}
                            :traverse/render? true}
                           {:tags #{:structure/flex-column}
                            :element/type :div
                            :element/style {:backgroundColor "blue"}
                            :component/id 3
                            :traverse/render? true
                            :layout/partition :column
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :layout/magnitude 200
                            :layout/min-magnitude 50
                            :style/backgroundColor "blue"}
                           {:tags #{:structure/flex-column}
                            :component/id 4
                            :traverse/render? true
                            :element/style {:backgroundColor "grey"}
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :style/backgroundColor "grey"
                            :children [{:tags #{:structure/layout-sidebar}
                                        :element/type :div
                                        :element/union? true
                                        :traverse/render? true}]}
                           {:tags #{:structure/flex-column}
                            :component/id 5
                            :traverse/render? true
                            :element/style {:backgroundColor "grey"}
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true}
                           {:tags #{:structure/flex-column}
                            :component/id 5
                            :element/style {:backgroundColor "grey"}
                            :traverse/render? true
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :children [{:tags #{:structure/layout-sidebar}
                                        :element/events {:onClick :layout/resize}
                                        :layout/resize-event? true
                                        :element/type :div
                                        :element/union? true
                                        :traverse/render? true}]}]}
               {:tags #{:structure/flex-row}
                :layout/partition :row
                :component/id 6
                :element/style {:backgroundColor "orange"}
                :traverse/render? true
                :layout/active? true
                :layout/inner? true
                :layout/magnitude 600
                :layout/min-magnitude 60
                :layout/variable? true
                :children [{:tags #{:structure/layout-sidebar}
                            :events #{:onClick
                                      :onDoubleClick
                                      :onZoom}
                            :element/type :div
                            :element/union? true
                            :traverse/render? true}]}
               {:tags #{:structure/flex-root
                        :structure/border}
                :component/id 7
                :traverse/render? true
                :element/style {:backgroundColor "grey"}
                :layout/partition :column
                :layout/active? true
                :layout/inner? false
                :layout/variable? true
                :layout/width 500
                :layout/height 700
                :layout/top 100
                :layout/left 100
                :style/backgroundColor "grey"
                :children [{:tags #{}
                            :element/style {:width "100%"
                                            :height 40
                                            :top -3
                                            :left -3
                                            :position "absolute"
                                            :backgroundColor "grey"
                                            :zIndex 5
                                            :borderColor "#181319"
                                            :border "solid"}
                            :traverse/render? true}
                           {:tags #{:structure/flex-row
                                    :structure/style}
                            :component/id 8
                            :traverse/render? true
                            :layout/partition :row
                            :layout/magnitude 100
                            :layout/min-magnitude 60
                            :layout/active? true
                            :layout/variable? false
                            :layout/inner? true
                            :style/backgroundColor "green"
                            :children [{:tags #{:structure/flex-column
                                                :structure/style}
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
                                       {:tags #{:structure/flex-column
                                                :structure/style}
                                        :component/id 10
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/min-magnitude 45
                                        :layout/active? true
                                        :layout/variable? false
                                        :layout/inner? true
                                        :style/backgroundColor "grey"}
                                       {:tags #{:structure/flex-column
                                                :structure/style}
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
                                   :structure/style]
                            :component/id 12
                            :traverse/render? true
                            :layout/partition :row
                            :layout/active? true
                            :layout/inner? true
                            :layout/magnitude 600
                            :layout/min-magnitude 60
                            :style/backgroundColor "green"
                            :layout/variable? true}]}]}))
