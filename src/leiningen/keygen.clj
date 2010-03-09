(ns leiningen.keygen
  (:import (com.jcraft.jsch JSch KeyPair)
           (java.io File)
           (java.net InetAddress UnknownHostException)))

(defn- get-hostname []
  (try
   (.getHostName (InetAddress/getLocalHost))
   (catch UnknownHostException e
     "localhost")))

(defn keygen
  "Generate an SSH key pair for authenticatin with Clojars.org"
  [& args]  
  (println "Generating a new SSH keypair...")
  (let [confdir (File. (System/getProperty "user.home") ".leiningen")
        privfile (str (File. confdir "id_rsa"))
        pubfile (str (File. confdir "id_rsa.pub"))
        keypair (KeyPair/genKeyPair (JSch.) KeyPair/RSA)]
    (.mkdirs confdir)
    (.writePrivateKey keypair privfile)
    (println "Private key written to" privfile)
    (.writePublicKey keypair pubfile
                     (str (System/getProperty "user.name") "@"
                          (get-hostname) " (Leiningen)"))
    (println "Public key written to" pubfile)
    (println "\nPaste the public key below into your profile at http://clojars.org/profile")
    (println (slurp pubfile))))

