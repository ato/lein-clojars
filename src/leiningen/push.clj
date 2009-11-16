(ns leiningen.push
  (:require [lancet])
  (:use [clojure.contrib.java-utils :only [as-file]]
        [clojure.contrib.duck-streams :only [copy]]
        [leiningen.deps :only [deps-if-missing]]
        [leiningen.jar :only [jar]]
        [leiningen.pom :only [pom]])
  (:import (com.jcraft.jsch JSch JSchException Logger)
           (java.io File FileInputStream)))

(let [re-repo #"(?:([^@]+)@)?([^:]+)(?::(\d+))?(?::(.*))?"]
  (defn parse-repo [repo]
    (let [[_ user host port path] (re-matches re-repo 
                                              (or repo "clojars@clojars.org"))]
      [(or user (System/getProperty "user.name")) 
       host
       (if port (Integer/parseInt port) 22)
       (or path ".")])))

(defn- add-identities [jsch]
 (let [homedir (File. (System/getProperty "user.home"))
       leindir (File. homedir ".leiningen")
       sshdir (File. homedir ".ssh")]
   (doseq [dir [leindir sshdir]
           name ["id_rsa" "id_dsa" "identity"]
           :let [file (File. dir name)]
           :when (do (println file) (.exists file))]
     (try
      (.addIdentity jsch (str file))
      (println "added identity" (str file))
      (catch JSchException e
        (println "Skipping invalid key" (str file)))))))

(defn- read-ack [in]
  (let [b (.read in)]
    (when-not (zero? b)
      (throw (Exception. (str "scp expected ACK but got " b))))))

(defn scp-send [repo files]
  (let [jsch (doto (JSch.) (add-identities))
        [user host port path] (parse-repo repo)
        session (doto (.getSession jsch user host port)
                  (.setConfig "StrictHostKeyChecking" "no")
                  (.connect))
        channel (doto (.openChannel session "exec")
                  (.setCommand (str "scp -p -t " \" path \"))
                  (.setErrStream System/err true))
        in (.getInputStream channel)
        out (.getOutputStream channel)]
    (try
     (.connect channel)
     (read-ack in)
     (doseq [path files]
       (let [file (as-file path)]
         (.write out (.getBytes (str "C0644 " (.length file) " "
                                     (.getName file) "\n")))
         (.flush out)
         (read-ack in)

         (copy (FileInputStream. file) out)
         (.write out 0)
         (read-ack in)))
     (.close out)
     (.read in) ; wait for remote close
     (finally
      (.disconnect channel)
      (.disconnect session)))))

(defn push
  "Push a jar to the Clojars.org repository over scp"
  [project & [repo]]
  (JSch/setLogger (proxy [Logger] []
                  (isEnabled [level] true)
                  (log [level message] (println level message))))
  (let [jarfile (str (:root project) "/" (:name project) ".jar")]
    (pom project)
    (jar project)
    (scp-send repo jarfile)))
