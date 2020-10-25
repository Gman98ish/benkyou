(ns benkyou.kana)


(defonce katakana->romaji-mapping
  {
   ;; Vowels
   "ア" "a",
   "イ" "i",
   "ウ" "u",
   "エ" "e",
   "オ" "o",

   ;; K-s
   "カ" "ka",
   "キ" "ki",
   "ク" "ku",
   "ケ" "ke",
   "コ" "ko",
   "キャ" "kya",
   "キュ" "kyu",
   "キョ" "kyo",

   ;; S
   "サ" "sa",
   "シ" "shi",
   "ス" "su",
   "セ" "se",
   "ソ" "so",
   "シャ" "sha",
   "シュ" "shu",
   "ショ" "sho",

   ;; T
   "タ" "ta",
   "チ" "chi",
   "ツ" "tsu",
   "テ" "te",
   "ト" "to",
   "チャ" "cha",
   "チュ" "chu",
   "チョ" "cho",

   ;; N
   "ナ" "na",
   "ニ" "ni",
   "ヌ" "nu",
   "ネ" "ne",
   "ノ" "no",
   "ニャ" "nya",
   "ニュ" "nyu",
   "ニョ" "nyo",

   ;; H
   "ハ" "ha",
   "ヒ" "hi",
   "フ" "fu",
   "ヘ" "he",
   "ホ" "ho",
   "ヒャ" "hya",
   "ヒュ" "hyu",
   "ヒョ" "hyo",

   ;; M
   "マ" "ma",
   "ミ" "mi",
   "ム" "mu",
   "メ" "me",
   "モ" "mo",
   "ミャ" "mya",
   "ミュ" "myu",
   "ミョ" "myo",

   "ヤ" "ya",
   "ユ" "yu",
   "ヨ" "yo",

   "ラ" "ra",
   "リ" "ri",
   "ル" "ru",
   "レ" "re",
   "ロ" "ro",
   "リャ" "rya",
   "リュ" "ryu",
   "リョ" "ryo",

   "ワ" "wa",
   "ヲ" "wo",

   "ン" "n"

   "ガ" "ga",
   "ギ" "gi",
   "グ" "gu",
   "ゲ" "ge",
   "ゴ" "go",
   "ギャ" "gya",
   "ギュ" "gyu",
   "ギョ" "gyo",

   "ザ" "za",
   "ジ" "ji",
   "ズ" "zu",
   "ゼ" "ze",
   "ゾ" "zo",
   "ジャ" "ja",
   "ジュ" "ju",
   "ジョ" "jo",

   "ダ" "da",
   "ヂ" "ji",
   "ヅ" "dzu",
   "デ" "de",
   "ド" "do",
   "ヂャ" "ja",
   "ヂュ" "ju",
   "ヂョ" "jo",

   "バ" "ba",
   "ビ" "bi",
   "ブ" "bu",
   "ベ" "be",
   "ボ" "bo",
   "ビャ" "bya",
   "ビュ" "byu",
   "ビョ" "byo",

   "パ" "pa",
   "ピ" "pi",
   "プ" "pu",
   "ペ" "pe",
   "ポ" "po",
   "ピャ" "pya",
   "ピュ" "pyu",
   "ピョ" "pyo"
   })

(defn katakana->romaji
  [katakana]
  (get katakana->romaji-mapping katakana))

(defn reading->romaji
  [reading]
  (apply str (map katakana->romaji (map str reading))))

(defn add-romaji
  [token]
  (assoc token :romaji (reading->romaji (:reading token))))


