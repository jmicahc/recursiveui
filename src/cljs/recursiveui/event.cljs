(ns recursiveui.event
  (:require [recursiveui.data :as data]
            [recursiveui.element :as elem :refer [attr attr*]]
            [recursiveui.command :as command
             :refer [layout-resize-height
                     layout-resize-width
                     layout-drag
                     update!]]
            [cljs.core.async :refer [chan pipe]]
            [goog.events :refer [listen unlisten]]))




(defn delta-event-fn [{:keys [event node] :as m}]
  (let [x (.-clientX event)
        y (.-clientY event)
        prev (:delta/prev node)
        value @prev]
    (reset! prev [x y])
    (assoc m
           :delta/dx (- x (value 0))
           :delta/dy (- y (value 1)))))




(defn on [k f & kfs]
  (fn [node]
    (let [channel (:channel (meta node))
          event-fn (fn [e] (if (map? e) e {:event e :node node}))
          fs (cons f (take-nth 2 (next kfs)))
          event-fns (map (fn [f] (comp f event-fn)) fs)
          ks (cons k (take-nth 2 kfs))
          event-map (apply assoc {} (interleave ks event-fns))]
      (fn [elem]
        (update elem 1 merge event-map)))))


(def buffer-size 100)


(defn on* [k f & kfs]
  "temporary."
  (fn [node]
    (vary-meta node :channel pipe (chan 10 (map inc)))))



(def delta-y
  {:init
   (fn [node] (assoc node :delta/prev (atom [0 0])))
   
   :render
   (fn [node]
     (letfn [(drag-listener [event]
               (->>  {:event event :node node}
                     delta-event-fn
                     (update! layout-resize-height)))
             (unlisten-f [e]
               (unlisten js/window
                         "mousemove"
                         drag-listener))]
       (attr :onMouseDown
             (fn [e]
               (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
               (listen js/window "mouseup" unlisten-f)
               (listen js/window "mousemove" drag-listener)))))})



(defn delta-y* [rf]
  (fn
    ([] (rf))
    ([node] (rf (assoc node :delta/prev (atom [0 0]))))
    ([node elem]
     (letfn [(drag-listener [event]
               (->>  {:event event :node node}
                     delta-event-fn
                     (update! layout-resize-height)))
             (unlisten-f [e]
               (unlisten js/window
                         "mousemove"
                         drag-listener))]
       (rf node
           (attr* elem
                  :onMouseDown
                  (fn [e]
                    (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
                    (listen js/window "mouseup" unlisten-f)
                    (listen js/winodw "mousemove" drag-listener))))))))



(def delta-x
  {:init
   (fn [node] (assoc node :delta/prev (atom [0 0])))

   :render
   (fn [node]
     (letfn [(drag-listener [event]
               (.preventDefault event)
               (->>  {:event event :node node}
                     delta-event-fn
                     (update! layout-resize-width)))
             (unlisten-f [e]
               (unlisten js/window "mousemove" drag-listener))]
       
       (attr :onMouseDown
             (fn [e]
               (.preventDefault e)
               (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
               (listen js/window "mouseup" unlisten-f)
               (listen js/window "mousemove" drag-listener)))))

   :destruct (fn [node] (dissoc node :delta/prev))})



(def layout-resize-delta
  (let [render-delta-x (:render delta-x)
        render-delta-y (:render delta-y)]
    {:init
     (fn [node]
       (assoc node :delta/prev (atom [0 0])))

     :render
     (fn [{:keys [layout/partition] :as node}]
       (if (= partition :column)
         (render-delta-x node)
         (render-delta-y node)))

     :destruct
     (fn [node] (dissoc node :delta/prev))}))


(def layout-drag-delta
  {:init (fn [node] (assoc node :delta/prev (atom [0 0])))
   :render
   (fn [node]
     (letfn [(drag-listener [event]
               (.preventDefault event)
               (->>  {:event event :node node}
                     delta-event-fn
                     (update! layout-drag)))
             (unlisten-f [e]
               (unlisten js/window "mousemove" drag-listener))]
       
     (attr :onMouseDown
             (fn [e]
               (.stopPropagation e)
               (.preventDefault e)
               (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
               (listen js/window "mouseup" unlisten-f)
               (listen js/window "mousemove" drag-listener)))))})



(defn delta-update [f]
  {:init (fn [node] (assoc node :delta/prev (atom [0 0])))
   :render
   (fn [node]
     (letfn [(drag-listener [event]
               (.preventDefault event)
               (.stopPropagation event)
               (->>  {:event event :node node}
                     delta-event-fn
                     (update! f)))
             (unlisten-f [e]
               (unlisten js/window "mousemove" drag-listener))]
       (attr :onMouseDown
             (fn [e]
               (.stopPropagation e)
               (.preventDefault e)
               (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
               (listen js/window "mouseup" unlisten-f)
               (listen js/window "mousemove" drag-listener)))))})


(defn delta-update* [f]
  (fn [rf]
    (fn
      ([] (rf))
      ([node] (rf (assoc node :delta/prev (atom [0 0]))))
      ([node elem]
       (letfn [(drag-listener [event]
                 (->>  {:event event :node node}
                       delta-event-fn
                       (update! f)))
               (unlisten-f [e]
                 (unlisten js/window
                           "mousemove"
                           drag-listener))]
         (rf node
             (let [x (attr* elem
                            :onMouseDown
                            (fn [e]
                              (reset! (:delta/prev node) [(.-clientX e) (.-clientY e)])
                              (listen js/window "mouseup" unlisten-f)
                              (listen js/window "mousemove" drag-listener)))]
               x)))))))




(def layout-resize-delta*
  (let [render-delta-x ((delta-update* layout-resize-width) (fn [a b] b))
        render-delta-y ((delta-update* layout-resize-height) (fn [a b] b))]
    (fn [rf]
      (fn
        ([] (rf))
        ([node] (rf (assoc node :delta/prev (atom [0 0]))))
        ([node elem]
         (if (= partition :column)
           (render-delta-x node elem)
           (render-delta-y node elem)))))))


