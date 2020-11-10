(ns benkyou.matchers)

(def ^:dynamic *token-pos* 0)
(def ^:dynamic *tokens* [])

(defmacro matcher
  "A matcher is a kind of predicate for tokens"
  [bindings & body]
  `(fn [tokens# token-pos#]
     (binding [*tokens* tokens#
               *token-pos* token-pos#]
       (let [~@bindings (get tokens# token-pos#)]
         ~@body))))

(defmacro defmatcher
  "Same as (def name (matcher body))"
  [name bindings  & body]
  `(def ~name
     (matcher ~bindings ~@body)))

(defmacro match-cond
  "Similar to clojure.core/cond but inserts the token and pos arguments
  for you"
  [tokens pos & conds]
  (let [with-args (mapcat (fn [[pred value]]
                         (list (concat pred (list tokens pos))
                               value))
                       (partition 2 conds))]
    `(cond ~@with-args)))

(defn preceded-by
  "Determines if a token is preceded by another matching a predicate
  Should only be run inside a matcher"
  [matcher]
  (matcher *tokens* (- *token-pos* 1)))

(defn followed-by
  "Determines if a token is followed by another matching a predicate
  Should onle be run inside a matcher"
  [matcher]
  (matcher *tokens* (+ 1 *token-pos*)))

(defn matches
  "Determines if the current token matches a predicate"
  [matcher]
  (matcher *tokens* *token-pos*))

(defmatcher verb?
  [token]
  (or (= "nai" (:romaji token))
      (= :verb (:surface token))
      (= :auxiliary-verb (:surface token))))

(defmatcher conjunctive?
  [token]
  (= :conjunctive (:particle token)))

(defmatcher particle?
  [token]
  (or (= (:surface token) :particle)
      (and (= "n" (:romaji token))
           (= :affix (:particle token)))))

(defmatcher suffix?
  [token]
  (= :suffix (:particle token)))

(defmatcher adjective?
  [token]
  (or (= :adnominal (:surface token))
      (= :adjective (:surface token))
      (= :adjective-base (:particle token))))

(defmatcher noun?
  [token]
  (= :noun (:surface token)))

(defmatcher adverb?
  [token]
  (or (= :adverb (:surface token))))

(defmatcher iie?
  [token]
  (= "iie" (:original-romaji token)))

(defmatcher hai?
  [token]
  (= "hai" (:original-romaji token)))

(defmatcher te-form?
  [{type :type :as token}]
  (and (matches verb?)
       (followed-by conjunctive?)))

