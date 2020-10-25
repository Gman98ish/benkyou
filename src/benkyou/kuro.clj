;; I've nicked this from https://github.com/kanasubs/kuromojure
;; and added my own twist on converting the Token objects to clojure maps
;; I've pulled it in this way because I couldn't get it working with lein deps
;; TODO: Pull it in normally

(ns benkyou.kuro
  (:require [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            [benkyou.kana :as kana])
  (:import (org.atilika.kuromoji Tokenizer Tokenizer$Mode)))

(def ^:dynamic ^Tokenizer *tokenizer*)
(def ^:dynamic *mode*)
(def ^:dynamic *debug* nil)

(defmacro with-tokenizer
  "Builds a tokenizer with mode (:normal, :search, :extended) as input,
   providing a context in which the tokenizer can be used with fns that need it."
  [mode & body]
  `(let [wrapper-mode# (try (->> ~mode name str/upper-case)
                           (catch ClassCastException _# "NORMAL"))
         mode# (let [mode-str# "org.atilika.kuromoji.Tokenizer$Mode/"]
                 (->> wrapper-mode# (str mode-str#) read-string eval))]
    (binding [*tokenizer* (-> (Tokenizer/builder) (.mode mode#) .build)
              *mode* wrapper-mode#]
      ~mode ~@body)))

(defonce jp->en-mapping
  {; TODO organize into categories:
   ;   - adjectives
   ;   - adverbs‎
   ;   - conjunctions‎
   ;   - counters‎
   ;   - interjections‎
   ;   - interrogatives‎
   ;   - nouns‎
   ;   - numerals‎
   ;   - particles‎
   ;   - postpositions‎
   ;   - pronouns‎
   ;   - verbs‎
   "固有名詞" :proper, "特殊" :special, "一般" :misc
   "名詞" :noun, "代名詞" :pronoun
   "名" :given-name, "姓" :surname, "人名" :person
   "組織" :organization "地域" :place "国" :country
   "助詞" :particle "助詞類接続" :particle_conjunction "接続詞" :conjunction
   "縮約" :contraction "感動詞" :interjection "間投" :interjection
   "引用文字列" :quotation "引用" :quote "連語" :compound
    "接頭詞" :prefix "接尾" :suffix "接続詞的" :suffix-conjunctive
   "助数詞" :classifier
   "助動詞語幹" :aux "助動詞" :auxiliary-verb "非自立" :affix
   "動詞" :verb "副詞" :adverb
   "サ変接続" :verbal "動詞接続" :verbal "非言語音" :non-verbal
   "副詞可能" :adverbial "副助詞" :adverbial "名詞接続" :nominal
   "連体詞" :adnominal "連体化" :adnominalizer "副詞化" :adnominalizer
   "アルファベット" :alphabetic "接続助詞" :conjunctive
   "形容詞" :adjective "ナイ形容詞語幹" :nai-adjective "形容動詞語幹" :adjective-base
   "数" :numeric "数接続" :numerical "形容詞接続" :adjectival "終助詞" :final
   "間投助詞" :interjective "並立助詞" :coordinate "動詞非自立的" :verbal_aux
   "自立" :main "基本形" :basic-form
   "記号" :symbol "句点" :period "空白" :space "読点" :comma
   "括弧開" :open-bracket "括弧閉" :close-bracket
   "格助詞" :case "係助詞" :dependency
   "副助詞／並立助詞／終助詞" :adverbial/conjunctive/final
   "その他" :other "フィラー" :filler "語断片" :fragment "未知語" :unknown
   ; exceptions
   "動詞,非自立" [:verb :auxiliary] "形容詞,非自立" [:adjective :auxiliary]
   "名詞,一般" [:noun :common]})

(defn jp->en
  "Converts word in Japanese to English. Defaults to Japanese in case it's not
   mapped yet."
  [word] (get jp->en-mapping word word))

(defn moji->clj-token
  "Accepts a org.atilika.kuromoji.Token and creates a Clojure map with the
   token attributes."
  [token]
  (-> token bean (update :allFeaturesArray seq)))

(defn ->exceptions
  "Accepts a clj-token and compounds the exceptions."
  [coll]
  (let [joined (str/join "-" coll)]
    (-> (fn [[old niw]]
          (let [replaced (str/replace-first joined (re-pattern old) niw)]
            (if-not (= joined replaced) replaced)))
        (keep [["動詞-非自立" "動詞,非自立"] ["形容詞-非自立" "形容詞,非自立"]
               ["名詞-一般" "名詞,一般"]])
        first
        (#(if % (str/split % #"-") coll)))))

(defn clj->en-token
  "Accepts a clj-token and translates its features to english."
  [token]
  (-> token
      (update :partOfSpeech (comp flatten (partial map jp->en) ->exceptions))
      (update :allFeaturesArray #(concat
                                  (->> (drop-last 3 %) ->exceptions (map jp->en) flatten)
                                  (take-last 3 %)))))

(defn clj->mojure-token
  [token]
  (let [features (-> (:allFeatures token)
                     (str/split #",")
                     (->> (map jp->en))
                     (->> (map #(if (= "*" %) nil %)))
                     (vec))
        surface-form (get-in token [:allFeatures :surfaceForm])]
    {:surface (get features 0)
     :particle (get features 1)
     :part-of-speech-1 (get features 2)
     :part-of-speech-2 (get features 3)
     :part-of-speech-3 (get features 4)
     :conjugation-type (get features 5)
     :conjugation-form (get features 6)
     :original-form (get features 7)
     :surface-form (:surfaceForm token)
     :reading (get features 8)
     :romaji (kana/reading->romaji (get features 8))
     :original-romaji (kana/reading->romaji (get features 7))
     :pronunciation (get features 9)}))

(defn raw-tokenize
  "Segments text into an ordered seq of org.atilika.kuromoji.Token tokens.
   Must be used in the context of with-tokenizer."
  [s] (seq (.tokenize *tokenizer* s)))

(defn clj-tokenize
  "Segments text into an ordered seq of clj tokens.
   Must be used in the context of with-tokenizer."
  [s] (map moji->clj-token (raw-tokenize s)))

(defn tokenize
  "Segments text into an ordered seq of kuromojure tokens.
   Must be used in the context of with-tokenizer."
  [s] (map clj->mojure-token (clj-tokenize s)))


