(ns clojure-wordscape.core
  (:gen-class)
  (:require clojure.string))

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

(def words-found #{})
(defn print-riddle [riddle]
  (println "Letters: " (riddle :letters))
  (println "Words: ")
  (doseq [word (riddle :words)]
    (if (words-found word)
      (println word)
      (println (repeat (count word) "_")))))

(def riddle (make-riddle 10 5))

(str riddle)
(print-riddle riddle)

