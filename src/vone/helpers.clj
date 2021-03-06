(ns vone.helpers
   (:require [clj-time.format :as format]
             [clj-time.core :as time]))

(defn transpose
  [in]
  (let [c (count in)]
    (partition c (if (<= c 1)
                   in
                   (apply interleave in)))))

; http://biesnecker.com/infinite-lazy-seqs-clojure-joda-time.html
;; basic functions to increment or decrement a date
(defn inc-date
  [#^org.joda.time.DateTime ds]
  (.plusDays ds 1))
(defn dec-date
  [#^org.joda.time.DateTime ds]
  (.minusDays ds 1))
;; generate infinite streams of LocalDate objects starting with start-ds
(defn inc-date-stream
  [#^org.joda.time.DateTime start-ds]
  (iterate inc-date start-ds))
(defn dec-date-stream
  [#^org.joda.time.DateTime start-ds]
  (iterate dec-date start-ds))
(defn weekend?
  [#^org.joda.time.DateTime ds]
  (> (.get (.dayOfWeek ds)) 5))

(defn parse-date
  [date]
  (try
    (format/parse (format/formatter "yyyy-MM-dd") date)
    (catch Exception e
      nil)))

(defn parse-date-full
  [date]
  (try
    (format/parse (format/formatter "yyyy-MM-dd'T'HH:mm:ss.SSS") date)
    (catch Exception e
      nil)))

;(parse-date "2013-05-28T10:49:28.943")

(defn parse-double
  [s]
  (try
    (Double/parseDouble s)
    (catch Exception e
      nil)))

(defn tostr-date
  [date]
  (format/unparse (format/formatter "yyyy-MM-dd'T23:59:59'") date))

(defn tostr-ds-date
  "converts a joda time into a javascript zero based month date"
  [date]
  (str "Date(" (time/year date) "," (dec (time/month date)) "," (time/day date) ")"))

(defn readable-date
  [date]
  (format/unparse (if (= (time/year date) (time/year (time/now)))
                    (format/formatter "MMM dd")
                    (format/formatter "MMM dd yyyy"))
                    date))

(defn basic-date
  [date]
  (format/unparse (format/formatters :basic-date) date))





