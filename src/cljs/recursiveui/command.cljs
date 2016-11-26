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
       :or {max-magnitude 1500}
       :as term}]
  (let [ret (or (<= (+ dx magnitude) min-magnitude)
                (>= (+ dx magnitude) max-magnitude))]
    (println "out-of-bounds?" ret)
    ret))



#_(defn- solve-equations
  [eqs delta]
  (letfn [(solve-eq [eq]
            (let [dx delta]
              (reduce (fn [ret {:keys [layout/magnitude
                                       layout/min-magnitude
                                       layout/max-magnitude]
                                :or {max-magnitude 2000}
                                :as term}]
                        (cond (<= (+ delta magnitude) min-magnitude)
                              (conj ret (assoc term :layout/magnitude min-magnitude))

                              (>= (+ delta magnitude) max-magnitude)
                              (conj ret (assoc term :layout/magnitude max-magnitude))

                              :else
                              (conj ret (assoc term :layout/magnitude (+ delta magnitude)))))
                      []
                      eq)))]
    (mapv solve-eq eqs)))


(defn calc-freq [eq dx]
  (count (remove (partial out-of-bounds? dx) eq)))


(defn- solve-equations
  [eqs dx]
  (letfn [(solve-equation [eq delta]
            (let [freq (count eq)
                  x    (/ delta freq)]
              (mapv (fn [term] (update term :layout/magnitude + x)) eq)))]
    (mapv (fn [eq] (solve-equation eq dx)) eqs)))


#_(defn- solve-equations
  [eqs delta]
  (letfn [(solve-eq [eq dx freq]
            (reduce (fn [ret {:keys [layout/magnitude
                                     layout/min-magnitude
                                     layout/max-magnitude]
                              :or {max-magnitude 900}
                              :as term}]
                      (cond (<= (+ dx magnitude) min-magnitude)
                            (conj (solve-eq ret
                                            (/ (+ delta (- magnitude min-magnitude))
                                               (dec freq))
                                            (dec freq))
                                  (assoc term :layout/magnitude min-magnitude))

                            (>= (+ dx magnitude) max-magnitude)
                            (conj (solve-eq ret
                                            (/ (- delta (- magnitude max-magnitude))
                                               (dec freq))
                                            (dec freq))
                                  (assoc term :layout/magnitude max-magnitude))

                            :else
                            (conj ret (assoc term :layout/magnitude (+ dx magnitude)))))
                    []
                    eq))]
    (mapv (fn [eq] (solve-eq eq (/ delta (count eq)) (count eq))) eqs)))





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
      (solve-equations  dy)
      (equations->tree  node)))




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
            layout/inner?
            path] :as node} :node
    :keys [delta/dx
           delta/dy] :as msg}]
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


#_(defn layout-resize-height
  [state {:keys [node delta/dy] :as m}]
  {:pre [(has-parent? node)
         (not (nil? dy))]}
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        left-equations (height-equations left-subtree)
        right-equations (height-equations right-subtree)
        real-left-delta (adjust-delta left-equations dy)
        real-right-delta (adjust-delta right-equations (- dy))
        real-delta (if (< (Math/abs real-left-delta)
                          (Math/abs real-right-delta))
                     real-left-delta
                     real-right-delta)
        solved-left (solve-equations left-equations (- real-delta))
        solved-right (solve-equations right-equations real-delta)
        left-children (:children (equations->tree solved-left left-subtree))
        right-children (:children (equations->tree solved-right right-subtree))
        new-children (into left-children right-children)]
    (if (empty? parent-path)
      (assoc state :children new-children)
      (update-in state parent-path assoc :children new-children))))


#_(defn layout-resize-width
  [state {:keys [node delta/dx] :as m}]
  {:pre [(has-parent? node)
         (not (nil? dx))]}
  (let [parent-path (-> (:path node) pop pop)
        parent (get-in state parent-path)
        index (peek (:path node))
        left-subtree (subtree parent 0 index)
        right-subtree (subtree parent index)
        left-equations (width-equations left-subtree)
        right-equations (width-equations right-subtree)
        real-left-delta (adjust-delta left-equations dx)
        real-right-delta (adjust-delta right-equations (- dx))
        real-delta (if (< (Math/abs real-left-delta)
                          (Math/abs real-right-delta))
                     real-left-delta
                     real-right-delta)
        solved-left (solve-equations left-equations (- real-delta))
        solved-right (solve-equations right-equations real-delta)
        left-children (:children (equations->tree solved-left left-subtree))
        right-children (:children (equations->tree solved-right right-subtree))
        new-children (into left-children right-children)]
    #_(println "real delta" real-delta)
    #_(println "real left delta" real-left-delta)
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
    (println "hello")
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




(defn layout-fullscreen
  [state {:keys [node] :as msg}]
  {:pre [(not (empty? (:path node)))]}
  (let [path (:path node)
        win-width (.-innerWidth js/window)
        win-height (.-innerHeight js/window)
        dx (- win-width (:layout/width node))
        dy (- win-height (:layout/height node))]
    (layout-resize-root state
                        {:node (assoc node
                                      :layout/left 0
                                      :layout/top 0)
                         :delta/dx dx
                         :delta/dy dy})))




(defn resize-root-left
  [state {:keys [node delta/dx] :as msg}]
  (update-in state
             (:path node)
             (fn [node]
               (-> node
                   (update :layout/left + dx)
                   (update :layout/width - dx)
                   (layout-update-width (- dx))))))





(defn resize-root-right
  [state {:keys [node delta/dx] :as msg}]
  (update-in state
             (:path node)
             (fn [node]
               (-> node
                   (update :layout/width + dx)
                   (layout-update-width dx)))))





(defn resize-root-bottom
  [state {:keys [node delta/dy] :as msg}]
  (update-in state
             (:path node)
             (fn [node]
               (-> node
                   (update :layout/height + dy)
                   (layout-update-height dy)))))




(defn resize-root-top
  [state {:keys [node delta/dy] :as msg}]
  (update-in state
             (:path node)
             (fn [node]
               (-> node
                   (update :layout/top + dy)
                   (update :layout/height - dy)
                   (layout-update-height (- dy))))))



(defn update! [f node]
  (swap! data/state f node))
