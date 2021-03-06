(ns vone.views.welcome
  (:require [noir.session :as session]
            [noir.response :as response])
  (:use [noir.core]
        [hiccup.core]
        [hiccup.form]
        [hiccup.page]
        [hiccup.element]))

(defpage "/" []
  (html5
    [:head
     ; When run on an intranet, IE defaults to compatibility
     ; which does not work for Google Visualization library
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:title "vone"]
     [:link {:rel "icon"
             :href "img/favicon.ico"
             :type "image/x-icon"}]
     [:link {:rel "shortcut"
             :href "img/favicon.ico"
             :type "image/x-icon"}]
     (include-css "/css/bootstrap.min.css")
     (include-css "/css/vone.css")]

    [:body {:authenticate "loginbox"}
     [:header.navbar
      [:div.navbar-inner
       [:a.brand {:href "#/"} [:strong "Vone"]]
       [:ul.nav
        [:li.divider-vertical]
        [:li (link-to "/#/" "Home")]
        [:li.divider-vertical]
        [:li (link-to "/#/retro" "Retrospective")]
        [:li.divider-vertical]
        [:li (link-to "/#/roadmap" "Roadmap")]
        [:li.divider-vertical]
        [:li (link-to "/#/fabel" "Fabel")]
        [:li.divider-vertical]
        [:li (link-to "/#/overall" "Overall")]
        [:li.divider-vertical]]
       [:div.login.ng-cloak.pull-right {:ng-show "!username"}
        (link-to "/#/login" "Login")]
       [:div.logout.ng-cloak.pull-right {:ng-show "username"}
        [:span "{{username}}"]
        (submit-button {:ng-click "logout()"} "logout")]]]

     [:div#loginbox.modal.hide.fade {:tabindex -1
                                     :role "dialog"
                                     :aria-labelledby "Login"
                                     :aria-hidden "true"}
      [:div.modal-header
       [:button.close {:type "button"
                       :data-dismiss "modal"
                       :aria-hidden "true"} "x"]]
      [:div.modal-body
       [:form {:ng-controller "LoginCtrl"
               :ng-submit "submit()"
               :novalidate true}
        [:div (label "username" "Username") (text-field {:ng-model "username"} "username")]
        [:div (label "password" "Password") (password-field {:ng-model "password"} "password")]
        (submit-button "VersionOne Login")]]
      ;TODO: should have a modal-footer with submit, but then no form?
      ]

     [:div#content.ng-view "Loading..."]
     (include-js "/js/jquery-1.8.2.min.js")
     (include-js "https://www.google.com/jsapi")
     (include-js "/js/angular-1.0.1.min.js")
     (include-js "/js/angular-resource-1.0.1.min.js")
     (include-js "/js/http-auth-interceptor.js")
     (include-js "/js/bootstrap.min.js")
     (include-js "/js/charts.js")
     (include-js "/js/controllers.js")
     (include-js "/js/vone.js")]))

(defpage "/login" []
  (html
    [:form {:ng-submit "submit()"
           :novalidate true}
          [:div (label "username" "Username") (text-field {:ng-model "username"} "username")]
          [:div (label "password" "Password") (password-field {:ng-model "password"} "password")]
          (submit-button "VersionOne Login")]))

(defpage [:post "/login"] {:keys [username password]}
  (println "login" username)
  (session/put! :username username)
  (session/put! :password password)
  (response/json username))

(defpage "/logout" []
         (println "logout" (session/get :username))
         (session/clear!)
         (html "Logged out"))

(defpage "/ping" []
         (response/json (session/get :username)))

(defpage "/about" []
         (html
           [:h1 "VersionOne Reporting"]
           [:ul
            [:li (link-to "/#/login" "Login")]
            [:li (link-to "/#/retro" "Retrospective")]]))

(defpage "/burndown" []
  (html
    (form-to [:post "/burndown"]
             [:div (label "sprint" "Sprint") (text-field "sprint")]
             [:div (label "team" "Team") (text-field "team")]
             (submit-button "Get Burndown"))))

(defpage "/cumulative" []
  (html
    (form-to [:post "/cumulative"]
             [:div (label "sprint" "Sprint") (text-field "sprint")]
             [:div (label "team" "Team") (text-field "team")]
             (submit-button "Get Cumulative Flow"))))

(def select-retro
    [:div
     [:select {:ng-model "team"
               :ng-options "key as key for (key, value) in teamSprints"}
      [:option {:value ""} "-- choose team --"]]
     [:select {:ng-model "sprint"
               :ng-options "s for s in sprints"
               :ng-visible "sprints"}
      [:option {:value ""} "-- choose sprint --"]]])

(defpage "/selectRetro" []
  (html
    [:h1 "Retrospective:"]
    select-retro))

(defpage "/retro" []
  (html
    [:h1 "Retrospective: {{sprintBegin}} {{sprintEnd}}"]
    select-retro
    [:hr]
    [:div.report {:ng-visible "team && sprint"}
     [:div {:chart "burndown"}]
     [:div {:chart "burndownComparison"}]
     [:div.break {:chart "cumulative"}]
     [:div {:chart "cumulativePrevious"}]
     [:div.break {:chart "velocity"}]
     [:div {:chart "estimates"}]
     [:div {:chart "failedReview"}]
     [:div {:chart "churnComparison"}]
     [:div {:chart "participants"}]
     [:div.break {:chart "stories"}]
     [:div {:chart "defects"}]
     [:div {:chart "testSets"}]
     [:div {:chart "splits"}]
     [:div {:chart "churnStories"}]
     [:div.break {:chart "customers"}]
     ;TODO:
     ;[:div "Epics"]
     [:div {:chart "customersNext"}]
     ;TODO: why does this have to be unsafe?
     [:div.break {:ng-bind-html-unsafe "feedback"}]]))

(defpage "/roadmap" []
  (html
    [:h1 "Roadmap"]
    [:label.checkbox (check-box {:ng-model "showProject"} "project") "Project"]
    [:label.checkbox (check-box {:ng-model "showCustomer"} "customer") "Customer"]
    [:label.checkbox (check-box {:ng-model "showTeam"} "team") "Team"]
    (link-to "/csv/roadmap" "csv")
    [:div {:roadmap true}]))

(defpage "/fabel" []
  (html
    [:h1 "Fabel"]
    (link-to "/csv/fabel" "csv")
    [:div {:fabel true}]))

(defpage "/overall" []
  (html
    [:h1 "Overall"]
    (link-to "/csv/overall" "csv")
    [:div {:allocation true}]
    [:div {:churn true}]
    [:div {:quality true}]))

(defpage "/members" []
  (html
    [:h1 "Members"]
    [:ul
     [:li {:ng-repeat "member in members"}
      [:a {:href "/#/member/{{member[0]}}"} "{{member[0]}} {{member[1]}}"]]]))

(defpage "/member" []
  (html
    [:h1 "{{member}}"]
    [:div {:chart "workitems"}]))

(defpage "/rankings" []
  (html
    [:h1 "Rankings"]
    [:ul
     [:li {:ng-repeat "m in members"}
      [:a {:href "/#/member/{{m.name}}"} "{{m.name}}"]
      "score:{{m.score}} | points:{{m.points}} | role:{{m.role}} | teir:{{m.tier}} | team:{{m.team}}"]]))

(defpage "" []
  (response/redirect "/vone/"))

