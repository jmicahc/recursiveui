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
  #_(println "@solve: # terms" (count (first eqs)))
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
  #_(println "eqs" eqs)
  #_(println "# terms:" (count (first eqs)))
  (let [depth (count (:path node))]
    (letfn [(eq->tree [[term & terms] node]
              #_(println "path" (:path term))
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
  #_(println "@layout-update-width")
  #_(pprint node)
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




(defn layout-resize-height
  ([state {:keys [node delta/dy]}]
   (layout-resize-height state node dy))
  ([state node dy]
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
     (if (or (= resized-left left-subtree)
             (= resized-right right-subtree))
       state
       (if (empty? parent-path)
         (assoc state :children new-children)
         (update-in state parent-path assoc :children new-children))))))





(defn layout-resize-width
  ([state {:keys [node delta/dx]}]
   (layout-resize-width state node dx))
  ([state node dx]
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
     (if (or (= resized-left left-subtree)
             (= resized-right right-subtree))
       state
       (if (empty? parent-path)
         (assoc state :children new-children)
         (update-in state parent-path assoc :children new-children))))))




(defn layout-resize
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





(defn resize-root-left
  ([state {:keys [node delta/dx] :as msg}]
   (resize-root-left state node dx))
  ([state node dx]
   (update-in state
              (:path node)
              (fn [node]
                (let [resized (layout-update-width node (- dx))]
                  (if (= resized node) node
                      (-> resized
                          (update :layout/left + dx)
                          (update :layout/width - dx))))))))






(defn resize-root-right
  ([state {:keys [node delta/dx] :as msg}]
   (resize-root-right state node dx))
  ([state node dx]
   (update-in state
              (:path node)
              (fn [node]
                (let [resized (layout-update-width node dx)]
                  (if (= resized node) node
                      (update resized :layout/width + dx)))))))






(defn resize-root-bottom
  ([state {:keys [node delta/dy] :as msg}]
   (resize-root-bottom state node dy))
  ([state node dy]
   (update-in state
              (:path node)
              (fn [node]
                (let [resized (layout-update-height node dy)]
                  (if (= resized node) node
                      (update resized :layout/height + dy)))))))





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





(defn layout-fullscreen
  [{:keys [layout/width
           layout/height]
    :as root-node}]
  (let [win-width (.-innerWidth js/window)
        win-height (.-innerHeight js/window)]
    (-> root-node
        (assoc :layout/left 0
                 :layout/top 0)
        (layout-resize-root win-width win-height))))





(defn layout-conjoin
  ([state {:keys [node] :as msg}]
   (layout-conjoin state node (last (:children node))))
  ([state node last-node]
   (let [dx (:layout/magnitude last-node)]
     (update-in state
                (:path node)
                (fn [node]
                  (-> node
                      (update :children conj last-node)
                      (layout-update-width (- dx))))))))





(defn delete-node!
  [{:keys [path] :as node}]
  (swap! data/state
         update-in
         (-> path pop pop)
         (fn [parent]
           (let [idx      (peek path)
                 children (:children parent)
                 before   (subvec children 0 idx)
                 after    (subvec children (inc idx))]
             (assoc parent
                    :children
                    (into before after))))))





(defn update!
  ([{:keys [command] :as msg}]
   (update! command msg))
  ([f & args]
   (apply swap! data/state f args)))




(defn update-node!
  [node f & args]
  (let [ret  (apply f node args)
        path (:path node)]
    (if (empty? path)
      (reset! data/state ret)
      (swap! data/state assoc-in path ret))
    ret))
