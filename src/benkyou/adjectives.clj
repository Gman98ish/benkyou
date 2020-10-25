(ns benkyou.adjectives)


(defn is-ii-adjective?
  [token]
  (let [adj (get-in token [:kuro-token :conjugation-form])]
    (= (last adj) \い)))
    
(defn is-negative-ii-adjective?
  [{inflection :inflection}]
  (= (:romaji inflection) "nai"))

(defn is-demonstrative?
  [{romaji :romaji}]
  (or (= "kono" romaji)
      (= "sono" romaji)
      (= "ano" romaji)))

(defn explain-demonstrative
  [token]
  [:div
   [:p (str "Specifically this is a special kind of adjective, called a demonstrative. "
            "It's the equivalent of 'this' or 'that'")]
   [:br]
   [:p {:class "pb-2"} "For example"]
   [:ul
    [:li "kono ringo - this apple (close to the speaker)"]
    [:li "sono ringo - that apple (close to the listener)"]
    [:li "ano ringo - that apple (far away from both)"]]])

(defn explain-ii-adjective
  [token]
  [:div
   [:p (str "Specifically, this is an ii adjective (pronounced 'ee')"
            (if (is-negative-ii-adjective? token) " which has been conjugated into the negative form" ""))]
   [:br]
   (if (is-negative-ii-adjective? token)
     (let [base (apply str (drop-last (get-in token [:kuro-token :conjugation-form])))
           stem (str base "く")]
       [:div
        [:p (str "To conjugate an ii adjective, you first need to get the stem. "
                 "You can do this by taking off the last い (i) and adding く (ku)")]
        [:p (str "For example: " (str base "い") " -> " base " -> " stem)]
        [:br]
        [:p "Once you have the stem, you add ない"]
        [:p "For example: " stem " -> " (:kanji token)]]))]) 

(defn explain-adjective
  [token]
  [:div
   [:p "This is an adjective. It describes the word following it"]
   [:br]
   (cond
     (is-ii-adjective? token) (explain-ii-adjective token)
     (is-demonstrative? token) (explain-demonstrative token))])

