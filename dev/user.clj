(ns user
  (:require [hierarchy.core :as h]
            [criterium.core :refer [report-result
                                    with-progress-reporting
                                    quick-benchmark] :as crit]))

(comment
  (report-result
   (with-progress-reporting
     (quick-benchmark
      (-> (make-hierarchy)
          (h/derive+ :bar [:that "thing"])
          (h/derive+ :foo :bar)
          (isa? :foo [:that "thing"]))
      {:verbose true})))) ;; => true

(comment
  (add-tap #'println)
  (remove-tap #'println)
  (tap> "foobar"))
