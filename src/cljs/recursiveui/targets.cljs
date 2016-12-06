(ns recursiveui.targets
  (:require [recursiveui.element :as element :refer [event]]))


(defn drag [x]
  (event x :drag
         (map (fn [msg]
                (assoc msg
                       :perform-drag? true
                       :node-path (:path x))))))
