(ns recursiveui.listeners
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [chan put! <!]]
            [recursiveui.data :as data]
            [recursiveui.command :as command :refer [update! update-node!]]
            [recursiveui.element :as elem :refer [attr style class tag]]
            [goog.events :as gevents :refer [listen unlisten]]
            [cljs.pprint :refer [pprint]]))


(def channel (chan 10))
(declare perform-drag)


(defn layout-resize-source [x]
  (attr x
        :onMouseDown
        (fn [e]
          (.persist e)
          (aset e "eventname" :layout/resize)
          e)))




(defn layout-resize-handler
  [{:keys [layout/partition
           node]
    :as x}]
  (attr x
        :onMouseDown
        (fn [e]
          (when (= (.-eventname e) :layout/resize)
            (put! channel
                  {:node node
                   :event e
                   :name :drag
                   :command (if (= partition :row)
                              command/layout-resize-height
                              command/layout-resize-width)}))
          e)))





(defn layout-resize-root-handler
  [{:keys [node]
    :as x}]
  (attr x
        :onMouseDown
        (fn [e]
          (.persist e)
          (when-let [f (case (.-eventname e)
                         :layout/resize-left   command/resize-root-left
                         :layout/resize-top    command/resize-root-top
                         :layout/resize-bottom command/resize-root-bottom
                         :layout/resize-right  command/resize-root-right
                         nil)]
            (put! channel
                  {:node node
                   :event e
                   :name :drag
                   :command f}))
          e)))



(defn layout-resize-left-source [x]
  (attr x
        :onMouseDown
        (fn [e]
          (aset e "eventname" :layout/resize-left)
          e)))




(defn layout-resize-right-source [x]
  (attr x
        :onMouseDown
        (fn [e]
          (aset e "eventname" :layout/resize-right)
          e)))




(defn layout-resize-top-source [x]
  (attr x
        :onMouseDown
        (fn [e]
          (aset e "eventname" :layout/resize-top)
          e)))




(defn layout-resize-bottom-source [x]
  (attr x
        :onMouseDown
        (fn [e]
          (aset e "eventname" :layout/resize-bottom)
          e)))




(defn conjoin-action-source [x]
  (attr x
        :onClick
        (fn [e]
          (aset e "eventname" :conjoin)
          e)))



(defn fullsize-action-source [x]
  (attr x
        :onClick
        (fn [e]
          (aset e "eventname" :fullsize)
          e)))



(defn layout-fullsize-action-handler
  ([{:keys [fullscreen?
            previous-width   layout/width
            previous-height  layout/height
            previous-top     layout/top
            previous-left    layout/left
            node]
     :as x}]
   (attr x
         :onClick
         (fn [e]
           (when (= (.-eventname e) :fullsize)
             (if fullscreen?
               (-> (update-node! node
                                 command/layout-resize-root
                                 previous-width
                                 previous-height)

                   (update-node! assoc
                                 :layout/width    previous-width
                                 :layout/height   previous-height
                                 :layout/top      previous-top
                                 :layout/left     previous-left
                                 :fullscreen?     false))
               
               (-> (assoc node
                          :previous-width  width
                          :previous-height height
                          :previous-top    top
                          :previous-left   left
                          :fullscreen?     true)
                   
                   (update-node! command/layout-fullscreen))))
           e))))





(defn delete-action-source
  [{:keys [node]
    :as x}]
  (attr x
        :onClick
        (fn [e]
          (println "event-name" (aget e "eventname")
                   (aget e "layout-delete-node"))
          (aset e "eventname" :delete)
          (aset e "node" node)
          e)))



(defn delete-action-handler
  [{:keys [node]
    :as x}]
  (attr x
        :onClick
        (fn [e]
          (when (= (.-eventname e) :delete)
            (aset e "deletenode" node)
            (aset e "deleteparent" (command/delete-node! node)))
          e)))




(defn layout-delete-handler
  [{:keys [node layout/partition]
    :as x}]
  (attr x
        :onClick
        (fn [e]
          (cond (aget e "layout-delete-node")
                (let [deleted (.-deletenode e)
                      parent  (.-deleteparent e)]
                  (aset e "layout-delete-node" false)
                  (update-node! parent
                                (if (= partition :row)
                                  command/layout-update-width
                                  command/layout-update-height)
                                (:layout/magnitude deleted)))

                (= (.-eventname e) :delete)
                (aset e "layout-delete-node" true))
          e)))






(defn layout-drag-handler
  [{:keys [node
           fullscreen?]
    :as x}]
  (attr x
        :onMouseDown
        (fn [e]
          (when (and (= (.-eventname e) :layout-drag)
                     (not fullscreen?))
            (perform-drag {:node     node
                           :event    e
                           :client-x (.-clientX e)
                           :client-y (.-clientY e)
                           :command  command/layout-drag}))
          e)))
 



(defn layout-drag-source [x]
  (attr x
        :onMouseDown
        (fn [e]
          (aset e "eventname" :layout-drag))))





(defn perform-drag
  [{:keys [event client-x client-y] :as msg}]
  (let [prev (atom [(or client-x (.-clientX event))
                    (or client-y (.-clientY event))])]
    (letfn [(drag-listener [e]
              (let [x (.-clientX e)
                    y (.-clientY e)
                    value @prev
                    dx (- x (value 0))
                    dy (- y (value 1))]
                (command/update! (assoc msg
                                        :delta/dx dx
                                        :delta/dy dy))
                (reset! prev [x y])))
            (unlisten-f [e]
              (unlisten js/window
                        "mousemove"
                        drag-listener))]
      (listen js/window "mouseup" unlisten-f)
      (listen js/window "mousemove" drag-listener))))



(defn init-chan []
  (go-loop []
    (let [{:keys [name] :as msg} (<! channel)]
      (case name
        :drag (perform-drag msg)
        (command/update! msg))
      (recur))))





(init-chan)
