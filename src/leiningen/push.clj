(ns leiningen.push
  (:require [lancet])
  (:use [leiningen.deps :only [deps-if-missing]]
        [leiningen.jar :only [jar]]
        [leiningen.pom :only [pom]])
  (:import (com.jcraft.jsch JSch JSchException)
           (java.io File)))

(defn- add-identities [jsch]
 (let [homedir (File. (System/getProperty "user.home"))
       leindir (File. homedir ".leiningen")
       sshdir (File. homedir ".ssh")
       jsch (JSch.)]
   (doseq [dir [leindir sshdir]
           name ["id_rsa" "id_dsa" "identity"]
           :let [file (File. dir name)]
           :when (.exists file)]
     (try
      (.addIdentity jsch (str file))
      (catch JSchException e
        (println "Skipping invalid key" (str file)))))))


(defn push
  "Push a jar to the Clojars.org repository"
  [project & [repo]]
  (let [jarfile (str (:root project) "/" (:name project) ".jar")]
    (pom project)
    (jar project)
    (doto (Scp.)
      (.setProject lancet/ant-project)
      (.setVerbose true)
      (.setPassphrase "")
      (.setTodir "clojars@clojars.org:")
      (.setTrust true)
      (.setKeyfile "/home/ato/.ssh/id_rsa")
      (.addFileset
       (lancet/fileset {:dir "/tmp/baz"}))
      (.execute))))


