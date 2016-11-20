(ns recursiveui.util)


(defn map-paths [f node]
  (let [path (or (:path node) [])]
    (map-indexed (fn [idx node]
                   (f (assoc node :path (conj path :children idx))))
                 (:children node))))
