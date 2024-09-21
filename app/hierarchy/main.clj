(ns hierarchy.main
  (:require [hierarchy.core :as h])
  (:gen-class))

(h/activate!)

(derive [:foo :bar] :foobar)

(defmulti print-a-thing :type)

(defmethod print-a-thing :default
  [thing]
  (println "thing:" thing))

(defmethod print-a-thing :foobar
  [thing]
  (println "foobar thing:" thing))

(defn -main
  "this is a super simple test app which dispatches to `:foobar`"
  [& args]
  (print-a-thing {:type [:foo :bar]
                  :foobar args}))
