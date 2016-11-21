(ns recursiveui.element)


(def base-element [:div {}
                   [:div {:id "canvas"}]])



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



(defn on [k f & kfs]
  (let [event-fn (fn [e] (if (map? e) e {:event e}))
        fs (cons f (take-nth 2 (next kfs)))
        event-fns (map (fn [f] (comp f event-fn)) fs)
        ks (cons k (take-nth 2 kfs))
        event-map (apply assoc {} (interleave ks event-fns))]
    (fn [elem]
      (update elem 1 merge event-map))))





(defn conjoin [c & cs]
  {:render (fn [node]
             (let [f (transduce (map #((% :render identity) node)) comp (list* c cs))
                   x (f base-element)]
               (fn [elem] (conj elem x))))
   
   :init (transduce (map #(% :init identity)) comp (list* c cs))})




(defn ipose [c & cs]
  {:render (fn [node]
             (let [f (transduce (map #((% :render identity) node)) comp (list* c cs))
                   x (f base-element)]
               (fn [elem]
                 (into (subvec elem 0 2)
                       (interpose x)
                       (subvec elem 2)))))
   
   :init (transduce (map #(% :init identity)) comp (list* c cs))})
