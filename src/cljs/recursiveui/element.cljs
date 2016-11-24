(ns recursiveui.element
  (:require [cljs.core.async :refer [put! pipe chan]]))


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


(def dom-event? #{:onClick
                  :onMouseDown
                  :onMouseMove
                  :onDoubleClick
                  :onMouseUp})



(defn event [elem node ch k f & kfs]
  (update elem 1
          (partial merge-with comp)
          (into {}
                (for [[k xf] (partition 2 (list* k f kfs))]
                  [k (fn [e]
                       (.stopPropagation e)
                       (.preventDefault e)
                       (let [transform (chan 1 xf)]
                         (pipe transform ch)
                         (put! transform {:event e :node node :name (keyword (.-name e))})
                         e))]))))




(defn conjoin [xf & xfs]
  (fn [rf]
    (fn
      ([] (rf))
      ([node] (((apply comp xf xfs) rf) node))
      ([node ch elem]
       (rf node ch
           (apply conj
                  elem
                  (map (fn [f]
                         ((f (fn [a b c] c)) node ch base-element))
                       (cons xf xfs))))))))
