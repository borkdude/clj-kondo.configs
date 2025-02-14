(ns clj-kondo.claypoole-test
  (:require [clj-kondo.core :as clj-kondo]
            [clojure.test :refer :all]
            [com.climate.claypoole :as cp]))

(deftest no-errors
  (let [errors (->> (clj-kondo/run! {:lint ["test/clj_kondo/claypoole_test.clj"]
                                     :cache false})
                    :findings
                    (filter #(= :error (:level %)))
                    (mapv #(select-keys % [:row :type :level :message])))]
    (is (empty? errors))))

(def pool (cp/threadpool 8))
(def coll (range 10))

(comment
  ;; future
  (let [x 1]
    (cp/future pool (println "Hi from future: " x)))

  ;; pdoseq
  (cp/pdoseq 8 [x coll]
             (println "Doseqing over" x))
  (cp/pdoseq pool [x coll :when (= x 1)]
             (println "Doseqing over" x))

  ;; pmap
  (cp/pmap 8
           (fn [x] (println "Mapping over" x))
           coll)
  (cp/pmap pool
           (fn [x y] (println "Mapping over" [x y]))
           coll
           coll)

  ;; upmap
  (cp/upmap 8
            (fn [x] (println "Mapping over" x))
            coll)
  (cp/upmap pool
            (fn [x y] (println "Mapping over" [x y]))
            coll
            coll)

  ;; pvalues
  (println "Parallel values"
           (cp/pvalues 8
                       (do (Thread/sleep 100)
                           (+ 1 2))
                       (+ 3 4)))
  (println "Parallel values"
           (cp/pvalues pool
                       (do (Thread/sleep 100)
                           (+ 1 2))
                       (+ 3 4)))

  ;; upvalues
  (println "Ordered parallel values"
           (cp/upvalues 8
                        (do (Thread/sleep 100)
                            (+ 1 2))
                        (+ 3 4)))
  (println "Ordered parallel values"
           (cp/upvalues pool
                        (do (Thread/sleep 100)
                            (+ 1 2))
                        (+ 3 4)))

  ;; pfor
  (cp/pfor 8 [x coll]
           (println "For over" x))
  (cp/pfor pool [x coll
                 y coll
                 :when (and (= x 1) (= y 1))]
           (println "For over" [x y]))

  ;; upfor
  (cp/upfor 8 [x coll]
            (println "Ordered for over" x))
  (cp/upfor pool [x coll
                  y coll
                  :when (and (= x 1) (= y 1))]
            (println "Ordered for over" [x y]))

  (cp/upfor 8) ;; arity warning with correct location

  (let [x 8] ;; binding is used
    (cp/upfor x [y coll] (println y))))
