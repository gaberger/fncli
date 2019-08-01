(ns fncli.commands.fncli-show
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs-http.util :as util]
            [clojure.pprint :as pprint]
            [clojure.string :as str]
            [cljs.core.async :refer [<!]]
            [cljs-bean.core :refer [bean ->clj ->js]]
            [commander]
            [fncli.utils.core :as utils]
            [w3c-xmlhttprequest-plus :refer [XMLHttpRequest]]))

(set! js/XMLHttpRequest XMLHttpRequest)

(def module-version "0.0.1")
(def api-base "https://fwd.app/api")
(def user (utils/get-env "FN_USER"))
(def password (utils/get-env "FN_PASSWORD"))
(def snapshot (utils/get-env "FN_SNAPSHOT"))

(def url (str/join "/" [api-base "snapshots" snapshot "graphql"]))
(def config-url (str/join "/" [api-base "snapshots" snapshot "files"]))

(defn auth-handler [user password]
  {:username user :password password})

(def show-interfaces-query "{devices {name
                                interfaces {name
                                            interfaceType
                                            mtu
                                            description
                                            adminStatus
                                            operStatus}}}")


(defn parse-intf [m]
  (reduce
   (fn [acc record]
     (let [device-name {:device-name (:name record)}
           device-record (conj device-name  (first (:interfaces record)))]
       (conj acc device-record)))
   []
   m))

(defn show-interfaces []
  (go (let [response (<! (http/post url
                                    {:headers {"Content-type" "application/json"}
                                     :basic-auth (auth-handler user password)
                                     :json-params {:query show-interfaces-query}}))]
        (condp = (:status response)
          200 (:body response)
          (do (println response)
                nil)))))


(defn show-config [device]
  (go (let [config (str/join "," [device "configuration" "16.txt" ])
            request (str/join "/" [config-url config])
            response (<! (http/get request
                                   {:headers {"Content-type" "application/json"}
                                    :basic-auth (auth-handler user password)
                                    }))]
        (condp = (:status response)
          200 (:body response)
          (do (println "Device not found")
              nil)))))


(defn command-handler []
  (let [program (.. commander
                    (version module-version)
                    (description "Show Module")
                    (option "-D --debug" "Debug")
                    (option "-J --json" "Output to JSON")
                    (option "-E --edn" "Output to EDN")
                    (option "-W --wide" "Wide screen output"))]

    (.. program
        (command "interfaces")
        (action (fn [cmd]
                  (println "Show Interfaces")
                  (go
                    (if-let [body (<! (show-interfaces))]
                      (let [decode (->
                                    (util/json-decode body)
                                    :data
                                    :devices)]
                        (-> decode
                            parse-intf
                            utils/print-table)))))))
    (.. program
        (command "config")
        (arguments "<device>")
        (action (fn [device cmd]

                  (println "Show Config" device)
                  (go
                    (if-let [body (<! (show-config device))]
                      (println body))))))
    
    (.on program "command:*" (fn [e]
                               (when-not
                                (contains?
                                 (into #{}
                                       (keys (js->clj (.-_execs program))))
                                 (first e))
                                 (.help program))))
    
    program))
    
(defn main! []
  (let [program  (command-handler)]       
      (.parse program (.-argv js/process))))

         