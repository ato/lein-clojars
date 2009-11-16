(ns leiningen.pom
  (:require [clojure.contrib.duck-streams :as duck-streams])
  (:import (org.apache.maven.model Model Dependency Contributor)
           (org.apache.maven.model.io.xpp3 MavenXpp3Writer)))

(defn- make-contributor [name]
  (doto (Contributor.)
    (.setName name)))

(defn- make-dependency [scope [dep version]]
  (doto (Dependency.)
    (.setGroupId (or (namespace dep) (name dep)))
    (.setArtifactId (name dep))
    (.setVersion version)    
    (.setScope scope)))

(defn make-model [project]
  (doto (Model.)
    (.setModelVersion "4.0.0")
    (.setGroupId (:group project))
    (.setArtifactId (:name project))
    (.setVersion (:version project))
    
    (.setDescription (:description project))
    (.setUrl (:homepage project))
    (.setContributors (map make-contributor (:authors project)))

    (.setDependencies 
     (concat 
      (map (partial make-dependency nil) (:dependencies project))
      (map (partial make-dependency "test") (:dev-dependencies project))))
    (.setPackaging "jar")))

(defn pom
  "Create a Maven-compatible pom.xml"
  [project & args]
  (with-open [writer (duck-streams/writer "pom.xml")]
    (.write (MavenXpp3Writer.) writer (make-model project))))
