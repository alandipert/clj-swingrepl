(ns org.dipert.swingrepl.main
  "Swing Clojure REPL using BeanShell's JConsole"
  (:require clojure.main clojure.repl)
  (:import (javax.swing JFrame)
           (java.awt.event WindowEvent)
           (bsh.util JConsole))
  (:gen-class))

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

(declare ^:dynamic *locals*)
(defn eval-with-locals
  "Evals a form with given locals. The locals should be a map of symbols to
  values."
  [locals form]
  (binding [*locals* locals]
    (eval
      `(let ~(vec (mapcat #(list % `(*locals* '~%)) (keys locals)))
         ~form))))


(def ^{:doc "Formatted Clojure version string"
       :private true}
     clj-version
     (apply str (interpose \. (map *clojure-version* [:major :minor :incremental]))))

(defn- set-defaults! []
  (set! *print-level* 15)
  (set! *print-length* 103))

(def ^{:doc "Default REPL options"
       :private false}
     default-opts
     {:width 972
      :height 400
      :title (str "Clojure " clj-version " REPL")
      :prompt #(print "user=> ")
      :init set-defaults!
      :eval eval
      :on-close JFrame/DISPOSE_ON_CLOSE})

(def ^{:doc "Default debug REPL options"
       :private false}
     default-dbg-opts
     {:title (str "Clojure " clj-version " Debug REPL")
      :prompt #(print "dr => ")
      :eval (partial eval-with-locals (local-bindings))})

(defn- make-repl-thread [console & repl-args]
  (binding [*out* (.getOut console)
            *in*  (clojure.lang.LineNumberingPushbackReader. (.getIn console))
            *err* (.getOut console)]
    (Thread. (bound-fn []
               (apply clojure.main/repl repl-args)))))

(defn- window-closing-dispatcher [window]
  (fn [] (.dispatchEvent window (WindowEvent. window WindowEvent/WINDOW_CLOSING))))

(defn make-repl-jframe
  "Displays a JFrame with JConsole and attached REPL."
  ([] (make-repl-jframe {}))
  ([optmap]
     (let [options (merge default-opts optmap)
           {:keys [title width height on-close prompt init eval]} options
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
          (let [thread (make-repl-thread console :prompt prompt :init init :eval eval)
                stopper (clojure.repl/thread-stopper thread)]
            (.setInterruptFunction console (fn [reason] (stopper reason)))
            (.setEOFFunction console (window-closing-dispatcher jframe))
            (.start thread)
            (.setVisible jframe true))))))

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
  `(let [locals# (local-bindings)]
     (make-repl-jframe (merge default-opts default-dbg-opts ~optmap)))))

(defn -main
  [& args]
  (make-repl-jframe {:on-close JFrame/EXIT_ON_CLOSE}))
