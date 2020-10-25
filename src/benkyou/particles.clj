(ns benkyou.particles)

(defn explain-topic-particle
  [token]
  [:div
   [:p (str "This is the topic particle, it tells you what the sentence is about. "
            "You can think of it as 'As for X'")]
   [:p "For example, 'Appleは' would mean 'as for apples'"]])

(defn explain-ne-particle
  [token]
  [:div
   [:p (str "This is a particle you stick at the end of the sentence when you're making "
            "a comment and want someone's opinion, it's vaguely equivalent to "
            "isn't it?")]
   [:p "For example, the weather's nice to day, isn't it?"]])

(defn explain-ka-particle
  [token]
  [:div
   [:p "This can be stuck on the end of a sentence to make it a question"]])

(defn explain-no-particle
  [token]
  [:div
   [:p "This is a multi purpose particle, but its most common usage is to indicate possession"]
   [:p "For example, 'my name' is 'watashi の namae' where 'watashi' is I and 'namae' is name"]])

(defn explain-mo-particle
  [token]
  [:div
   [:p "This is a multi purpose particle, it most commonly means 'also' e.g."]
   [:p "'Appleも' means 'also apples'"]
   [:br]
   [:p "It can also mean both (or neither depending on context)"]
   [:p "For example, 'appleも, orangeも' means apples or oranges"]
   [:br]
   [:p "When combined with te (て) as ても it an also mean 'even though' or 'in spite of' e."]])

(defn explain-object-particle
  [token]
  [:div
   [:p "This is a very common particle, and describes the object of the sentence"]
   [:br]
   [:p "The object of the sentence is the thing the subject is acting on"]
   [:p (str "For example, 'I eat apples', I is the subject (the thing doing the verb) "
            "eat is the verb and apples is the object (the thing that the verb is being done to)")]
   [:br]
   [:p (str "It's the same in Japanese, but instead of being indicated by the position of the sentence, "
            "it's marked with a particle")]])

(defn explain-made-particle
  [token]
  [:div
   [:p "This means either 'until' or 'up to'"]
   [:br]
   [:p "It's often used with times, for example 8:00まで - until 8:00"]
   [:br]
   [:p "It can be used with verbs to mean 'until something happens'"]
   [:p "For example, 大人になる means to grow up, 大人になる + まで means 'until you grow up'"]])

(defn explain-subject-particle
  [token]
  [:div
   [:p "The subject particle indicates what's doing the verb"]
   [:p "In the sentence 'I eat apples' 'I' is the subject"]
   [:br]
   [:p (str "The topic of the sentence and the subject of the sentence are often the same thing, "
            "so the subject particle often gets replaced with the topic particle")]
   [:p "However, you can still use the subject particle for emphasis, or when the subject isn't the topic"]
   [:br]
   [:p "For example: 'watashi ga shinda'"]
   [:p "watashi (meaning I) is the subject of the sentence, shinda is a verb meaning died"]
   [:p "so this sentence means 'I died'"]])

(defn explain-ni-particle
  [token]
  [:div
   [:p "This is by far the most flexible and confusing particle"]
   [:p "It can be used to indicate, time, place, indirect object, motion, adverbs, and more"]
   [:br]
   [:p "The main use cases for it are:"]
   [:br]
   [:p [:strong "Time"]]
   [:p "ni is often used to indicate when something happens e.g. 8:00に (at 8:00)"]
   [:br]
   [:p [:strong "Movement"]]
   [:p "ni is often used with movement verbs to indicate where something is going"]
   [:p "For example, uchi is home, and iku is go: uchi ni iku (I go home)"]
   [:br]
   [:p [:strong "Indirect object"]]
   [:p "When a verb acts on two things, it has an object and an indirect object"]
   [:p "A good example of this is the verb send, you can send something to someone"]
   [:p (str "So 'something' is the object, and 'someone' is the indirect object, 'someone ni' is"
            " like saying to someone")]])

(defn explain-n-particle
  [token]
  [:div
   [:p (str "ん (pronounced unn) can be used when offering up an explanation or when you want "
            "the person you're talking to respond in more detail")]
   [:br]
   [:p "It's a very nuanced particle, but it makes your Japanese sound really natural when used properly"]])

(defn explain-particle
  [token]
  (let [particle (:romaji token)]
    [:div
     [:p "Particles go after words and indicate their role in the sentence"]
     [:br]
     (cond
       (= "wa" particle) (explain-topic-particle token)
       (= "ne" particle) (explain-ne-particle token)
       (= "ka" particle) (explain-ka-particle token)
       (= "no" particle) (explain-no-particle token)
       (= "mo" particle) (explain-mo-particle token)
       (= "wo" particle) (explain-object-particle token)
       (= "ga" particle) (explain-subject-particle token)
       (= "made" particle) (explain-made-particle token)
       (= "ni" particle) (explain-ni-particle token)
       (= "n" particle) (explain-n-particle token)
       :else [:p "Sorry, I don't know this particle yet"])]))
