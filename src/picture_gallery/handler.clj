;(ns picture-gallery.handler
;  (:require [compojure.core :refer [defroutes routes]]
;            [ring.middleware.resource :refer [wrap-resource]]
;            [ring.middleware.file-info :refer [wrap-file-info]]
;            [hiccup.middleware :refer [wrap-base-url]]
;            [compojure.handler :as handler]
;            [compojure.route :as route]
;            [picture-gallery.routes.home :refer [home-routes]]
;            [noir.util.middleware :as noir-middleware]))
(ns picture-gallery.handler(:require [compojure.route :as route]            [compojure.core :refer [defroutes]]            [noir.util.middleware :as noir-middleware]            [picture-gallery.routes.home :refer [home-routes]]
            [picture-gallery.routes.auth :refer [auth-routes]]))

(defn init []
  (println "picture-gallery is starting"))

(defn destroy []
  (println "picture-gallery is shutting down"))
  
(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

;(def app (handler/site (routes home-routes app-routes)))
(def app (noir-middleware/app-handler [home-routes app-routes]))

;(def war-handler 
;  (-> app    
;    (wrap-resource "public") 
;    (wrap-base-url)
;    (wrap-file-info)))
 (def war-handler (noir-middleware/war-handler app)) 

(def app (noir-middleware/app-handler 
           [auth-routes
            home-routes
            app-routes]))
