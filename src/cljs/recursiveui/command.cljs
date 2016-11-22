(ns recursiveui.command
  (:require [recursiveui.data :as data]
            [recursiveui.util :refer [with-paths subtree]]
            [cljs.pprint :refer [pprint]]))




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
           (filter :layout/inner?))
     node))
  ([xf node]
   (with-paths
     (comp (filter :layout/active?)
           (filter :layout/inner?)
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
       (layout-nav (mapcat width-equations) node))
     (product (layout-nav (map width-equations) node)))))



(defn- height-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?]
     :as node}]
   (if (= partition :row)
     (if magnitude
       (if variable? [[node]] [[]])
       (layout-nav (mapcat height-equations) node))
     (product (layout-nav (map height-equations) node)))))



(defn- solve-equations
  [eqs dx]
  (letfn [(solve-equation [eq delta]
            (let [freq (count eq)
                  x    (/ delta freq)]
              (mapv (fn [term]
                      (update term :layout/magnitude + x)) eq)))]
    (mapv (fn [eq] (solve-equation eq dx)) eqs)))



(defn- equations->tree
  [eqs node]
  (let [depth (count (:path node))]
    (letfn [(eq->tree [[term & terms] node]
              (if term
                (recur terms (assoc-in node (subvec (:path term) depth) term))
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
      (solve-equations dy)
      (equations->tree node)))




(defn- layout-update-size
  [node dx dy]
  (-> (layout-update-width node dx)
      (layout-update-height dy)))




(defn- has-parent? [node]
  (>= (count (:path node)) 2))




(defn layout-resize-root
  [state
   {{:keys [layout/width
            layout/height
            layout/inner?] :as node} :node
    :keys [delta/dx
           delta/dy
           path] :as msg}]
  {:pre [(not (neg? width))
         (not (neg? height))
         (number? dx)
         (number? dy)
         (false? inner?)]}
  (let [new-node (assoc node
                        :layout/width (+ dx width)
                        :layout/height (+ dy height))
        new-layout (layout-update-size new-node dx dy)]
    (if (empty? path) new-layout (assoc-in state path new-layout))))




(defn layout-resize-height
  [state {:keys [node delta/dy] :as m}]
  {:pre [(not (nil? dy))
         (has-parent? node)]}
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (layout-update-height left-subtree dy)
        resized-right (layout-update-height right-subtree (- dy))
        new-children (into (:children resized-left) (:children resized-right))]
    (if (empty? parent-path)
      (assoc state :children new-children)
      (update-in state parent-path assoc :children new-children))))




(defn layout-resize-width
  [state {:keys [node delta/dx] :as m}]
  {:pre [(not (nil? dx))
         (has-parent? node)]}
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (layout-update-width left-subtree dx)
        resized-right (layout-update-width right-subtree (- dx))
        new-children (into (:children resized-left) (:children resized-right))]
    (if (empty? parent-path)
      (assoc state :children new-children)
      (update-in state parent-path assoc :children new-children))))





(defn layout-resize
  [state {:keys [node delta/dy delta/dx] :as m}]
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
    (if (empty? parent-path)
      (assoc state :children new-children)
      (update-in state parent-path assoc :children new-children))))




(defn update! [f node]
  (swap! data/state f node))
