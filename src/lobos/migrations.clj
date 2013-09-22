(ns lobos.migrations
  (:refer-clojure 
   :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema config)))


(defmigration add-users-table
  (up [] (create
          (table :users
                 (varchar :id 32 :primary-key)
                 (varchar :pass 100))))
  (down [] (drop (table :users))))


(defmigration add-images-table
  (up [] ( create
           (table :images
                  (varchar :userid 32)
                  (varchar :name 100))))
  (down [] (drop (table :images))))
