(ns recursiveui.event
  (:require [recursiveui.data :as data]
            [recursiveui.element :as elem :refer [attr]]
            [recursiveui.command :as command
             :refer [layout-resize-height
                     layout-resize-width
                     update!]]
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
    (let [event-fn (fn [e] (if (map? e) e {:event e :node node}))
          fs (cons f (take-nth 2 (next kfs)))
          event-fns (map (fn [f] (comp f event-fn)) fs)
          ks (cons k (take-nth 2 kfs))
          event-map (apply assoc {} (interleave ks event-fns))]
      (fn [elem]
        (update elem 1 merge event-map)))))





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



;; The problem is that there is not a single
;; dom element associated with a given node,
;; making it tricky to maintain a mapping from
;; nodes to elements. It would seem that there
;; is now a coordination problem associated
;; with identifying the result of a node->element
;; transformation and supplying that identification
;; to the listen function, which would seem to need
;; access to the underlying DOM nodes. We need a 
;; reverse arrow in our function from db to DOM.
;; This is bad news. Very bad news and in fact it 
;; argues for sticking with hiccup-based description
;; of DOM events.


;; What are the reasons for using google closure's 
;; event listeners? The overarching reason is that
;; it seems to fit better with Clojure's async model.
;; But what specifically is the reason? There were
;; many reasons we want to separate event listening
;; from rendering. We want the async logic to be a
;; relatively pure function of the application state.
;; We can do this by making listen a separate phase
;; over which a time-dependent funciton of the current
;; state is implemented. It doesn't exactly make sense
;; to do this as part of render. 


;; We want a channel valued recursive function of the
;; application state. This is the ultimate goal. But it 
;; seems the only natural way to attach an event listener
;; to the dom is with a hiccup valued function which
;; obviously is not channel valued. The idea is to add
;; event listener.
