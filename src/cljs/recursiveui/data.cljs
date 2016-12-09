(ns recursiveui.data
  (:require [reagent.core :as reagent]
            [cljs.pprint :refer [pprint]]
            [cljs.core.async :refer [chan]]))




(def root-channel (chan))


(def state
  (atom
   {:traverse/render? true
    :element/attr {:id "root"}
    :children
    [{:tags #{:structure/flex-root
              :layout/column-leaf-decorator
              :layout/layout-handler
              :component/root-drag-handler
              #_:listeners/undo-action-handler}
      :element/style {:backgroundColor "red"}
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
      :children [{:tags #{:listeners/delete-sink
                          :layout/resizable-flex-row
                          :listeners/duplicate-sink}
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
                                      #_:listeners/layout-delete-handler
                                      #_:listeners/delete-action-handler}
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
                              :children [{:tags #{#_:listeners/delete-action-source
                                                  :structure/action-button}
                                          :component/id 40
                                          :traverse/render? true
                                          :children [{}]}]}
                             {:tags #{:layout/resizable-flex-column
                                      :listeners/delete-handler
                                      :listeners/duplicate-handler}
                              :component/id 4
                              :traverse/render? true
                              :element/style {:backgroundColor "purple"}
                              :layout/partition :column
                              :layout/magnitude 100
                              :layout/min-magnitude 20
                              :layout/active? true
                              :layout/variable? true
                              :layout/inner? true
                              :style/backgroundColor "grey"
                              :children [{:tags #{:structure/button
                                                  :listeners/delete-source
                                                  #_:listeners/duplicate-action}
                                          :component/id 30
                                          :traverse/render? true}
                                         {:tags #{:structure/action-button
                                                  :listeners/duplicate-source}
                                          :component/id 31
                                          :traverse/render? true}
                                         #_{:tags #{:structure/sidebar-left
                                                  :sources/resize
                                                  #_:listeners/layout-resize}
                                          :traverse/render? true}]}
                             {:tags #{#_:structure/flex-column
                                      :layout/resizable-flex-column
                                      #_:listeners/layout-resize
                                      #_:listeners/layout-delete-handler
                                      #_:listeners/delete-action-handler}
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
                                                  #_:listeners/delete-action-source}
                                          :traverse/render? true}
                                         #_{:tags #{:structure/sidebar-left
                                                  #_:listeners/layout-resize}
                                          :traverse/render? true}]}
                             {:tags #{#_:structure/flex-column
                                      :layout/resizable-flex-column
                                      #_:listeners/layout-resize
                                      #_:listeners/layout-delete-handler
                                      #_:listeners/delete-action-handler}
                              :component/id 5
                              :element/style {:backgroundColor "grey"}
                              :traverse/render? true
                              :layout/partition :column
                              :layout/magnitude 100
                              :layout/min-magnitude 20
                              :layout/active? true
                              :layout/variable? true
                              :layout/inner? true
                              :children [{:tags #{:structure/action-button}
                                          :traverse/render? true}]}]}
                 {:tags #{:layout/resizable-flex-row}
                  :layout/partition :row
                  :component/id 6
                  :element/style {:backgroundColor "orange"}
                  :traverse/render? true
                  :layout/active? true
                  :layout/inner? true
                  :layout/magnitude 600
                  :layout/min-magnitude 60
                  :layout/variable? true
                  :children [{:tags #{:structure/action-button
                                      :listeners/undo-source}
                              :traverse/render? true}
                             {:tags #{:sources/pretty-print-state}
                              :element/type "button"
                              :element/inner-html "print state"
                              :element/style {:position :absolute
                                              :top "70px"}
                              :traverse/render? true}
                             {:tags #{:sources/save-state}
                              :element/type "button"
                              :element/inner-html "save state"
                              :element/style {:position :absolute
                                              :top "95px"}
                              :traverse/render? true}]}
                 {:tags #{:layout/resizable-flex-root
                          :layout/column-leaf-decorator
                          :targets/drag
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
                  :children [{:tags #{#_:structure/flex-row
                                      :layout/resizable-flex-row
                                      #_:listeners/duplicate}
                              :component/id 8
                              :element/style {:backgroundColor "green"}
                              :traverse/render? true
                              :layout/partition :row
                              #_:layout/magnitude #_300
                              :layout/min-magnitude 60
                              :layout/active? true
                              :layout/variable? true
                              :layout/inner? true
                              :children [{:tags #{:layout/resizable-flex-column
                                                  #_:layout/resizable-flex-column}
                                          :element/style {:backgroundColor "blue"}
                                          :component/id 9
                                          :traverse/render? true
                                          :layout/partition :column
                                          :layout/active? true
                                          :layout/variable? true
                                          :layout/inner? true
                                          :layout/magnitude 200
                                          :layout/max-magnitude 800
                                          :layout/min-magnitude 50
                                          :children [{:tags #{:layout/resizable-flex-row}
                                                      :element/style {:backgroundColor "green"}
                                                      :traverse/render? true
                                                      :layout/partition :row
                                                      :layout/active? true
                                                      :layout/variable? true
                                                      :layout/inner? true
                                                      :layout/magnitude 100
                                                      :layout/min-magnitude 50
                                                      :children [#_{:tags #{:layout/resizable-flex-column}
                                                                  :traverse/render? true
                                                                  :element/style {:backgroundColor "green"}
                                                                  :layout/partition :column
                                                                  :layout/active? true
                                                                  :layout/variable? true
                                                                  :layout/inner? true
                                                                  :layout/magnitude 100
                                                                  :layout/min-magnitude 50
                                                                  :children []}
                                                                 #_{:tags #{:layout/resizable-flex-column}
                                                                  :traverse/render? true
                                                                  :element/style {:backgroundColor "green"}
                                                                  :layout/partition :column
                                                                  :layout/active? true
                                                                  :layout/variable? true
                                                                  :layout/inner? true
                                                                  :layout/magnitude 100
                                                                  :layout/min-magnitude 50
                                                                  :children []}]}
                                                     
                                                     {:tags #{:layout/resizable-flex-row}
                                                      :element/style {:backgroundColor "green"}
                                                      :component/id 42
                                                      :traverse/render? true
                                                      :layout/partition :row
                                                      :layout/variable? true
                                                      :layout/active? true
                                                      :layout/inner? true
                                                      :layout/magnitude 100
                                                      :layout/min-magnitude 50
                                                      :children [{:tags #{:sources/layout-add-partition
                                                                          :structure/button}
                                                                  :component/id 44
                                                                  :traverse/render? true}]}
                                                     {:tags #{:layout/resizable-flex-row}
                                                      :element/style {:backgroundColor "green"}
                                                      :component/id 43
                                                      :traverse/render? true
                                                      :layout/partition :row
                                                      :layout/variable? true
                                                      :layout/active? true
                                                      :layout/inner? true
                                                      :layout/magnitude 100
                                                      :layout/min-magnitude 50
                                                      :children []}
                                                     #_{:tags #{:structure/button}
                                                      :component/id 20
                                                      :traverse/render? true}]}
                                         {:tags #{#_:structure/flex-column
                                                  :layout/resizable-flex-column}
                                          :element/style {:backgroundColor "grey"}
                                          :component/id 10
                                          :traverse/render? true
                                          :layout/partition :column
                                          :layout/magnitude 150
                                          :layout/min-magnitude 45
                                          :layout/active? true
                                          :layout/variable? true
                                          :layout/inner? true
                                          :children [#_{:tags #{:structure/sidebar-left}
                                                        :traverse/render? true}
                                                     {:tags #{:structure/button}
                                                      :traverse/render? true}]}
                                         {:tags #{#_:structure/flex-column
                                                  :layout/resizable-flex-column}
                                          :element/style {:backgroundColor "grey"}
                                          :component/id 11
                                          :traverse/render? true
                                          :layout/partition :column
                                          :layout/magnitude 150
                                          :layout/min-magnitude 50
                                          :layout/active? true
                                          :layout/variable? true
                                          :layout/inner? true
                                          :children [#_{:tags #{:structure/sidebar-left}
                                                        :traverse/render? true}]}]}
                             {:tags #{#_:structure/flex-row
                                      :layout/resizable-flex-row
                                      #_:listeners/layout-resize-handler}
                              :component/id 12
                              :element/style {:backgroundColor "green"}
                              :traverse/render? true
                              :layout/partition :row
                              :layout/active? true
                              :layout/inner? true
                              :layout/magnitude 400
                              :layout/min-magnitude 60
                              :layout/variable? true
                              :children [{:tags #{#_:structure/sidebar-top
                                                  #_:listeners/layout-resize-source}
                                          :traverse/render? true}]}]}]}]}))




(def root-typology (atom {}))
(def typologies (atom {}))


(def to-typology
  (let [cache (atom {})]
    (fn [[id children :as arg]]
      (if-let [value (@cache arg)]
        value
        (let [ret {:component/id id
                   :children children}]
          (swap! cache assoc arg ret)
          (swap! typologies assoc id ret)
          ret)))))



(defn walk [{:keys [component/id children] :as node}]
  (if (empty? children) []
      (to-typology [id (mapv walk children)])))



(add-watch state :k (fn [_ _ old new]
                      (swap! root-typology
                             (fn [typology] (walk new)))))

(def memory
  (atom {:root-timeline []}))

