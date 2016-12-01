(ns recursiveui.element)


(defn class [node s & ss]
  (assoc-in node
            [:element/attr :class]
            (apply str (interpose " " (cons s  ss)))))


(defn attr [node k v & kvs]
  (apply update node :element/attr assoc k v kvs))



(defn style [node k v & kvs]
  (apply update node :element/style assoc k v kvs))



(defn tag [node t]
  (assoc node :element/type t))
