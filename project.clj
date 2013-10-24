(defproject uk.org.russet/swingrepl "1.4.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :description "A Swing Clojure REPL using BeanShell's JConsole"
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main org.dipert.swingrepl.main)
