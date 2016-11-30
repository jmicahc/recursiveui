(ns recursiveui.listeners
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [chan put! <!]]))


(def event-channel (chan))
