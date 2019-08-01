(ns fncli.fncli
  (:require [commander]
            [fncli.utils.core :as utils]))

(def module-version "0.0.1")

(defn command-handler []
  (let [program (.. commander
                      (version module-version)
                      (description "Forward Networks CLI")
                      (option "-D --debug" "Debug")
                      (option "-J --json" "Output to JSON")
                      (option "-E --edn" "Output to EDN")
                      (option "-W --wide" "Wide screen output")
                      )]

    (.. program
        (command "show <object>" "Show operations"))))

(defn main! []
  (let [program  (command-handler)
        env-keys (utils/get-env-keys)]
    (cond
      (every? env-keys #{"FN_USER" "FN_PASSWORD" "FN_SNAPSHOT"})
      (.parse program (.-argv js/process))
      :else
      (println "Please set env FN_USER and FN_PASSWORD and FN_SNAPSHOT"))))

