(ns benkyou.verbs)

(defn is-te-form?
  [token]
  (not (nil? (:te-form token))))

(defn is-desu?
  [{romaji :romaji}]
  (= "desu" romaji))

(defn is-tai-form?
  [{romaji :romaji}]
  (= "tai" (apply str (take-last 3 romaji))))

(defn explain-te-form
  [token]
  [:div
   [:br]
   [:p "This verb is conjugated to the te form"]
   [:p (str "This is an extremely common conjugation often used to connect two verbs together or "
            "to connect sentences")]
   [:br]
   [:p "An example of connecting verbs would be te + iru"]
   [:p "iru is the verb to be, or to exist"]
   [:p "when combined with the te form of another verb, it turns it into a status"]
   [:p "For example, taberu (to eat) -> tabete (te form) -> tabete + iru (to be eating)"]
   [:br]
   [:p "It can also be used to connect two sentences, sort of like 'and', for example"]
   [:p "nomu (to drink) -> nonde (te form) -> nonde taberu (eat and drink)"]])

(defn explain-tai-form
  [token]
  [:div
   [:br]
   [:p "This verb is conjugated to the tai form"]
   [:p "This indicates that you want to do something"]
   [:p "E.g. taberu (to eat) tabetai (want to eat)"]])

(defn explain-desu
  []
  [:div
   [:p "This is a very commonly used verb, and it's the equivalent of 'is'"]
   [:p "A very common pattern would be 'Aは(wa) B です' meaning A is B"]])

(defn explain-verb
  [token]
  [:div
   [:p "This is a verb. It's a doing word, or the action of the sentence"]
   (cond
     (is-te-form? token) (explain-te-form token)
     (is-tai-form? token) (explain-tai-form token)
     (is-desu? token) (explain-desu))])
