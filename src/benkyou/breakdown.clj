(ns benkyou.breakdown
  (:require [benkyou.kuro :as kuro]
            [benkyou.adjectives :as adjectives]
            [benkyou.particles :as particles]
            [benkyou.verbs :as verbs]))

(defn- token-type
  [{surface :surface particle :particle :as token}]
  (cond
    (and (= :pronoun particle) (= :noun surface)) :pronoun
    (= :particle surface) :particle
    (and (= "n" (:romaji token)) (= :affix particle)) :particle
    (= :suffix particle) :suffix
    (= "nai" (:romaji token)) :verb
    (= :adnominal surface) :adjective
    (= :adjective surface) :adjective
    (= :adjective-base particle) :adjective
    (= :verb surface) :verb
    (= :auxiliary-verb surface) :verb
    (= :noun surface) :noun
    (= :adverb surface) :adverb
    (= "iie" (:original-romaji token)) :iie
    (= "hai" (:original-romaji token)) :hai))

(defn squash-adjectives
  [tokens]
  (loop [unsquashed (vec tokens)
         squashed []]
    (let [token (first unsquashed)
          next-token (second unsquashed)]
      (if (nil? token)
        squashed
        (if (and (= :auxiliary-verb (get-in next-token [:kuro-token :surface]))
                 (= :adjective (:type token)))
          (recur (rest (rest unsquashed)) (conj squashed (assoc token :inflection next-token)))
          (recur (rest unsquashed) (conj squashed token)))))))

(defn squash-verbs
  [tokens]
  (let [squashed (loop [unsquashed (vec tokens)
                        squashed []]
                   (let [token (first unsquashed)
                         second-token (second unsquashed)
                         third-token (get unsquashed 3)]
                     (if (nil? token)
                       squashed
                       (if (not (= :verb (:type token)))
                         (recur (rest unsquashed) (conj squashed token))
                         (let [aux-verbs (loop [tokens (rest unsquashed)
                                                aux-verbs []]
                                           (if (= :auxiliary-verb (get-in (first tokens) [:kuro-token :surface]))
                                             (recur (rest tokens) (conj aux-verbs (first tokens)))
                                             aux-verbs))]
                           (recur (rest (subvec (vec unsquashed) (count aux-verbs)))
                                  (conj squashed (assoc token :aux-verbs aux-verbs))))))))]
    (map (fn [token]
           (assoc token
                  :kanji
                  (str (:kanji token) (apply str (map :kanji (:aux-verbs token))))
                  :romaji
                  (str (:romaji token) (apply str (map :romaji (:aux-verbs token))))))
         squashed)))

(defn squash-te-form
  [tokens]
  (let [squashed (loop [unsquashed (vec tokens)
                        squashed []]
                   (let [token (first unsquashed)
                         next-token (second unsquashed)]
                     (if (nil? token)
                       squashed
                       (if (= :conjunctive (get-in next-token [:kuro-token :particle]))
                         (recur (rest (rest unsquashed)) (conj squashed (assoc token :te-form next-token)))
                         (recur (rest unsquashed) (conj squashed token))))))]
    (map (fn [token]
           (assoc token
                  :kanji (str (:kanji token) (get-in token [:te-form :kanji]))
                  :romaji (str (:romaji token) (get-in token [:te-form :romaji]))))
         squashed)))

(defn expand-inflections
  [token]
  (let [inflection (:inflection token)
        surface-form (get-in token [:kuro-token :surface-form])]
    (if (nil? inflection)
      (assoc token :kanji surface-form)
      (assoc token
             :kanji (str surface-form (get-in inflection [:kuro-token :surface-form]))
             :romaji (str (:romaji token) (:romaji inflection))))))

(defn explain-suffix
  [token]
  [:div
   [:p "Suffixes are a common ending to a noun."]
   [:p "A common example would be 屋 (ya) meaning shop"]
   [:p "e.g. Bread屋 (Bread Shop)"]])

(defn add-explanations
  "Takes a token and adds an :explanation to it.
  Currently returns a hiccup html array, which is not ideal
  but I'm rushing so eh"
  [{type :type :as token}]
  (let [explanation (cond
                      (= :adjective type) (adjectives/explain-adjective token)
                      (= :particle type) (particles/explain-particle token)
                      (= :verb type) (verbs/explain-verb token)
                      (= :pronoun type) [:div
                                         [:p "Pronouns are similar to nouns but describe something already mentioned"]
                                         [:p "For example, I, me, you, he, she, it"]
                                         [:p "Japanese doesn't use them all too often"]]
                      (= :noun type) [:p "Nouns are things, like car, chair, apple, etc."]
                      (= :adverb type) [:p (str "Adverbs modify the verb or describe how it's done, "
                                                "e.g. quickly, slowly, already")]
                      (= :suffix type) (explain-suffix token)
                      (= :hai type) [:p "Basically means yes"]
                      (= :iie type) [:div [:p "Basically means no"]
                                     [:p "Can also mean 'no problem'"]]
                      :else [:p "Sorry, I don't know how to explain this yet"])]
    (assoc token :explanation explanation)))

(defn remove-kuro-token
  [token]
  (-> token
      (update-in [:inflection] dissoc :kuro-token)
      (dissoc :kuro-token)))

(defn breakdown-sentence
  [sentence]
  (->>
   (kuro/with-tokenizer (kuro/tokenize sentence))
   (map (fn [token]
           {:type (token-type token)
           :original-text (:surface-form token)
           :dictionary-form (:conjugation-form token)
           :romaji (let [romaji (:original-romaji token)]
                     (if (= "wa" (:romaji token))
                       "wa"
                       romaji))
           :kuro-token token}))
   (vec)
   (squash-adjectives)
   (map expand-inflections)
   (squash-verbs)
   (squash-te-form)
   (map add-explanations)))
