(ns recursiveui.layout
  (:require [recursiveui.element :as elem :refer [event conjoin]]
            [recursiveui.command :as command :refer [dispatch layout-nav]]
            [recursiveui.structure :as structure]
            [recursiveui.data :as data]
            [recursiveui.util :as util :refer [subtree]]))



(def layout-handler
  (fn [{:keys [node] :as x}]
    (event x
           :delete
           (mapcat (fn [msg]
                     (if (::handled? msg)
                       [msg]
                       [msg
                        (assoc msg
                               ::handled? true
                               :event-name :layout-delete
                               :child (:node x))])))

           

           :layout-delete
           (map (fn [msg]
                  (assoc msg
                         :parent-path (:path x)
                         :event-name  :layout-delete-sink)))

             

           :resize
           (mapcat (fn [msg]
                     (let [{:keys [node layout/partition]} x]
                       (if (::handled? msg)
                         [msg]
                         [(assoc msg ::handled? true)
                          (assoc msg
                                 ::handled? true
                                 :event-name :layout-resize
                                 :child-index (peek (:path x)))]))))

           
             
           :layout-resize
           (map (fn [msg]
                  (let [event-name (if (= (:layout/partition node) :row)
                                     :layout-resize-width
                                     :layout-resize-height)]
                    (assoc msg
                           ::handled?     true
                           :perform-drag? true
                           :parent-path   (:path x)
                           :event-name    event-name))))

           
             
           :duplicate
           (mapcat (fn [msg]
                     (if (::handled? msg)
                       [msg]
                       [(assoc msg ::handled? true)
                        (assoc msg
                               ::handled?   true
                               :event-name  :layout-duplicate
                               :child       (:node x))])))

           
             
           :layout-duplicate
           (map (fn [msg]
                  (assoc msg
                         :parent-path (:path x)
                         :event-name  :layout-duplicate-sink))))))




(defn layout-root-handler
  [{:keys [path node] :as x}]
  (event x
         :layout-resize-left
         (map (fn [msg]
                (assoc msg
                       :node-path path
                       :perform-drag? true)))


         
         :layout-resize-top
         (map (fn [msg]
                (assoc msg
                       :node-path path
                       :perform-drag? true)))


         
         :layout-resize-right
         (map (fn [msg]
                (assoc msg
                       :node-path path
                       :perform-drag? true)))


         
         :layout-resize-bottom
         (mapcat (fn [msg]
                   [{:event-name :save-state}
                    (assoc msg
                           :node-path path
                           :perform-drag? true)]))))




(defn leaf? [x] (empty? (layout-nav x)))




(defn leaf-decorator [leaf-value]
  (fn [{:keys [children] :as x}]
    (reduce (fn [x [path child]]
              (if (leaf? child)
                (let [p (conj path :children)]
                  (update-in x p conj leaf-value))
                x))
            x
            (next (layout-nav (map (fn [c] [(:path c) c]))
                              (assoc x :path []))))))




(def column-leaf-decorator
  (leaf-decorator {:tags #{:structure/sidebar-top
                           :sources/resize}
                   :traverse/render? true
                   :element/attr {:id "hello-world"}}))





(def row-leaf-decorator
  (leaf-decorator {:tags #{:structure/sidebar-left
                           :sources/resize}
                   :traverse/render? true}))





(def resizable-flex-root
  (comp structure/flex-root
        layout-handler
        layout-root-handler
        (fn [x]
          (conjoin x
                   {:tags #{:structure/flex-root-action-bar}
                    :traverse/render? true
                    :children [{:tags #{:structure/action-button}
                                :traverse/render? true}]}
                   {:tags #{:structure/sidebar-top
                            :sources/layout-resize-top}
                    :traverse/render? true}
                   {:tags #{:structure/sidebar-left
                            :sources/layout-resize-left}
                    :traverse/render? true}
                   {:tags #{:structure/sidebar-right
                            :sources/layout-resize-right}
                    :traverse/render? true}
                   {:tags #{:structure/sidebar-bottom
                            :sources/layout-resize-bottom}
                    :traverse/render? true}))))




(def resizable-flex-row
  (comp layout-handler
        structure/flex-row
        row-leaf-decorator))





(def resizable-flex-column
  (comp structure/flex-column
        layout-handler
        column-leaf-decorator))






(defmethod dispatch :layout-resize-width
  ([{:keys [parent-path child-index delta-x] :as msg}]
   {:pre [(not (empty? parent-path)) child-index delta-x]}
   (swap! data/state
          update-in
          parent-path
          (fn [parent]
            (let [left-subtree  (subtree parent 0 child-index)
                  right-subtree (subtree parent child-index)
                  resized-left  (command/layout-update-width left-subtree delta-x)
                  resized-right (command/layout-update-width right-subtree (- delta-x))
                  new-children  (into (:children resized-left)
                                      (:children resized-right))]
              (if (or (= resized-left left-subtree)
                      (= resized-right right-subtree))
                parent
                (assoc parent :children new-children)))))))





(defmethod dispatch :layout-resize-height
  [{:keys [parent-path child-index delta-y] :as msg}]
  {:pre [(not (empty? parent-path)) child-index delta-y]}
  (swap! data/state
         update-in
         parent-path
         (fn [parent]
           (let [left-subtree  (subtree parent 0 child-index)
                 right-subtree (subtree parent child-index)
                 resized-left  (command/layout-update-height left-subtree  delta-y)
                 resized-right (command/layout-update-height right-subtree (- delta-y))
                 new-children  (into (:children resized-left)
                                     (:children resized-right))]
             (if (or (= resized-left left-subtree)
                     (= resized-right right-subtree))
               parent 
               (assoc parent :children new-children))))))





(defmethod dispatch :layout-resize
  [{:keys [parent-path child-index delta-x delta-y] :as msg}]
  {:pre [delta-x delta-y (not (empty? parent-path)) child-index]}
  (swap! data/state
         update-in
         parent-path
         (fn [parent]
           (let [left-subtree  (subtree parent 0 child-index)
                 right-subtree (subtree parent child-index)
                 resized-left  (command/layout-update-size left-subtree delta-x delta-y)
                 resized-right (command/layout-update-size right-subtree (- delta-x) (- delta-y))]
             (if (or (= resized-left left-subtree)
                     (= resized-right right-subtree))
               parent
               (assoc parent
                      :children
                      (into (:children resized-left)
                            (:children resized-right))))))))






(defmethod dispatch :layout-resize-left
  [{:keys [node-path delta-x] :as msg}]
  {:pre [(not (empty? node-path)) delta-x]}
   (swap! data/state
          update-in
          node-path
          (fn [node]
            (let [resized (command/layout-update-width node (- delta-x))]
              (if (and (not (empty? (:children node)))
                       (= resized node))
                node
                (-> resized
                    (update :layout/left + delta-x)
                    (update :layout/width - delta-x)))))))





(defmethod dispatch :layout-resize-right
  [{:keys [node-path delta-x] :as msg}]
  {:pre [(not (empty? node-path)) delta-x]}
  (swap! data/state
         update-in
         node-path
         (fn [node]
           (let [resized (command/layout-update-width node delta-x)]
             (if (and (not (empty? (:children node)))
                      (= resized node))
               node
               (update resized :layout/width + delta-x))))))





(defmethod dispatch :layout-resize-top
  [{:keys [node-path delta-y] :as msg}]
  {:pre [(not (empty? node-path)) delta-y]}
   (swap! data/state
          update-in 
          node-path
          (fn [node]
            (let [resized (command/layout-update-height node (- delta-y))]
              (if (and (not (empty? (:children node)))
                       (= resized node))
                node
                (-> resized
                    (update :layout/top + delta-y)
                    (update :layout/height - delta-y)))))))





(defmethod dispatch :layout-resize-bottom
  [{:keys [node-path delta-y] :as msg}]
  {:pre [(not (empty? node-path)) delta-y]}
  (swap! data/state
         update-in
         node-path
         (fn [node]
           (let [resized (command/layout-update-height node delta-y)]
             (if (and (not (empty? (:children node)))
                      (= resized node))
               node
               (update resized :layout/height + delta-y))))))






(defmethod dispatch :layout-delete-sink
  [{:keys [parent-path child] :as msg}]
  {:pre [(not (empty? parent-path)) child (:layout/magnitude child)]}
  (swap! data/state
         update-in
         parent-path
         command/layout-update-width
         (:layout/magnitude child)))






(defmethod dispatch :layout-duplicate-sink
  [{:keys [parent-path child] :as msg}]
  {:pre [parent-path child]}
  (swap! data/state
         update-in
         parent-path
         command/layout-update-width
         (- (:layout/magnitude child))))





(defmethod dispatch :layout-fullscreen
  [{:keys [node-path] :as msg}]
  {:pre [node-path]}
  (swap! data/state
         update-in
         node-path
         command/layout-resize-root
         (.-innerWidth js/window)
         (.-innerHeight js/window)))
