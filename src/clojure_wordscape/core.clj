(ns clojure-wordscape.core
  (:gen-class)
  (:require clojure.string)
  ;; (:require [cljfx.api :as fx])
  )

;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (println "Hello, World!"))


(def words
  (->> (slurp "words.txt")
       (re-seq #"\w+")
       (filter #(> (count %) 2))
       (map clojure.string/upper-case)
       (distinct)))

(def num-words 10)
(def num-letters 5)

(def first-word
  (->> words
       (filter (fn [word] (== (count word) num-letters)))
       (rand-nth)))

first-word
seq first-word

(def lettercounts (frequencies first-word))

(->> words
     (filter #(<= (count %) num-letters))
     (filter (fn [word]
               (let [counts (frequencies word)]
                 (every? #(<= (get counts % 0) (get lettercounts % 0)) (keys counts)))))
     (shuffle)
     (take num-words))

(defn make-riddle [num-words num-letters]
  (let [first-word (->> words
                        (filter (fn [word] (== (count word) num-letters)))
                        (rand-nth))
        lettercounts (frequencies first-word)
        more-words (->> words
                        (filter #(<= (count %) num-letters))
                        (filter (fn [word]
                                  (let [counts (frequencies word)]
                                    (every? #(<= (get counts % 0) (get lettercounts % 0)) (keys counts)))))
                        (shuffle)
                        (take num-words))]
    (if (== (count more-words) num-words)
      {:letters (seq first-word)
       :words more-words}
      (make-riddle num-words num-letters))))

(defn print-riddle [riddle-state]
  (let [riddle (riddle-state :riddle)
        words-found (riddle-state :words-found)]
    (println "Letters: " (riddle :letters))
    (println "Words: ")
    (doseq [word (riddle :words)]
      (if (words-found word)
        (println word)
        (println (repeat (count word) "_"))))))


(def riddle (make-riddle 10 5))


(def riddle-state {:riddle riddle
                   :words-found #{}})
(riddle :words)
(print-riddle riddle-state)

(defn eval-guess [riddle-state guess]
  (let [riddle (:riddle riddle-state)
        words-found (:words-found riddle-state)
        guess (clojure.string/upper-case guess)]
    (if (contains? (set (riddle :words)) guess)
      (if (contains? words-found guess)
        (do
          (println "You already found that word!")
          riddle-state)
        (do
          (println "You found a word!")
          {:riddle riddle,
           :words-found (conj words-found guess)}))
      (do
        (println "That's not a word!")
        riddle-state))))

;; repeatedly read a word from stdin until all are found
(defn play-riddle [riddle-state]
  (let [riddle (:riddle riddle-state)
        words-found (:words-found riddle-state)]
    (if (== (count words-found) (count (riddle :words)))
      (println "You found all the words!")
      (do
        (print-riddle riddle-state)
        (play-riddle (eval-guess riddle-state (read-line)))))))

(play-riddle riddle-state)