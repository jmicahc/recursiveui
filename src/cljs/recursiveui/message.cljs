(ns recursiveui.message
  (:require [cljs.core.async :refer [put! pipe chan]]))




(defn on [node ch k f & kfs]
  (let [c (chan 1 (fn [rf]
                    (let [rfs (into {} (map (fn [k xf] [k (xf rf)]) (list* k f kfs)))]
                      (fn
                        ([] (rf))
                        ([buff] (rf buff))
                        ([buff {:keys [name] :as msg}]
                         (if-let [rf* (rfs name)]
                           (rf* buff msg)
                           (rf buff msg)))))))]
    (pipe c ch)
    c))
