(ns user
  (:require [criterium.core :refer [report-result
                                    with-progress-reporting
                                    quick-benchmark] :as crit]))

(comment
  (report-result
   (with-progress-reporting
     (quick-benchmark
      (-> (make-hierarchy)
          (derive :bar :that)
          (derive :foo :bar)
          (isa? :foo :bar))
      {:verbose true})))) ;; => true

(comment
  (add-tap #'println)
  (remove-tap #'println)
  (tap> "foobar"))
