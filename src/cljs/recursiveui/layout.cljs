(ns recursiveui.layout
  (:require [recursiveui.element :as elem :refer [event conjoin]]
            [recursiveui.command :as command :refer [dispatch layout-nav]]
            [recursiveui.structure :as structure]
            [recursiveui.targets :as targets]
            [recursiveui.data :as data]
            [recursiveui.util :as util :refer [subtree]]))



(def layout-handler
  (fn [{:keys [node] :as x}]
    (event x
           :delete
           (mapcat (fn [msg]
                     (if (::handled? msg)
                       [msg]
                       [(assoc msg ::handled? true)
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
                           :perform-drag? true
                           :parent-path   (:path x)
                           :event-name    event-name))))
           
           :duplicate
           (fn [rf]
             (fn ([] (rf))
               ([buff] (rf buff))
               ([buff msg]
                (if (::handled? msg)
                  (rf buff msg)
                  (do (rf buff (assoc msg ::handled? true))
                      (rf buff (assoc msg
                                      ::handled?  true
                                      :event-name :layout-duplicate
                                      :child      (:node x))))))))
           
             

           :layout-duplicate
           (map (fn [msg]
                  (assoc msg
                         :parent-path (:path x)
                         :event-name  :layout-duplicate-sink)))

           

           :layout-add-partition
           (mapcat (fn [msg]
                     [{:event-name :save-state}
                      (assoc msg
                             :event-name :layout-add-partition-2
                             :child-path (:path x))]))


           
           :layout-add-partition-2
           (map (fn [msg]
                  (assoc msg
                         :event-name :layout-add-partition-sink
                         :parent-path (:path x)))))))





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
  (fn walk [{:keys [children] :as x}]
    (reduce (fn [x [path child]]
              (let [p (conj path :children)]
                (update-in x p conj leaf-value)))
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
                   {:tags #{:structure/flex-root-action-bar
                            :sources/drag}
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


(def nil-vec
  (memoize (fn [n] (vec (repeat n nil)))))


(defn spread-delta
  "takes a delta and a vector of remainders and returns a 
   vector for which the sum is equal to dx * | rs | with 
   a distribution as uniform as possible given the constraint 
   that dx_i <= remainder_i. Returns nill if a spread does not
   exist.
   
    ex. 
    ==> (spread-delta 18 [4 8 8])
    ==> [4 7 7]
   
    ==> (spread-delta 18 [5 8 8])
    ==> [5 6.5 6.5]

    ==> (spread-delta 18 [4 6 8])
    ==> [4 6 8].


    ==> (spread-delta 18 [4 6 9])
    ==> nil"
  ([delta remainders]
   (spread-delta (transient (nil-vec (count remainders)))
                 delta
                 (map vector (range) remainders)))
  ([ret delta remainders]
   (letfn [(into-spread [ret indexed-deltas]
             (reduce (fn [ret [idx dx]]
                       (assoc! ret idx dx))
                     ret
                     indexed-deltas))]
     (when remainders
       (let [freq    (count remainders)
             mean-dx (/ delta freq)
             {rs-below-mean true  rs-above-mean false}
             (group-by (comp (fn [r] (< r mean-dx)) second) remainders)]
         (if (empty? rs-below-mean)
           (persistent! (reduce (fn [ret [idx _]]
                                  (assoc! ret idx mean-dx))
                                ret
                                rs-above-mean))
           (let [adjusted-delta (reduce (fn [d [_ r]] (- d r)) delta rs-below-mean)]
             (recur (into-spread ret rs-below-mean)
                    adjusted-delta
                    rs-above-mean))))))))



(defn reduce-height-props
  [{:keys [layout/magnitude
           layout/partition]
    :as node}]
  (if (= partition :row)
    (if magnitude
      {:min-height-remainder (:min-height-remainder node)
       :layout/height magnitude
       :max-height-remainder (:max-height-remainder node)}
      (apply merge-with min (layout-nav (map reduce-height-props) node)))
    (apply merge-with + (layout-nav (map reduce-height-props) node))))




(defn reduce-width-props
  [{:keys [layout/magnitude
           layout/partition]
    :as node}]
  (if (= partition :column)
    (if magnitude
      {:min-width-remainder (:min-width-magnitude node)
       :layout/width        magnitude
       :max-width-remainder (:min-width-magnitude node)}
      (apply merge-with min (layout-nav (map reduce-width-props) node)))
    (apply merge-with + (layout-nav (map reduce-width-props) node))))




(defn calc-remainders
  [{:keys [layout/partition
           layout/magnitude
           layout/min-magnitude
           layout/max-magnitude]
    :as node}]
  (let [children (layout-nav (map calc-remainders) node)]
    (if (= partition :row)
      (if magnitude
        (assoc node
               :min-height-remainder (- magnitude (or min-magnitude 0))
               :max-height-remainder (- (or max-magnitude js/Infinity) magnitude)
               :children children)
        (-> node
            (merge (reduce-height-props node))
            (assoc :children children
                   :layout/width (transduce (map :layout/width) + children))))
      (if magnitude
        (assoc node
               :min-width-remainder (- magnitude (or min-magnitude 0))
               :max-width-remainder (- (or max-magnitude js/Infinity) magnitude)
               :children children)
        (-> node
            (merge (reduce-width-props node))
            (assoc :children children
                   :layout/height (transduce (map :layout/height) + children)))))))







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
            (let [resized (command/layout-update-width node delta-x)]
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





(defn add-partition-helper
  [cnt
   magnitude
   {:keys [layout/partition] :as node}]
  (-> node
      (update :tags
              disj 
              (if (= partition :row)
                :layout/resizable-flex-row
                :layout/resizable-flex-column))
      (update :tags
              conj
              (if (= partition :row)
                :layout/resizable-flex-column
                :layout/resizable-flex-row))
      (assoc :layout/partition (if (= partition :row) :column :row)
             :layout/magnitude 0 #_(/ magnitude cnt))))




#_(defn get-magnitude
  [parent]
  (let [parent-partition (:layout/partition parent)]
    (letfn [(walk [{:keys [layout/partition
                           layout/magnitude
                           children]
                    :as node}]
              (if (and magnitude (= parent-partition partition))
                (list magnitude)
                (mapcat walk children)))]
      (reduce + (walk parent)))))


(defn get-magnitude
  [{:keys [layout/partition] :as node}]
  (reduce + (map :layout/magnitude
                 (first (if (= partition :row)
                          (command/height-equations node)
                          (command/width-equations node))))))



(defmethod dispatch :layout-add-partition-sink
  [{:keys [parent-path child-path cnt]
    :or {cnt 2}
    :as msg}]
  {:pre [(not (empty? parent-path)) (not (empty? child-path))]}
  (swap! data/state
         update-in
         parent-path
         (fn [{:keys [layout/magnitude]
               :or {magnitude (get-magnitude parent)}
               :as parent}]
           (println "magnitude" magnitude)
           (when (nil? magnitude)
             (println (get-magnitude parent)))
           (-> parent
               (dissoc :layout/magnitude)
               (update-in (subvec child-path (count parent-path))
                          (fn [{:keys [layout/partition] :as child}]
                            (let [f (if (= partition :row)
                                      command/layout-update-width
                                      command/layout-update-height)]
                              (-> (update child
                                          :children
                                          into
                                          (repeat cnt (add-partition-helper cnt magnitude child)))
                                  (f magnitude)))))))))





(defmethod dispatch :layout-fullscreen
  [{:keys [node-path] :as msg}]
  {:pre [node-path]}
  (swap! data/state
         update-in
         node-path
         command/layout-resize-root
         (.-innerWidth js/window)
         (.-innerHeight js/window)))
