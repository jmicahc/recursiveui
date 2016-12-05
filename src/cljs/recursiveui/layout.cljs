(ns recursiveui.layout
  (:require [recursiveui.element :as elem :refer [event]]
            [recursiveui.command :as command :refer [dispatch]]
            [recursiveui.data :as data]
            [recursiveui.util :as util :refer [subtree]]))


(defn layout-handler
  [{:keys [node] :as x}]
  (event x
         :delete
         (mapcat (fn [msg]
                   [msg (assoc msg
                               :event-name :layout-delete
                               :child (:node x))]))


         :layout-delete
         (map (fn [msg]
                (assoc msg
                       :parent (:node x)
                       :event-name :layout-delete-sink)))


         :zoom
         (mapcat (fn [msg]
                   [msg (assoc msg
                               :event-name :layout-zoom
                               :node (:node x))]))


         :compress
         (mapcat (fn [msg]
                   [msg (assoc msg
                               :event-name :layout-compress
                               :node (:node x))]))

         
         :resize
         (mapcat (fn [msg]
                   [msg (assoc msg
                               :event-name :layout-resize
                               :child (:node x))]))

         
         :duplicate
         (mapcat (fn [msg]
                   [msg (assoc msg
                               :event-name :layout-duplicate
                               :child (:node x))]))

         
         :layout-duplicate
         (map (fn [msg]
                (assoc msg
                       :parent (:node x)
                       :event-name :layout-duplicate-sink)))))




(defn layout-root-handler
  [{:keys [node] :as x}]
  (event x
         :layout-resize-left
         (map (fn [msg]
                (assoc msg
                       :node node
                       :perform-drag? true)))


         :layout-resize-top
         (map (fn [msg]
                (assoc msg
                       :node node
                       :perform-drag? true)))


         :layout-resize-right
         (map (fn [msg]
                (assoc msg
                       :node node
                       :perform-drag? true)))


         :layout-resize-bottom
         (mapcat (fn [msg]
                   [{:event-name :save-state}
                    (assoc msg
                           :node node
                           :perform-drag? true)]))))




(defmethod dispatch :layout-resize-width
  ([{:keys [parent child delta-x] :as msg}]
   (let [index (peek (:path child))
         left-subtree (subtree parent 0 index)
         right-subtree (subtree parent index)
         resized-left (command/layout-update-width left-subtree delta-x)
         resized-right (command/layout-update-width right-subtree (- delta-x))
         new-children (into (:children resized-left) (:children resized-right))]
     (if (or (= resized-left left-subtree)
             (= resized-right right-subtree))
       @data/state
       (if (empty? (:path parent))
         (swap! data/state assoc :children new-children)
         (swap! data/state update-in (:path parent) assoc :children new-children))))))




(defmethod dispatch :layout-resize-height
  [{:keys [parent child delta-y] :as msg}]
  (let [index (peek (:path child))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (command/layout-update-height left-subtree delta-y)
        resized-right (command/layout-update-height right-subtree (- delta-y))
        new-children (into (:children resized-left) (:children resized-right))]
    (if (or (= resized-left left-subtree)
            (= resized-right right-subtree))
      @data/state
      (if (empty? (:path parent))
        (swap! data/state assoc :children new-children)
        (swap! data/state update-in (:path parent) assoc :children new-children)))))





(defmethod dispatch :layout-resize-left
  [{:keys [node delta-x] :as msg}]
   (swap! data/state
          update-in
          (:path node)
          (fn [node]
            (let [resized (command/layout-update-width node (- delta-x))]
              (if (and (not (empty? (:children node)))
                       (= resized node))
                node
                (-> resized
                    (update :layout/left + delta-x)
                    (update :layout/width - delta-x)))))))





(defmethod dispatch :layout-resize-right
  [{:keys [node delta-x] :as msg}]
  (swap! data/state
         update-in
         (:path node)
         (fn [node]
           (let [resized (command/layout-update-width node delta-x)]
             (if (and (not (empty? (:children node)))
                      (= resized node))
               node
               (update resized :layout/width + delta-x))))))






(defmethod dispatch :layout-resize-top
  [{:keys [node delta-y] :as msg}]
   (swap! data/state
          update-in 
          (:path node)
          (fn [node]
            (let [resized (command/layout-update-height node (- delta-y))]
              (if (and (not (empty? (:children node)))
                       (= resized node))
                node
                (-> resized
                    (update :layout/top + delta-y)
                    (update :layout/height - delta-y)))))))





(defmethod dispatch :layout-resize-bottom
  [{:keys [node delta-y] :as msg}]
  (swap! data/state
         update-in
         (:path node)
         (fn [node]
           (let [resized (command/layout-update-height node delta-y)]
             (if (and (not (empty? (:children node)))
                      (= resized node))
               node
               (update resized :layout/height + delta-y))))))

