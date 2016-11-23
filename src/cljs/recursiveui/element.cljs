(ns recursiveui.element)


(def base-element [:div {}])


(defn attr [elem k v & kvs]
  (apply update elem 1 assoc k v kvs))


(defn style [elem k v & kvs]
  (apply update-in elem [1 :style] assoc k v kvs))


(defn tag [name]
  (fn [elem]
    (assoc elem 0 name)))


(defn class [elem s]
  (update-in elem [1 :class] str " " s))


(defn conjoin [xf & xfs]
  (fn [rf]
    (fn
      ([] (rf))
      ([node] (((apply comp xf xfs) rf) node))
      ([node elem]
       (rf node (apply conj
                       elem
                       (map (fn [f]
                              (let [x ((f (fn [a b] b)) node base-element)]
                                x))
                            (cons xf xfs))))))))
