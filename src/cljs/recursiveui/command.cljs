(ns recursiveui.command
  (:require [recursiveui.data :as data]
            [recursiveui.util :refer [map-paths]]
            [cljs.pprint :refer [pprint]]))




(defn product [node]
  "f: List X List X List --> List X List
     Computes cartesian cross product of list of lists of lists of elements.
     (((a b) (c d)) ((e f) (g h))) ==> ((a b e f) (a b g h) (c d e f) (c d g h))"
  (reduce (fn [left right]
            (mapcat (fn [lhs] (map #(concat lhs %) right)) left))
          (first node)
          (next node)))



(defn width-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?
            children]
     :as node}]
   (if (= partition :column)
     (if magnitude
       (if variable? [[node]] [[]])
       (into [] cat (map-paths width-equations node)))
     (product (map-paths width-equations node)))))



(defn height-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?]
     :as node}]
   (if (= partition :row)
     (if magnitude
       (if variable? [[node]] [[]])
       (into [] cat (map-paths height-equations node)))
     (product (map-paths height-equations node)))))


(defn solve-equations
  [eqs dx]
  (letfn [(solve-equation [eq delta]
            (let [freq (count eq)
                  x    (/ delta freq)]
              (mapv (fn [term] (update term :layout/magnitude + x)) eq)))]
    (mapv (fn [eq] (solve-equation eq dx)) eqs)))



(defn equations->tree
  [[eq & eqs] node]
  (letfn [(equation->tree [[term & terms] node]
            (if term
              (recur terms (assoc-in node (:path term) term))
              node))]
    (if eq (recur eqs (equation->tree eq node)) node)))



(defn update-row
  [node dx]
  (-> (width-equations node)
      (solve-equations dx)
      (equations->tree node)))



(defn update-column
  [node dy]
  (-> (height-equations node)
      (solve-equations dy)
      (equations->tree node)))



(defn update-layout-size
  [node dx dy]
  (-> (update-row node dx)
      (update-column dy)))



(defn update-layout-root-size
  [{:keys [layout/width
           layout/height]
    :as node} dx dy]
  (update-layout-size (assoc node
                             :layout/width (+ dx width)
                             :layout/height (+ dy height))
                      dx
                      dy))



(defn subtree
  ([node start]
   (assoc node
          :children
          (subvec (:children node) start)))
  ([node start end]
   (assoc node
          :children
          (subvec (:children node) start end))))



(defn resize-grid 
  [{:keys [node delta/dx delta/dy] :as m}]
  (let [parent (get-in @data/state (-> (:path node) pop pop))
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (update-layout-size left-subtree 0 dy)
        resized-right (update-layout-size right-subtree 0 (- dy))]
    (swap! data/state
           assoc
           :children
           (into (:children resized-left)
                 (:children resized-right)))))
