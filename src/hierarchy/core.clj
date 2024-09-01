(ns hierarchy.core
  (:require [clojure.core :as core]))

(defn derive+
  "Establishes a parent/child relationship between parent and tag.
  Unlike `clojure.core/underive`, there is no requirement that
  tag or parent must be a class, keyword or symbol; h must be a
  hierarchy obtained from make-hierarchy, if not supplied defaults to,
  and modifies, the global hierarchy."
  ([tag parent]
   (alter-var-root #'core/global-hierarchy derive+ tag parent))
  ([h tag parent]
   (assert (some? h))
   (assert (some? tag))
   (assert (some? parent))
   (assert (not= tag parent))
   (let [tp (:parents h)
         td (:descendants h)
         ta (:ancestors h)
         tf (fn do-transform
              [m source sources target targets]
              (reduce (fn [ret k]
                        (assoc ret k
                               (reduce conj (get targets k #{}) (cons target (targets target)))))
                      m (cons source (sources source))))]
     (or
      (when-not (contains? (tp tag) parent)
        (when (contains? (ta tag) parent)
          h)
        (when (contains? (ta parent) tag)
          (throw (Exception. (print-str "Cyclic derivation:" parent "has" tag "as ancestor"))))
        (-> (assoc-in h [:parents tag] (conj (get tp tag #{}) parent))
            (update :ancestors tf tag td parent ta)
            (update :descendants tf parent ta tag td)))
      h))))

(defn underive+
  "Removes a parent/child relationship between parent and tag.
  Unlike `clojure.core/underive`, there is no requirement that
  tag or parent must be a class, keyword or symbol; h must be a
  hierarchy obtained from make-hierarchy, if not supplied defaults to,
  and modifies, the global hierarchy."
  ([tag parent]
   (alter-var-root #'core/global-hierarchy underive+ tag parent))
  ([h tag parent]
   (assert (some? h))
   (assert (some? tag))
   (assert (some? parent))
   (assert (not= tag parent))
   (let [parent-map (:parents h)
         childs-parents (if (parent-map tag)
                          (disj (parent-map tag) parent) #{})
         new-parents (if (not-empty childs-parents)
                       (assoc parent-map tag childs-parents)
                       (dissoc parent-map tag))
         deriv-seq (map #(cons (key %) (interpose (key %) (val %)))
                        (seq new-parents))]
     (if (contains? (parent-map tag) parent)
       (reduce (fn do-derive
                 [h [t p]]
                 (derive+ h t p))
               (core/make-hierarchy)
               deriv-seq)
       h))))

(when (not= core/derive derive+)
  (alter-var-root #'core/derive (constantly derive+)))

(when (not= core/underive underive+)
  (alter-var-root #'core/underive (constantly underive+)))
