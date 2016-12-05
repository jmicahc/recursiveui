(ns recursiveui.listeners
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [chan put! <!]]
            [recursiveui.data :as data]
            [recursiveui.command :as command :refer [update! update-node!]]
            [recursiveui.element :as elem :refer [attr style class tag event]]
            [goog.events :as gevents :refer [listen unlisten]]
            [cljs.pprint :refer [pprint]]))


(declare perform-drag)


(defn save-actions [x]
  (event x
         :delete
         (mapcat (fn [msg] [{:event-name :save-state} msg]))
         :duplicate
         (mapcat (fn [msg] [{:event-name :save-state} msg]))))


(defn resize-source [x]
  (event x :onMouseDown
         (fn [msg] (assoc msg :event-name :resize))))



(defn duplicate-source [x]
  (event x :onMouseDown
         (map (fn [msg] (assoc msg :event-name :duplicate)))))



(defn duplicate-handler [x]
  (event x :duplicate
         (map (fn [msg] (assoc msg :child (:node x))))))



(defn duplicate-sink [x]
  (event x :duplicate
         (map (fn [msg]
                (assoc msg :parent (:node x))))))



(defn layout-resize-target [x]
  (event x :resize
         (mapcat (fn [msg]
                   [msg (assoc msg
                               :name  :layout-resize-sink
                               :child (:node x))]))))

(defn layout-resize-sink
  [{:keys [node]
    :as x}]
  (event x
         :layout-resize-sink
         (map (fn [msg]
                (assoc msg :parent node)))))



(defn layout-resize-root-handler
  [{:keys [node]
    :as x}]
  (event x
         :layout-resize-root
         (map (fn [msg] (assoc msg :node node)))))




(defn layout-resize-left-source [x]
  (event x :onMouseDown
         (map (fn [msg]
                (assoc msg
                       :event-name  :layout-resize-root
                       :resize-side :left)))))



(defn layout-resize-right-source [x]
  (event x :onMouseDown
         (map (fn [msg]
                (assoc msg
                       :event-name :layout-resize-root
                       :resize-side :right)))))



(defn layout-resize-top-source [x]
  (event x :onMouseDown
         (map (fn [msg]
                (assoc msg
                       :event-name   :layout-resize-root
                       :resize-side  :top)))))



(defn layout-resize-bottom-source [x]
  (event x :onMouseDown
         (map (fn [msg]
                (assoc msg
                       :event-name :layout-resize-root
                       :layout-resize-root
                       :resize-side :bottom)))))



(defn conjoin-action-source [x]
  (event x :onClick
         (map (fn [msg]
                 (assoc msg
                        :event-name :conjoin)))))



(defn conjoin-action-handler
  [{:keys [node] :as x}]
  (event x :conjoin
         (map (fn [msg]
                (assoc msg :child node)))))



(defn conjoin-action-sink
  [{:keys [node] :as x}]
  (event x :conjoin
         (map (fn [msg]
                (assoc msg :node node)))))




(defn fullsize-source [x]
  (event x :onClick
         (map (fn [msg]
                (assoc msg :event-name :fullsize)))))



(defn fullsize-handler
  ([{:keys [fullscreen?
            node]
     :as x}]
   (event x :fullsize
          (map (fn [msg]
                 (assoc msg
                        :node node
                        :minimize? (if fullscreen? true false)))))))



(defn delete-source
  [{:keys [node]
    :as x}]
  (event x :onClick
         (map (fn [msg]
                (println "@delete-source")
                (assoc msg :event-name :delete)))))



(defn delete-handler
  [{:keys [node]
    :as x}]
  (event x :delete
        (map (fn [msg]
               (println "@delete-handler")
               (assoc msg :child node)))))


(defn delete-sink
  [{:keys [node] :as x}]
  (event x :delete
        (map (fn [msg]
               (println "@delete-sink")
               (assoc msg :parent node)))))



(defn layout-delete
  [{:keys [node layout/partition]
    :as x}]
  (event x :delete
         (mapcat (fn [msg]
                   [msg (assoc msg :event-name :layout-delete)]))))



(defn layout-drag
  [{:keys [node
           fullscreen?]
    :as x}]
  (event x :drag
         (mapcat (fn [msg]
                   [msg (assoc msg :event-name :layout-drag)]))))



(defn undo-source [x]
  (event x :onClick
         (map (fn [msg]
                (println "@undo-source")
                (assoc msg :event-name :undo)))))
