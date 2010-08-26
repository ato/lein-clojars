Clojars.org Leiningen Plugin
============================

This is a simple plugn for interacting with Clojars.org directly from 
Leiningen.

Setup
-----

Add lein-clojars to your project's dev-dependencies.  For example:

  (defproject my-cool-library "1.0"
    :dev-dependencies [[lein-clojars "0.6"]]
    :repositories [["clojars" "http://clojars.org/repo"]])

Create a Clojars account and paste your SSH public key into your [profile] [1].
If you don't have ssh-keygen available -- perhaps you're using Windows -- 
then you can use:

    lein keygen

SSH keys will searched for in ~/.leiningen and ~/.ssh under the names id_rsa,
id_dsa and identity.

[1]: http://clojars.org/profile

Usage
-----

To push your project to the Clojars repository, simply type:

    lein push

TODO
-----

Some ideas for extra commands include: search, register and add-dep.

