clj-swingrepl
=============

Swing Clojure REPL that uses BeanShell's JConsole component.

Build
-----

* Install [Leiningen](http://github.com/technomancy/leiningen)
* `lein deps`
* `lein compile-java`

Run
---

You can run `lein swank` and connect with SLIME via Emacs, or you can build a distributable jar with `lein uberjar`

To run, use something like `java -jar swingrepl-standalone.jar`

Todo
----

* Drop into `user` namespace on startup by default
* Completions for things available in the current namespace: JConsole has its own completions mechanism that might be hooked into
* Bracket, parentheses, quote completion/matching/highlighting
* Better as-library behavior: provide configurable automatic imports

Notes
-----

* A Clojure implementation of something like JConsole might be nice

Thanks
------

Many props to the BeanShell dude for making such a cool REPL.
