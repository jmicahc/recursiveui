(ns recursiveui.util)


(defn with-paths
  ([node] (with-paths (map identity) node))
  ([xf node]
   (let [path (or (:path node) [])]
     (into []
           (comp (map-indexed
                  (fn [idx node]
                    (assoc node :path (conj path :children idx))))
                 xf)
           (:children node)))))



(defn subtree
  ([node start]
   (assoc node
          :children
          (subvec (:children node) start)))
  ([node start end]
   (assoc node
          :children
          (subvec (:children node) start end))))

