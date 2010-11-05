(defproject swingrepl "1.3.0"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :java-fork "true"
  :javac-debug "true"
  :description "A Swing Clojure REPL using BeanShell's JConsole"
  :dependencies [[org.clojure/clojure "1.3.0-alpha2"]]
  :dev-dependencies [[lein-javac "1.2.1-SNAPSHOT"]
                     [swank-clojure "1.3.0-SNAPSHOT"]]
  :hooks [leiningen.hooks.javac]
  :main org.dipert.swingrepl.main)
