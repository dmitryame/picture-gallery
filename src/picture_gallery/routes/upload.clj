(ns picture-gallery.routes.upload 
 (:require [compojure.core :refer :all]
           [hiccup.form :refer :all]
           [hiccup.element :refer [image]]
           [hiccup.util :refer [url-encode]]
           [picture-gallery.views.layout :as layout]
           [picture-gallery.models.db :as db]
           [noir.io :refer [resource-path]]
           [noir.session :as session]
           [noir.response :as resp]
           [clojure.java.io :as io]
           [noir.util.route :refer [restricted]])
  (:import [java.io File FileInputStream FileOutputStream]
           java.awt.image.BufferedImage
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           java.awt.image.AffineTransformOp
           javax.imageio.ImageIO))

(def thumb-size 150)
(def thumb-prefix "thumb_")

(defn gallery-path []
  (str (resource-path) "img" File/separator (session/get :user) File/separator))

(defn upload-page [info]   
  (layout/common
    [:h2 "Upload an image"]
    [:p info]
    (form-to {:enctype "multipart/form-data"}
      [:post "/upload"] 
      (file-upload :file) 
      (submit-button "upload"))))


(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance
                (double ratio) (double ratio)) 
       transform-op (java.awt.image.AffineTransformOp.
                        scale java.awt.image.AffineTransformOp/TYPE_BILINEAR)]
       (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let [img (ImageIO/read file)
       img-width (.getWidth img)
       img-height (.getHeight img)]
    (let [ratio (/ thumb-size img-height)]
      (scale img ratio (int (* img-width ratio)) thumb-size))))

(defn save-thumbnail [{:keys [filename]}] 
  (ImageIO/write
    (scale-image (io/input-stream (str (gallery-path) filename))) 
    "jpeg"
    (File. (str (gallery-path) thumb-prefix filename))))

(defn handle-upload [file] 
  (upload-page
    ;;check that a filename was supplied
    (if (empty? (:filename file)) 
      "please select a file to upload" 
      (try
        ;;save the file and create the thumbnail
        (noir.io/upload-file
          (str File/separator "img"
            File/separator
            (session/get :user)
            File/separator)
          file)
        (save-thumbnail file) 
        (db/add-image (session/get :user) (:filename file))
        ;;display the thumbnail 
        (image {:height "150px"}
          (str "/img/" 
            (session/get :user)
            "/"
            thumb-prefix
            (url-encode (:filename file))))
        ;;handle errors
        (catch Exception ex
          (str "error uploading file " (.getMessage ex)))))))


(defroutes upload-routes  (GET "/upload" [info] (restricted (upload-page info)))  (POST "/upload" [file] (restricted (handle-upload file))))
