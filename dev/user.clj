(ns user
  (:require [criterium.core :refer [report-result
                                    quick-benchmark] :as crit]))

(comment
  (add-tap #'println)
  (remove-tap #'println)
  (tap> "foobar"))
