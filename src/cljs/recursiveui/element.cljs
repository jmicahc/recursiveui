(ns recursiveui.element)


(def base-element [:div {}
                   [:div {:id "actions"}]])




(defn attr
  ([k v & kvs]
   (fn [elem]
     (apply update elem 1 assoc k v kvs))))


(defn attr* [elem k v & kvs]
  (apply update elem 1 assoc k v kvs))


(defn style [k v & kvs]
  (fn [elem]
    (apply update-in elem [1 :style] assoc k v kvs)))


(defn style* [elem k v & kvs]
  (apply update-in elem [1 :style] assoc k v kvs))


(defn tag [name]
  (fn [elem]
    (assoc elem 0 name)))


(defn class [s]
  (fn [elem]
    (update-in elem [1 :class] str "" s)))


(defn class* [elem s]
  (update-in elem [1 :class] str " " s))




(defn conjoin [c & cs]
  {:render (fn [node]
             (let [f (transduce (map #((% :render identity) node)) comp (list* c cs))
                   x (f base-element)]
               (fn [elem] (conj elem x))))
   
   :init (transduce (map #(% :init identity)) comp (list* c cs))})


(defn conjoin* [xf & xfs]
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





(defn ipose [c & cs]
  {:render (fn [node]
             (let [f (transduce (map #((% :render identity) node)) comp (list* c cs))
                   x (f base-element)]
               (fn [elem]
                 (into (subvec elem 0 2)
                       (interpose x)
                       (subvec elem 2)))))
   
   :init (transduce (map #(% :init identity)) comp (list* c cs))})


(defn elem [f & fs]
  (fn [elem]
    ((apply comp f fs) elem)))



(defn ccomp [f & fs]
  (fn [node]
    (let [g (reduce comp (map #(% node) (cons f fs)))]
      g)))



(defn children [f & fs]
  (fn [node]
    (let [gs (map #(% node) (cons f fs))]
      (fn [elem]
        (apply conj elem (map #(%  base-element) gs))))))
