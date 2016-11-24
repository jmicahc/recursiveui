(ns recursiveui.structure
  (:require [recursiveui.element :as elem
             :refer [attr style tag conjoin class event]]
            [recursiveui.types :as types]
            [recursiveui.event :as event]
            [recursiveui.command :as command]
            [cljs.core.async :refer [chan]]))



(defn flex-row [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/magnitude] :as node} ch elem]
     (rf node
         ch
         (style elem
                :height magnitude
                :display "flex"
                :position "relative"
                :flexDiretion "row")))))



(defn flex-column [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/magnitude] :as node} ch elem]
     (rf node
         ch
         (style elem
                :width magnitude
                :display "flex"
                :position "relative"
                :flexDiretion "column")))))



(defn flex-root [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/width layout/height
              layout/top   layout/left
              layout/flex-direction]
       :as node} ch elem]
     (rf node
         ch
         (style elem
                :width width
                :height height
                :top top
                :left left
                :dipslay "flex"
                :position "absolute"
                :flexDirection flex-direction)))))



(defn flex-root-action-bar [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node ch elem]
     (rf node
         ch
         (-> (class elem "layout-root-action-bar")
             (style :width "100%"
                    :height 40
                    :top -3
                    :left -3
                    :position "absolute"
                    :backgroundColor "grey"
                    :borderColor "#181319"
                    :border "solid"))))))




(defn sidebar-left [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [side-bar/backgroundColor
              side-bar/opacity
              side-bar/width]
       :or {width "8px"
            backgroundColor "brown"
            opacity 1}
       :as node} ch elem]
     (rf node
         ch
         (-> (class elem "sidebar-left")
             (style :backgroundColor backgroundColor
                    :position "absolute"
                    :top "0px"
                    :left "0px"
                    :width width
                    :height "100%"
                    :opacity opacity))))))


(defn sidebar-top [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [side-bar/backgroundColor
              side-bar/opacity
              side-bar/height]
       :or {height "9px"
            backgroundColor "#1D1D2A"
            opacity 0.7}
       :as node} ch elem]
     (rf node
         ch
         (-> (class elem "sidebar-top")
             (style :backgroundColor backgroundColor
                    :position "absolute"
                    :left "0px"
                    :top "0px"
                    :height height
                    :botttom "6px"
                    :width "100%"
                    :opacity opacity))))))


(defn sidebar-right [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node ch elem]
     (rf node
         ch
         (-> (class elem "sidebar-right")
             (style :backgroundColor "brown"
                    :position "absolute"
                    :top "0px"
                    :right "0px"
                    :height "100%"
                    :width "8px"
                    :opacity "1"))))))



(defn sidebar-bottom [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node ch elem]
     (rf node
         ch
         (-> (class elem "sidebar-bottom")
             (style :backgroundColor "brown"
                    :position "absolute"
                    :bottom "0px"
                    :width "100%"
                    :height "8px"
                    :opacity "1"))))))



(defn layout-sidebar [rf]
  (let [render-sidebar-left (sidebar-left (fn [a b c] c))
        render-sidebar-top (sidebar-top (fn [a b c] c))]
    (fn
      ([] (rf))
      ([node] (rf node))
      ([{:keys [layout/partition] :as node} ch elem]
       (rf node ch
           (if (= partition :column)
             (render-sidebar-left node ch elem)
             (render-sidebar-top node ch elem)))))))




(defn drag-button [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [layout/partition] :as node} ch elem]
     (rf node ch 
         (-> (class elem "drag-button")
             (style :position "absolute"
                    :top 10
                    :left 10
                    :width 15
                    :height 20
                    :backgroundColor "blue")
             (event node ch
                    :onMouseDown
                    (map (fn [msg] "message-recieved"))

                    :onDoubleClick
                    (map (fn [msg] "double clicked!")))
             (attr :onClick
                   (fn [e]
                     (.stopPropagation e)
                     (command/update! command/layout-fullscreen
                                      {:node node}))))))))



(defn border
  "temporary"
  [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([node ch elem]
     (rf node
         ch
         (style elem
                :border "solid"
                :borderColor "#181319")))))



(defn style-element
  "temporary"
  [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [style/backgroundColor] :as node} ch elem]
     (rf node ch (style elem :backgroundColor backgroundColor)))))



;; The root must change its state in response to messages.
;; It gets sent instructions for a job and its job is to
;; apply those instructions in its context. Is the full
;; job in the message?
#_(defn root-listener [rf]
  (fn
    ([] (rf))
    ([node] (rf node))
    ([{:keys [root/dragging?] :as msg} ch elem]
     (rf node
         (event/on :job event/delta-xy)
         (-> )))))

;; Everyone needs to be able to send a job
;; to everyone else so that any element can act
;; as a worker! This implies we need some 
;; kind of query.
