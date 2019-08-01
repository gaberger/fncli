(ns fncli.utils.core
  (:require 
            [cljs.reader :refer [read-string]]
            [util]
            [which]
            [prompts]
            [os]
            [js-yaml :as yaml]
            [path]
            [taoensso.timbre :as timbre
             :refer-macros [log  trace  debug  info  warn  error  fatal  report
                            logf tracef debugf infof warnf errorf fatalf reportf
                            spy]]
            [goog.object :as obj]
            [clojure.pprint :as pprint]
            [clojure.walk :refer [postwalk]]
            [clojure.string :as str]
            [schema.core :as s :include-macros true]
            ))

(defn get-home-dir [] (.homedir os))

(defn print-edn [obj]
  (pprint/pprint  obj))

(defn print-json [obj]
  (println (.stringify js/JSON (clj->js obj) nil " ")))

;TODO filter empty set
(defn print-table [obj]
  (let [convert-set (postwalk #(if (and (set? %) (string? (first %))) (str/join "," %)  %) obj)
        convert-keys (postwalk #(if (keyword? %) (name %) %) convert-set)]
    (pprint/print-table convert-keys)))

(defn log-error [& msg]
  (js/console.error (str/join " " msg)))

(defn dump-object [obj]
  (println "###########")
  (println (.inspect util obj)))

(defn get-env [v]
  (aget (.-env js/process) v))

(defn- set-env [k v]
  (aset (.-env js/process) k v))

(defn get-env-keys []
  (into #{} (-> (obj/getKeys (.-env js/process)) (js->clj))))

(defn filter-pred [data filter]
  (into []
        (for [m data
              :let  [tags (:tags m)]
              :when (contains? tags filter)]
          m)))

(defn handle-command-default [cmd]
  (.on cmd "command:*" (fn [e]
                         (println e)
                         (when-not
                          (contains?
                           (into #{}
                                 (keys (js->clj (.-_execs cmd))))
                           (first e))
                           (.help cmd)))))

(defn prefix-match [match coll]
  (let [matches (for [x     coll
                      :when (str/starts-with? x match)]
                  x)]
    (if (or (> (count matches) 1)
            (empty? matches))
      nil
      (do 
          (first matches)))))

(defn selector [input coll]
  (let [str->vec (mapv str input)
        collv    (into [] coll)
        accum    (atom [])]
    (conj accum (first str->vec))
    (reduce
     (fn [acc x]
       (let [match-string (str/join "" @accum)]
         (when (some? (prefix-match match-string collv))
             (reduced :FOO #_(prefix-match match-string collv)))
       (swap! accum conj x))
       )
     []
     str->vec)))

(defn error-and-exit [message]
  (do 
    (error message)
    (.exit js/process 1)))

(defn rotate-left [xs]
  (when (seq xs) (concat (rest xs) [(first xs)])))

(def exports #js {})
