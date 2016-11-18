(ns recursiveui.command
  (:require [recursiveui.data :as data]))



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
            layout/term
            children]
     :as node}]
   (if (= partition :column)
     (if magnitude
       (if (= term :var) [[node]] [[]])
       (into [] cat (map width-equations children)))
     (product (map width-equations children)))))



(defn height-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/term
            children]
     :as node}]
   (if (= partition :row)
     (if magnitude
       (if (= term :var) [[node]] [[]])
       (into [] cat (map height-equations children)))
     (product (map height-equations children)))))



(defn solve-equation
  [eq delta]
  (let [freq (count eq)
        x    (/ delta freq)]
    (mapv (fn [term] (update term :layout/magnitude + x)) eq)))



(defn solve-equations
  [eqs dx]
  (mapv (fn [eq] (solve-equation eq dx)) eqs))



(defn equation->tree
  [[term & terms] node]
  (if term
    (recur terms (assoc-in node (:path term) term))
    node))



(defn equations->tree
  [[eq & eqs] node]
  (if eq (recur eqs (equation->tree eq node)) node))



(defn update-row
  [node dx]
  (->
   (width-equations node)
   (solve-equations dx)
   (equations->tree node)))



(defn update-column
  [node dy]
  (->
   (height-equations node)
   (solve-equations dy)
   (equations->tree node)))



(defn update-grid
  [node dx dy]
  (-> (update-row node dx)
      (update-column dy)))


(defn window-width [] (.-innerWidth js/window))
(defn window-height [] (.-innerHeight js/window))



(defn subtree
  ([node start]
   (assoc node
          :children
          (subvec (:children node) start)))
  ([node start end]
   (assoc node
          :children
          (subvec (:children node) start end))))



(defn merge-children [a b]
  (apply update a :children conj (:children b)))



(defn resize-grid
  [{:keys [path] :as node} dx dy]
  (let [parent (get-in @data/state (-> path pop pop))
        index (pop path)
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)]
    (merge-children (update-grid left-subtree dx dy)
                    (update-grid right-subtree (- dx) (- dy)))))




(defn init-paths
  ([node] (init-paths [] node))
  ([path {:keys [children] :as node}]
   (assoc node
          :path path
          :children (mapv (fn [idx child]
                            (init-paths (conj path :children idx)
                                        child))
                          (range)
                          children))))