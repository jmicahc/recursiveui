(ns recursiveui.element)


(defn class [node s & ss]
  (assoc-in node
            [:element/attr :class]
            (apply str (interpose " " (cons s  ss)))))



(defn attr-mrg [a k v & kvs]
  (reduce (fn [a [k v]]
            (if (and (fn? v) (k a))
              (update a k comp v)
              (assoc a k v)))
          a
          (partition 2 (list* k v kvs))))


(defn attr [node k v & kvs]
  (apply update node :element/attr attr-mrg k v kvs))



(defn style [node k v & kvs]
  (apply update node :element/style assoc k v kvs))



(defn tag [node t]
  (assoc node :element/type t))


(defn conjoin [node x & xs]
  (apply update node :children conj x xs))
