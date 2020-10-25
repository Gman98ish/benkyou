(ns benkyou.repl
  (:require [benkyou.core :as core]))

(defn restart-server
  []
  (.stop core/server)
  (.start core/server))
