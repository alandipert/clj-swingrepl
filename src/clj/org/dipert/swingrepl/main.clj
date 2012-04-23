(ns org.dipert.swingrepl.main
  "Swing Clojure REPL using BeanShell's JConsole"
  (:require clojure.main clojure.repl)
  (:import (javax.swing JFrame)
           (bsh.util JConsole))
  (:gen-class))

(def ^{:doc "Formatted Clojure version string"
       :private true}
     clj-version
     (apply str (interpose \. (map *clojure-version* [:major :minor :incremental]))))

(def ^{:doc "Default REPL display options"
       :private false}
     default-opts
     {:width 972
      :height 400
      :title (str "Clojure " clj-version)
      :on-close JFrame/DISPOSE_ON_CLOSE})

(defn set-defaults! []
  (set! *print-level* 15)
  (set! *print-length* 103))

(defn make-repl-thread [console & repl-args]
  (binding [*out* (.getOut console)
            *in*  (clojure.lang.LineNumberingPushbackReader. (.getIn console))
            *err* (.getOut console)]
    (Thread. (bound-fn []
               (apply clojure.main/repl repl-args)))))

(defn make-repl-jframe
  "Displays a JFrame with JConsole and attached REPL."
  ([] (make-repl-jframe {}))
  ([optmap]
     (let [options (merge default-opts optmap)
           {:keys [title width height on-close]} options
           jframe (doto (JFrame. title)
                    (.setSize width height)
                    (.setDefaultCloseOperation on-close)
                    (.setLocationRelativeTo nil))]
       (let [console (bsh.util.JConsole.)]
          (doto (.getContentPane jframe)
            (.setLayout (java.awt.BorderLayout.))
            (.add console))
          (doto jframe
            (.pack)
            (.setSize width height))
          (.requestFocus console)
          (let [thread (make-repl-thread console :init set-defaults!)]
            (.setREPLThread console thread)
            (.start thread)
            (.setVisible jframe true))))))


;; Debug swing macro
;
; Can't take credit for the debug macro, came from here:
; http://gist.github.com/252421
; Inspired by George Jahad's version: http://georgejahad.com/clojure/debug-repl.html
(defmacro local-bindings
  "Produces a map of the names of local bindings to their values."
  []
  (let [symbols (map key @clojure.lang.Compiler/LOCAL_ENV)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

(declare *locals*)
(defn eval-with-locals
  "Evals a form with given locals. The locals should be a map of symbols to
  values."
  [locals form]
  (binding [*locals* locals]
    (eval
      `(let ~(vec (mapcat #(list % `(*locals* '~%)) (keys locals)))
         ~form))))

(defmacro make-dbg-repl-jframe
  "Displays a JFrame with JConsole and attached REPL. The frame has the context
  from wherever it has been called, effectively creating a debugging REPL.

  Usage:

    (use 'org.dipert.swingrepl.main)
    (defn foo [a] (+ a 5) (make-dbg-repl-jframe {}) (+ a 2))
    (foo 3)

  This will pop up the debugging REPL, you should be able to access the var 'a'
  from the REPL.
  "
  ([] `(make-dbg-repl-jframe {}))
  ([optmap]
  `(let [opts# (merge default-opts ~optmap)
         jframe# (doto (JFrame. (:title opts#))
                   (.setSize (:width opts#) (:height opts#))
                   (.setDefaultCloseOperation (:on-close opts#))
                   (.setLocationRelativeTo nil))]
     (let [console# (bsh.util.JConsole.)]
       (doto (.getContentPane jframe#)
         (.setLayout (java.awt.BorderLayout.))
         (.add console#))
       (doto jframe#
         (.pack)
         (.setSize (:width opts#) (:height opts#)))
       (.requestFocus console#)
       (let [thread (make-repl-thread console
                                      :prompt #(print "dr => ")
                                      :eval (partial eval-with-locals (local-bindings)))]
         (.setREPLThread console thread)
         (.start thread)
         (.setVisible jframe# true))))))


(defn -main
  [& args]
  (make-repl-jframe {:on-close JFrame/EXIT_ON_CLOSE}))

