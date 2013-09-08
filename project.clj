(defproject picture-gallery "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [ring-server "0.2.8"]
                 [postgresql/postgresql "9.1-901.jdbc4"] 
                 [org.clojure/java.jdbc "0.2.3"] 
                 [lib-noir "0.6.8"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler picture-gallery.handler/war-handler
         :init picture-gallery.handler/init
         :destroy picture-gallery.handler/destroy}
  :profiles
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.3"] [ring/ring-devel "1.1.8"]]}})
