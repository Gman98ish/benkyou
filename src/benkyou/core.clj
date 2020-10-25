(ns benkyou.core
  (:gen-class)
  (:require [benkyou.kuro :as kuro]
            [benkyou.web :as web]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn -main
  [& args]
  (println "Benkyou")
  (run-jetty web/handler {:port 8833
                          :host "0.0.0.0"
                          :join? false}))
