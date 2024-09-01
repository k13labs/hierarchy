(ns hierarchy.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [hierarchy.core :as h]
            [pjstadig.humane-test-output :as hto]
            [malli.generator :as mg]))

(hto/activate!)

(def hierarchy-id
  [:or
   [:string]
   [:keyword]
   [:symbol]
   [:qualified-keyword]
   [:qualified-symbol]])

(def hierarchy-member
  [:or
   [:map
    [:x hierarchy-id]
    [:y hierarchy-id]]
   [:vector hierarchy-id]
   hierarchy-id])

(deftest hierarchy-bindings-are-installed-test
  (testing "derive is installed"
    (is (= h/derive+ derive)))
  (testing "underive is installed"
    (is (= h/underive+ underive))))

(defspec derive-tests
  1000
  (prop/for-all
   [[foo bar that] (gen/vector-distinct (mg/generator hierarchy-member) {:num-elements 3})]
   (is (= {:parents {bar #{that}, foo #{bar}},
           :descendants {that #{bar foo}, bar #{foo}},
           :ancestors {bar #{that}, foo #{bar that}}}
          (-> (make-hierarchy)
              (derive bar that)
              (derive foo bar))))))

(defspec underive-tests
  1000
  (prop/for-all
   [[foo bar that] (gen/vector-distinct (mg/generator hierarchy-member) {:num-elements 3})]
   (is (= {:parents {foo #{bar}},
           :descendants {bar #{foo}},
           :ancestors {foo #{bar}}}
          (-> (make-hierarchy)
              (derive bar that)
              (derive foo bar)
              (underive bar that))))))

(defn- reset-global-hierarchy!
  []
  (alter-var-root #'clojure.core/global-hierarchy (constantly (make-hierarchy))))

(defn- fetch-global-hierarchy
  []
  (-> #'clojure.core/global-hierarchy deref))

(defspec global-derive-tests
  1000
  (prop/for-all
   [[foo bar that] (gen/vector-distinct (mg/generator hierarchy-member) {:num-elements 3})]
   (reset-global-hierarchy!)
   (derive bar that)
   (derive foo bar)
   (is (= {:parents {bar #{that}, foo #{bar}},
           :descendants {that #{bar foo}, bar #{foo}},
           :ancestors {bar #{that}, foo #{bar that}}}
          (fetch-global-hierarchy)))
   (reset-global-hierarchy!)))

(defspec global-underive-tests
  1000
  (prop/for-all
   [[foo bar that] (gen/vector-distinct (mg/generator hierarchy-member) {:num-elements 3})]
   (reset-global-hierarchy!)
   (derive bar that)
   (derive foo bar)
   (underive bar that)
   (is (= {:parents {foo #{bar}},
           :descendants {bar #{foo}},
           :ancestors {foo #{bar}}}
          (fetch-global-hierarchy)))
   (reset-global-hierarchy!)))
