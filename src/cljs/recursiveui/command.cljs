(ns recursiveui.command
  (:require [recursiveui.data :as data]
            [recursiveui.util :refer [with-paths subtree]]
            [cljs.pprint :refer [pprint]]))




(defmulti dispatch :event-name)




(defn- product [node]
  "f: List X List X List --> List X List
     Computes cartesian cross product of list of lists of lists of elements.
     (((a b) (c d)) ((e f) (g h))) ==> ((a b e f) (a b g h) (c d e f) (c d g h))"
  (reduce (fn [left right]
            (mapcat (fn [lhs] (map #(concat lhs %) right)) left))
          (first node)
          (next node)))




(defn- layout-nav
  ([node]
   (with-paths
     (comp (filter :layout/active?)
           (mapcat (fn [node]
                     (if (:layout/inner? node)
                       (list node)
                       (layout-nav (:children node))))))
     node))
  ([xf node]
   (with-paths
     (comp (mapcat (fn [node]
                     (if (:layout/inner? node)
                       (list node)
                       (with-paths xf node))))
           (filter :layout/active?)
           xf)
     node)))



(defn max-by [f coll]
  (reduce (fn [ret x]
            (if (> (f x) ret) x ret))
          coll))


(defn min-by [f coll]
  (reduce (fn [ret x]
            (if (< (f x) ret) x ret))
          coll))


#_(defn- layout-height
  [{:keys [layout/partition
           layout/magnitude]
    :as node}]
  (when node
    (if (= partition :row)
      (if magnitude
        {:magnitude magnitude
         :max-magnitude (or (:layout/max-magnitude node) js/Infinity)
         :min-magnitude (or (:layout/min-magnitude node) 0)
         :remainder (- magnitude (or (:layout/min-magnitude node) 0))}
        (layout-height (min-by :remainder (layout-nav node))))
      (apply merge-with + (layout-nav (map layout-height) node)))))


;; Apply this to each subtree.
(defn- layout-height
  [{:keys [layout/partition
           layout/magnitude]
    :as node}]
  (when node
    (if (= partition :row)
      (if magnitude
        (- magnitude (or (:layout/min-magnitude node) 0))
        (layout-height (reduce min (layout-nav node))))
      (reduce + (layout-nav (map layout-height) node)))))



(def nil-vec
  (memoize (fn [n] (vec (repeat n nil)))))


(defn spread-delta
  "takes a delta and a vector of remainders and returns a 
   vector for which the sum is equal to dx * | rs | with 
   a distribution as uniform as possible given the constraint 
   that dx_i <= remainder_i. Assumes (> (reduce + rs) dx).
   
    ex. 
    ==> (spread-delta 18 [4 8 8])
    ==> [4 7 7]
   
    ==> (spread-delta 18 [5 8 8])
    ==> [5 6.5 6.5]

    ==> (spread-delta 18 [4 6 8])
    ==> [4 6 8]."
  ([delta remainders]
   (spread-delta (transient (nil-vec (count remainders)))
                 delta
                 (map vector (range) remainders)))
  ([ret delta remainders]
   {:pre [(not (empty? remainders))]}
   (letfn [(into-spread [ret indexed-deltas]
             (reduce (fn [ret [idx dx]]
                       (assoc! ret idx dx))
                     ret
                     indexed-deltas))]
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
                   rs-above-mean)))))))


;; dx = 6
;; r =  [4 8 8]
;; ret = [4 7 7]

;; r = [8 4 8]
;; ret = [7 4 7]



;; At each node we ask if (+ mag delta) is greater
;; than the remainder.

;; First, we know at the root whether the delta can
;; be applied or not. if delta > remainder, then only
;; the remainder can be applied as the new delta. The
;; question is how do we spread the delta recursively?
;; We use the fact that each node knows its remainder.
;; Each parent tries to spread the delta evenly between
;; its children. However, some children may not have 
;; room to accomodate the expected delta. The parent
;; finds any child for which (+ mag delta) is greater
;; than the remainder for that child, and first updates
;; them with delta equal to their remainder. The sum
;; of these remainders is then subtracted from the delta,
;; which is then divided between the remaining children.
;; However, at this point it may be that the sum of the 
;; remainders of the children is less than their count
;; divided by the new delta, since they now have to 
;; proportionally take on more delta because other children
;; didn't carry their full weight. Thus we must repeat this 
;; whole process again for the remaining children. We 
;; do this recursively at each node, giving a time complexity
;; of O(n^2). Ouch. This can be improved by back propagating the
;; remainders.



(defn reduce-height-props
  [{:keys [layout/magnitude
           layout/partition]
    :as node}]
  (if (= partition :row)
    (if magnitude
      {:min-height-remainder (:min-height-remainder node)
       :layout/height        magnitude
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





(defn- min-height-remainder
  [{:keys [layout/partition
           layout/magnitude]
    :as node}]
  (when node
    (if (= partition :row)
      (if magnitude
        (- magnitude (or (:layout/min-magnitude node) js/Infinity))
        (min-height-remainder (reduce min js/Infinity (layout-nav node))))
      (reduce + (layout-nav (map min-height-remainder) node)))))




(defn- update-height
  [{:keys [layout/partition
           layout/magnitude]
    :as node}]
  (when node
    (if (= partition :row)
      (if magnitude
        {:magnitude     magnitude
         :max-magnitude (or (:layout/max-magnitude node) js/Infinity)
         :min-magnitude (or (:layout/min-magnitude node) 0)
         :remainder     (- magnitude (or (:layout/min-magnitude node) 0))}
        (layout-height (min-by :remainder (layout-nav node))))
      (apply merge-with + (layout-nav (map layout-height) node)))))



(defn- width-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?]
     :as node}]
   (if (= partition :column)
     (if magnitude
       (if variable? [[node]] [[]])
       (layout-nav (comp (map width-equations) cat) node))
     (product (layout-nav (map width-equations) node)))))




(defn- height-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?]
     :as node}]
   (if (= partition :row)
     (if magnitude
       (if variable? [[node]] [[]])
       (layout-nav (comp (map height-equations) cat) node))
     (product (layout-nav (map height-equations) node)))))




(defn- out-of-bounds?
  [dx {:keys [layout/magnitude
              layout/min-magnitude
              layout/max-magnitude]
       :as term}]
  (or (and min-magnitude (<= (+ dx magnitude) min-magnitude))
      (and max-magnitude (>= (+ dx magnitude) max-magnitude))))



;; We are overcounting the number of terms, since
;; some equations contain some of the same terms.
(defn- solve-equations
  [eqs dx]
  (letfn [(remove-out-of-bounds
            ([eq] (remove-out-of-bounds [] eq (count eq)))
            ([ret [term & terms] freq]
             (cond (nil? term) ret

                   (out-of-bounds? (/ dx freq) term)
                   (recur ret terms (dec freq))
                   
                   :else
                   (recur (conj ret term) terms freq))))
          (solve-equation [eq]
            (let [freq (count eq)
                  x    (/ dx freq)]
              (mapv (fn [term] (update term :layout/magnitude + x)) eq)))]
    (transduce (comp (map remove-out-of-bounds)
                     (fn [rf]
                       (fn
                         ([] (rf))
                         ([ret] (rf ret))
                         ([ret eq]
                          (if (empty? eq)
                            (reduced eqs)
                            (rf ret eq)))))
                     (map solve-equation))
               conj
               eqs)))


(defn- equations->tree
  [eqs node]
  (let [depth (count (:path node))]
    (letfn [(eq->tree [[term & terms] node]
              (if term
                (recur terms (update-in node
                                        (subvec (:path term) depth)
                                        assoc
                                        :layout/magnitude
                                        (:layout/magnitude term)))
                node))
            (eqs->tree [[eq & eqs] node]
              (if eq (recur eqs (eq->tree eq node)) node))]
      (eqs->tree eqs node))))




(defn layout-update-width
  [node dx]
  (-> (width-equations node)
      (solve-equations dx)
      (equations->tree node)))




(defn layout-update-height
  [node dy]
  (println "remainder" (layout-height node))
  (-> (height-equations node)
      (solve-equations  dy)
      (equations->tree  node)))




(defn- layout-update-size
  [node dx dy]
  (-> (layout-update-width node dx)
      (layout-update-height dy)))





(defn- has-parent? [node]
  (>= (count (:path node)) 2))





(defn layout-resize-root
  ([{:keys [layout/width
            layout/height]
     :as root-node} new-width new-height]
   (let [dx (- new-width width)
         dy (- new-height height)]
     (-> root-node
         (layout-update-size dx dy)
         (assoc :layout/width  new-width
                :layout/height new-height)))))




#_(defn layout-resize
  ([state {:keys [node delta/dx delta/dy] :as m}]
   (layout-resize state node dx dy))
  ([state node dx dy]
   {:pre [(not (nil? dx))
          (not (nil? dy))
          (has-parent? node)]}
   (let [parent-path (-> (:path node) pop pop)
         parent (get-in state parent-path)
         index (peek (:path node))
         left-subtree (subtree parent 0 index)
         right-subtree (subtree parent index)
         resized-left (layout-update-size left-subtree dx dy)
         resized-right (layout-update-size right-subtree (- dx) (- dy))
         new-children (into (:children resized-left) (:children resized-right))]
     (if (or (= resized-left left-subtree)
             (= resized-right right-subtree))
       state
       (if (empty? parent-path)
         (assoc state :children new-children)
         (update-in state parent-path assoc :children new-children))))))






(defn resize-root-top
  ([state {:keys [node delta/dy] :as msg}]
   (resize-root-top state node dy))
  ([state node dy]
   (update-in state
              (:path node)
              (fn [node]
                (let [resized (layout-update-height node (- dy))]
                  (if (= resized node) node
                      (-> resized
                          (update :layout/top + dy)
                          (update :layout/height - dy))))))))






(defn layout-drag
  [state {:keys [node delta/dx delta/dy] :as msg}]
  (let [path (:path node)
        update-fn (fn [{:keys [layout/top layout/left] :as state}]
                    (assoc state
                           :layout/top (+ top dy)
                           :layout/left (+ left dx)))]
    (if (empty? path)
      (update-fn state)
      (update-in state path update-fn))))




(defmethod dispatch :default
  [msg]
  @data/state)




(defmethod dispatch :delete
  [{:keys [parent-path child] :as msg}]
  {:pre [(not (empty? parent-path)) child]}
  (swap! data/state
         update-in
         parent-path
         (fn [parent]
           (let [idx (peek (:path child))
                 children (:children parent)]
             (assoc parent
                    :children
                    (into (subvec children 0 idx)
                          (subvec children (inc idx))))))))





(defmethod dispatch :duplicate
  [{:keys [parent-path child] :as msg}]
  {:pre [parent-path child]}
  (swap! data/state
         update-in
         parent-path
         (fn [{:keys [children] :as p}]
           (let [idx (peek (:path child))]
             (assoc p
                    :children
                    (into (conj (subvec children 0 idx) child)
                          (subvec children idx)))))))





(defmethod dispatch :drag
  [{:keys [node-path delta-x delta-y] :as msg}]
  (swap! data/state
         update-in
         node-path
         (fn [{:keys [layout/top
                      layout/left]
               :as node}]
           (assoc node
                  :layout/top (+ top delta-y)
                  :layout/left (+ left delta-x)))))





(defmethod dispatch :undo
  [msg]
  (if-let [prev-state (last (:root-timeline @data/memory))]
    (do (swap! data/memory update :root-timeline pop)
        (reset! data/state prev-state))
    @data/state))




(defmethod dispatch :pretty-print-state
  [msg]
  (pprint @data/state)
  @data/state)




(defmethod dispatch :save-state
  [msg]
  (swap! data/memory update :root-timeline conj @data/state)
  @data/state)
