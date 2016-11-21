(ns recursiveui.command
  (:require [recursiveui.data :as data]
            [recursiveui.util :refer [with-paths subtree]]
            [cljs.pprint :refer [pprint]]))




(defn product [node]
  "f: List X List X List --> List X List
     Computes cartesian cross product of list of lists of lists of elements.
     (((a b) (c d)) ((e f) (g h))) ==> ((a b e f) (a b g h) (c d e f) (c d g h))"
  (reduce (fn [left right]
            (mapcat (fn [lhs] (map #(concat lhs %) right)) left))
          (first node)
          (next node)))



(defn layout-nav
  ([node]
   (with-paths node))
  ([xf node]
   (with-paths (comp (filter :layout/active?)
                     (filter :layout/inner?)
                     xf) node)))



(defn width-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?]
     :as node}]
   (if (= partition :column)
     (if magnitude
       (if variable? [[node]] [[]])
       (layout-nav (comp (map width-equations) cat) node))
     (product (layout-nav (map width-equations) node)))))



(defn height-equations
  ([{:keys [layout/magnitude
            layout/partition
            layout/variable?]
     :as node}]
   (if (= partition :row)
     (if magnitude
       (if variable? [[node]] [[]])
       (layout-nav (comp (map height-equations) cat) node))
     (product (layout-nav (map height-equations) node)))))




#_(defn solve [equation total-magnitude]
  (let [constant
        (- total-magnitude
           (transduce (comp (remove :layout/variable?)
                            (map :layout/magnitude))
                      +
                      equation))
        coefficient
        (transduce (comp (filter :layout/variable?)
                         (map :layout/coefficient))
                   +
                   equation)]
    (/ constant coefficient)))



#_(defn solve-eqs [equations total-magnitude]
  (letfn [(solve-eq [eq]
            (let [sol (solve eq total-magnitude)]
              (into []
                    (comp (filter :layout/variable?)
                          (map (fn [{:keys [coefficient] :as term}]
                                 (assoc term
                                        :layout/magnitude
                                        (* sol coefficient)))))
                    eq)))]
    (mapv solve-eq equations)))


(defn solve-equations
  [eqs dx]
  (letfn [(solve-equation [eq delta]
            (let [freq (count eq)
                  x    (/ delta freq)]
              (mapv (fn [term]
                      #_(println "x" x "color" (:style/backgroundColor term))
                      (update term :layout/magnitude + x)) eq)))]
    (mapv (fn [eq] (solve-equation eq dx)) eqs)))



(defn equations->tree
  [[eq & eqs] node]
  (letfn [(equation->tree [[term & terms] node]
            (if term
              (recur terms (assoc-in node
                                     (subvec (:path term)
                                             (count (:path node)))
                                     term))
              node))]
    (if eq (recur eqs (equation->tree eq node)) node)))



(defn layout-update-width
  [node dx]
  (-> (width-equations node)
      (solve-equations dx)
      (equations->tree node)))




(defn layout-update-height
  [node dy]
  (-> (height-equations node)
      (solve-equations dy)
      (equations->tree node)))




(defn layout-update-size
  [node dx dy]
  (-> (layout-update-width node dx)
      (layout-update-height dy)))




(defn layout-update-root-size
  [{:keys [layout/width
           layout/height]
    :as node} dx dy]
  (layout-update-size (assoc node
                             :layout/width (+ dx width)
                             :layout/height (+ dy height))
                      dx
                      dy))




(defn layout-resize-height
  [state {:keys [node delta/dy] :as m}]
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in @data/state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (layout-update-height left-subtree dy)
        resized-right (layout-update-height right-subtree (- dy))]
    (if (empty? parent-path)
      (assoc state
             :children
             (into (:children resized-left)
                   (:children resized-right)))
      (assoc-in state
                (conj parent-path :children)
                (into (:children resized-left)
                      (:children resized-right))))))





(defn layout-resize-width
  [state {:keys [node delta/dx] :as m}]
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in @data/state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (layout-update-width left-subtree dx)
        resized-right (layout-update-width right-subtree (- dx))
        new-children (into (:children resized-left) (:children resized-right))]
    (if (empty? parent-path)
      (assoc state :children new-children)
      (update-in state
                 parent-path
                 assoc
                 :children
                 new-children))))




(defn layout-resize
  [state {:keys [node delta/dy delta/dx] :as m}]
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in @data/state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        resized-left (layout-update-size left-subtree dx dy)
        resized-right (layout-update-size right-subtree (- dx) (- dy))]
    (if (empty? parent-path)
      (assoc state
             :children
             (into (:children resized-left)
                   (:children resized-right)))
      (assoc-in state
                (conj parent-path :children)
                (into (:children resized-left)
                      (:chldren resized-right))))))




(defn update! [f node]
  (swap! data/state f node))
