(ns picture-gallery.handler
(:require 
  [compojure.core :refer :all]
  [compojure.route :as route]
  [compojure.core :refer [defroutes]]
  [noir.util.middleware :as noir-middleware]
  [picture-gallery.routes.home :refer [home-routes]]
  [picture-gallery.routes.auth :refer [auth-routes]]
  [picture-gallery.routes.upload :refer [upload-routes]]
  [picture-gallery.routes.gallery :refer [gallery-routes]]
  [noir.session :as session]
  [taoensso.timbre :as timbre]
  [com.postspectacular.rotor :as rotor]))

(defn user-page [_] 
  (session/get :user))

(defn init []
  (timbre/set-config!
   [:appenders :rotor]
   {:min-level :info
    :enabled? true
    :async? false ; should be always false for rotor :max-message-per-msecs nil
    :fn rotor/append})
  (timbre/set-config!
   [:shared-appender-config :rotor]
   {:path "error.log" :max-size (* 512 1024) :backlog 10})
  (timbre/info "picture-gallery started successfully"))

(defn destroy []
  (println "picture-gallery is shutting down"))
  
(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))


(def app (noir-middleware/app-handler 
           [auth-routes            home-routes            upload-routes
            gallery-routes            app-routes]           :access-rules [user-page]))

(def war-handler (noir-middleware/war-handler app)) 

