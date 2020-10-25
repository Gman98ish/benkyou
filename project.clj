;; allow insecure downloads
(require 'cemerick.pomegranate.aether)
(cemerick.pomegranate.aether/register-wagon-factory!
 "http" #(org.apache.maven.wagon.providers.http.HttpWagon.))

(defproject benkyou "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.atilika.kuromoji/kuromoji "0.7.7"]
                 [ring "1.8.1"]
                 [lein-ring "0.12.5"]
                 [hiccup "1.0.5"]
                 [compojure "1.6.2"]]
  :main benkyou.core
  :plugins [[lein-javadoc "0.3.0"]]
  :javadoc-opts {:package-names ["org.atilika.kuromoji"]}
  :repl-options {:init-ns benkyou.core}
  :repositories [["atilika" "https://www.atilika.org/nexus/content/repositories/atilika"]])
