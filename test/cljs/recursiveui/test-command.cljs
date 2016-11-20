(ns recursiveui.test-command
  (:require [recursiveui.command :as command]
            [recursiveui.data :as data]
            [cljs.test :refer [deftest is run-tests]]))


(command/update-grid @data/state 1000 1000)



(deftest test-resize-grid
  (is (= "hello" "hello")))








