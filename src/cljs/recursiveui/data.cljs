(ns recursiveui.data
  (:require [reagent.core :as reagent]))


(def state
  (reagent/atom
   {:tags #{:structure/flex-root}
    :element/style {:backgroundColor "red"}
    :element/attr {:onClick
                   (fn [e] (aset e "eventname" nil))}
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
    :children [{:tags #{:structure/flex-row
                        :listeners/layout-delete-handler}
                :component/id 2
                :element/style {:backgroundColor "green"}
                :traverse/render? true
                :layout/partition :row
                :layout/magnitude 100
                :layout/min-magnitude 40
                :layout/max-magnitude 400
                :layout/active? true
                :layout/variable? true
                :layout/inner? true
                :children [{:tags #{:structure/flex-column
                                    :listeners/layout-delete-handler
                                    :listeners/delete-action-handler}
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
                            :style/backgroundColor "blue"
                            :children [{:tags #{:listeners/delete-action-source
                                                :structure/action-button}
                                        :traverse/render? true}]}
                           {:tags #{:structure/flex-column
                                    :listeners/layout-resize-handler
                                    :listeners/layout-delete-handler
                                    :listeners/delete-action-handler}
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
                            :children [{:tags #{:listeners/delete-action-source
                                                :structure/action-button}
                                        :traverse/render? true}
                                       {:tags #{:structure/sidebar-left
                                                :listeners/layout-resize-source}
                                        :traverse/render? true}]}
                           {:tags #{:structure/flex-column
                                    :listeners/layout-resize-handler
                                    :listeners/layout-delete-handler
                                    :listeners/delete-action-handler}
                            :component/id 5
                            :traverse/render? true
                            :element/style {:backgroundColor "grey"}
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :children [{:tags #{:structure/action-button
                                                :listeners/delete-action-source}
                                        :traverse/render? true}
                                       {:tags #{:structure/sidebar-left
                                                :listeners/layout-resize-source}
                                        :traverse/render? true}]}
                           {:tags #{:structure/flex-column
                                    :listeners/layout-resize-handler
                                    :listeners/layout-delete-handler
                                    :listeners/delete-action-handler}
                            :component/id 5
                            :element/style {:backgroundColor "grey"}
                            :traverse/render? true
                            :layout/partition :column
                            :layout/magnitude 100
                            :layout/min-magnitude 20
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :children [{:tags #{:structure/action-button
                                                :listeners/delete-action-source}
                                        :traverse/render? true}
                                       {:tags #{:structure/sidebar-left
                                                :listeners/layout-resize-source}
                                        :messages {:onMouseDown :layout/resize}
                                        :traverse/render? true}]}]}
               {:tags #{:structure/flex-row
                        :listeners/layout-resize-handler}
                :layout/partition :row
                :component/id 6
                :element/style {:backgroundColor "orange"}
                :traverse/render? true
                :layout/active? true
                :layout/inner? true
                :layout/magnitude 600
                :layout/min-magnitude 60
                :layout/variable? true
                :children [{:tags #{:structure/sidebar-top
                                    :listeners/layout-resize-source}
                            :events #{:layout/resize}
                            :traverse/render? true}]}
               {:tags #{:component/resizable-flex-root
                        :structure/flex-root-action-bar}
                :component/id 7
                :traverse/render? true
                :element/style {:backgroundColor "grey"}
                :fullsize? false
                :layout/partition :column
                :layout/active? true
                :layout/inner? false
                :layout/variable? true
                :layout/width 500
                :layout/height 700
                :layout/top 100
                :layout/left 100
                :children [{:tags #{:structure/flex-row}
                            :component/id 8
                            :element/style {:backgroundColor "green"}
                            :traverse/render? true
                            :layout/partition :row
                            :layout/magnitude 100
                            :layout/min-magnitude 60
                            :layout/active? true
                            :layout/variable? true
                            :layout/inner? true
                            :children [{:tags #{:structure/flex-column}
                                        :element/style {:backgroundColor "blue"}
                                        :component/id 9
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :layout/magnitude 200
                                        :layout/max-magnitude 300
                                        :layout/min-magnitude 50}
                                       {:tags #{:structure/flex-column
                                                :listeners/layout-resize-handler}
                                        :element/style {:backgroundColor "grey"}
                                        :component/id 10
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/min-magnitude 45
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :children [{:tags #{:listeners/layout-resize-source
                                                            :structure/sidebar-left}
                                                    :traverse/render? true}]}
                                       {:tags #{:structure/flex-column
                                                :listeners/layout-resize-handler}
                                        :element/style {:backgroundColor "grey"}
                                        :component/id 11
                                        :traverse/render? true
                                        :layout/partition :column
                                        :layout/magnitude 150
                                        :layout/min-magnitude 50
                                        :layout/active? true
                                        :layout/variable? true
                                        :layout/inner? true
                                        :children [{:tags #{:structure/sidebar-left
                                                            :listeners/layout-resize-source}
                                                    :traverse/render? true}]}]}
                           {:tags #{:structure/flex-row
                                    :listeners/layout-resize-handler}
                            :component/id 12
                            :element/style {:backgroundColor "green"}
                            :traverse/render? true
                            :layout/partition :row
                            :layout/active? true
                            :layout/inner? true
                            :layout/magnitude 600
                            :layout/min-magnitude 60
                            :layout/variable? true
                            :children [{:tags #{:structure/sidebar-top
                                                :listeners/layout-resize-source}
                                        :traverse/render? true}]}]}]}))
