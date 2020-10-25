(ns benkyou.ui)

(defn search-box
  ([] (search-box ""))
  ([sentence]
   [:form {:method "GET" :action "analyze"}
    [:input {:class "text-xl p-3 my-4 w-full border-gray-400 border-b border-b-1 shadow-md outline-none"
             :name "sentence"
             :value sentence}]
    [:input {:type "submit"
             :class "px-3 py-2 shadow bg-blue-800 text-white text-xl rounded cursor-pointer"
             :value "Analyze"}]]))

(defn example
  [sentence]
  [:li {:class "text-blue-600 text-underline my-2 text-lg"}
   [:a {:href (str "/analyze?sentence=" sentence)} sentence]])

(defn example-box
  []
  [:div {}
   [:h2 {:class "text-gray-800"} "Here are some examples"]
   [:ui {:class "ml-4 pb-4"}
    (example "パン屋はどこですか")
    (example "このサイトは悪くないですね")
    (example "太いになるまで、りんごを食べたい")
    (example "抵抗しても無駄だ")
    (example "レストランがあります")
    (example "お前はもう死んでいる")]])
  
