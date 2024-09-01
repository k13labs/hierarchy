(ns hierarchy.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [hierarchy.core :as h]
            [criterium.core :refer [report-result
                                    quick-benchmark
                                    with-progress-reporting]]))

(defn hierarchy-fixture
  [f]
  (f))

(use-fixtures :once hierarchy-fixture)
