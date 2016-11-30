(ns recursiveui.listeners
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [chan put! <!]]))


(def event-channel (chan))







(defn event-loop []
  (go-loop []
    (let [event (<! event-channel)]
      (println event)
      (recur))))
