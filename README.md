Clojars.org Leiningen Plugin
============================

Deprecated: This plugin is no longer being maintained as Leiningen can now [push to Clojars](https://github.com/ato/clojars-web/wiki/Pushing) with `lein deploy clojars`.

About
-----

This is a simple plugn for interacting with Clojars.org directly from 
Leiningen.

Setup
-----

Add lein-clojars as a Leiningen (1.x) plugin:

    lein plugin install lein-clojars 0.9.1

Or for Leiningen 2 add it to ~/.lein/profiles.clj:

    {:user {:plugins [[lein-clojars "0.9.1"]]}}

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

License
-------

Licensed under the EPL, same as Clojure and Leiningen.  See COPYING.
