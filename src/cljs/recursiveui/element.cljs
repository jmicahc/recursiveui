(ns recursiveui.element)


(def base-element [:div {}])


(defn attr
  ([k v & kvs]
   (fn [elem]
     (apply update elem 1 assoc k v kvs))))



(defn style [k v & kvs]
  (fn [elem]
    (apply update-in elem [1 :style] assoc k v kvs)))



(defn tag [name]
  (fn [elem]
    (assoc elem 0 name)))



(defn html [s]
  (fn [elem]
    (conj elem s)))



(defn conjoin [f & fs]
  (let [g (apply comp f fs)]
    (fn [elem]
      (conj elem (g base-element)))))



(defn ipose [f & fs]
  (let [x ((apply comp f fs) base-element)]
    (fn [elem]
      (into (subvec elem 0 2)
            (interpose x)
            (subvec elem 2)))))



(defn on [k f & kfs]
  (let [event-fn (fn [e] (if (map? e) e {:event e}))
        fs (cons f (take-nth 2 (next kfs)))
        event-fns (map (fn [f] (comp f event-fn)) fs)
        ks (cons k (take-nth 2 kfs))
        event-map (apply assoc {} (interleave ks event-fns))]
    (fn [elem]
      (update elem 1 merge event-map))))
