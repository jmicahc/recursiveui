(ns recursiveui.event
  (:require [recursiveui.data :as data]
            [recursiveui.command :as command
             :refer [resize-grid]]))


(defn on [k f & kfs]
  (fn [elem]
    (update elem 1
            (fn [props]
              (reduce (fn [props [k f]]
                        (if-let [g (k props)]
                          (assoc props k (comp g f))
                          (assoc props k (fn [e] (f {:e e})))))
                      props
                      (partition 2 (list* k f kfs)))))))

(defn vargs [f & kvs]
  (comp f (fn [m] (apply assoc m kvs))))


(defn delta []
  (let [prev (atom nil)]
    (fn [{:keys [e] :as m}]
      (let [x (.-clientX e)
            y (.-clientY e)
            value @prev]
        (reset! prev [x y])
        (if (vector? value)
          (assoc m
                 :delta/dx (- x (value 0))
                 :delta/dy (- y (value 1)))
          (assoc m
                 :delta/dx 0
                 :delta/dy 0))))))


(defn drag [f]
  (comp f (delta)))




(def resize-fn
  (comp (fn [{:keys [delta/dx delta/dy element/path] :as m}]
          #_(swap! data/state update-in path resize-grid dx dy)
          m)
        (delta)))


(defn test-event [{:keys [path] :as node}]
  (on :onClick (vargs resize-fn :element/path path)))



