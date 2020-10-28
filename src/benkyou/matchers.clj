(ns benkyou.matchers)

(defmacro matcher
  [bindings & body]
  `(fn [tokens# token-pos#]
     (let [~'followed-by (fn [matcher#]
                           (matcher# tokens# (+ 1 token-pos#)))
           ~'matches (fn [matcher#]
                       (matcher# tokens# token-pos#))]
       (let [~@bindings (get tokens# token-pos#)]
         ~@body))))

(defmacro defmatcher
  [name bindings  & body]
  `(def ~name
     (matcher ~bindings ~@body)))

(defmatcher verb?
  [token]
  (or (= "nai" (:romaji token))
      (= :verb (:surface token))
      (= :auxiliary-verb (:surface token))))

(defmatcher conjunctive?
  [token]
  (= :conjunctive (:particle token)))

(defmatcher te-form?
  [{type :type :as token}]
  (and (matches verb?)
       (followed-by conjunctive?)))

