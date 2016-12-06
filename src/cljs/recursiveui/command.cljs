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




(defn- solve-equations
  [eqs dx]
  (letfn [(remove-out-of-bounds [eq]
            (remove (partial out-of-bounds? (/ dx (count eq))) eq))
          (solve-equation [eq]
            (let [freq (count eq)
                  x    (/ dx freq)]
              (mapv (fn [term] (update term :layout/magnitude + x)) eq)))]
    (into []
          (comp (map remove-out-of-bounds)
                (map solve-equation))
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




(defn- layout-update-width
  [node dx]
  (-> (width-equations node)
      (solve-equations dx)
      (equations->tree node)))





(defn- layout-update-height
  [node dy]
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
         (fn [node]
           (assoc node
                  :layout/top (+ top dy)
                  :layout/left (+ left dx)))))





(defmethod dispatch :undo
  [msg]
  (if-let [prev-state (last (:root-timeline @data/memory))]
    (do (swap! data/memory update :root-timeline pop)
        (reset! data/state prev-state))
    @data/state))





(defmethod dispatch :save-state
  [msg]
  (swap! data/memory update :root-timeline conj @data/state)
  @data/state)
