(defproject swingrepl "1.0.0-SNAPSHOT"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :java-fork "true"
  :javac-debug "true"
  :description "A Swing Clojure REPL using BeanShell's JConsole"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[lein-javac "1.2.1-SNAPSHOT"]
                     [lein-clojars "0.5.0-SNAPSHOT"]
                     [swank-clojure "1.2.1"]]
  :repositories {"clojure-releases" "http://build.clojure.org/releases"}
  :main org.dipert.swingrepl.main)
