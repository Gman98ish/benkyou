(ns benkyou.web
  (:require [ring.util.response :as response]
            [ring.middleware.params]
            [ring.middleware.reload]
            [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :as route]
            [clojure.string])
  (:use [hiccup.core]
        [benkyou.ui :as ui]
        [benkyou.breakdown :as breakdown]))

(defn master-template
  ([] (master-template nil))
  ([& children]
   (html [:head
          [:meta {:http-equiv "Content-Type" :content "text/html" :charset "utf-8"}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
          [:link {:rel "stylesheet" :href "https://unpkg.com/tailwindcss@^1.0/dist/tailwind.min.css"}]]
         [:body
          [:div {:class "md:p-10 p-5 min-h-screen w-full bg-gray-100"} children]])))

(defn index
  [request]
  (let [body (master-template
              [:div {:class "text-3xl"} "勉強 - Benkyou "]
              [:div {:class "mt-3 w-full"}
               [:div {:class "text-xl text-gray-800"} "Try entering a Japanese sentence"]

               (ui/search-box)

               [:p {:class "text-gray-800 mb-1"}]
               (ui/example-box)])]
    {:status 200 :headers {} :body body}))

(defn colorize
  [{token-type :type :as token}]
  (cond
    (= :particle token-type) "text-red-700"
    (= :suffix token-type) "text-purple-700"
    (= :adjective token-type) "text-green-800"
    (= :adverb token-type) "text-teal-600"
    (= :verb token-type) "text-blue-700"))

(defn jisho-link
  [token]
  (str "https://jisho.org/search/" (:dictionary-form token)))

(defn explain
  [token]
  [:div {:class "shadow w-full md:mx-5 my-2 p-5 bg-white"}
   [:h3 {:class "text-xl"} [:span {:class (colorize token)} (:kanji token)] [:span (str " - " (:romaji token))] ]
   (if (not (= (:type token) :particle))
     [:a {:href (jisho-link token) :class "text-blue-700 underline"} "See dictionary definition"])
   [:p {:class "mt-3"}
    [:strong {:class (str (colorize token) "")} (if-let [type (:type token)]
                                                           (clojure.string/capitalize (name (:type token)))
                                                           "Unknown type")]
    (:explanation token)]])

(defn analyze
  [request]
  (let [sentence (get-in request [:query-params "sentence"])
        tokens (breakdown/breakdown-sentence sentence)
        without-punctuation (filter (fn [token] (not (= "" (:romaji token)))) tokens)]
    {:status 200 :headers {} :body (master-template
                                    [:div {:class "mt-3 w-full"} (ui/search-box sentence)]
                                    [:a {:class "text-blue-700 underline cursor-pointer" :href "/"}
                                     "Back to examples page"]
                                    [:h2 {:class "text-3xl mb-3 mt-6"} "Breakdown"]
                                    [:div {:class "mt-5 text-3xl"}
                                     (map (fn [token] [:span {:class (colorize token)} (:kanji token)]) tokens)
                                     [:br]
                                     (map (fn [token] [:span {:class (str
                                                                      (colorize token)
                                                                      " mr-1 italic font-light")}
                                                       (:romaji token)]) tokens)]
                                    [:div {:class "my-5"}]
                                    (if (empty? without-punctuation)
                                      [:div {:class "text-lg"}
                                       [:p (str "Looks like you've only entered romaji characters "
                                                "Unfortunately, we don't support that yet, please use something ")]
                                       
                                       [:p (str "Like google translate to copy/paste a sentence using Japanese "
                                                "characters. IME support will be added in the future")]]
                                      (map explain without-punctuation)))}))

(defroutes routes
  (GET "/" req (index req))
  (GET "/analyze" req (analyze req))
  (route/not-found "Page not found"))

(defn handler
  [request]
  (let [with-middleware
        (-> routes
            ;;(ring.middleware.reload/wrap-reload)
            (ring.middleware.params/wrap-params))]
    (with-middleware request)))
