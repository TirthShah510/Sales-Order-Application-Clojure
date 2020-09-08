(ns comparativeclojure.retail
  (:require [clojure.string :as str]))

(defn createCustomerMap [index customerVector customerMap]
  (def customerLine (get customerVector index))
  (def innerList (str/split customerLine #"\|"))
  (def customerMap (assoc customerMap (Integer/parseInt (re-find #"\A-?\d+" (get innerList 0)))
                                      (str (get innerList 1) "|" (get innerList 2) "|" (get innerList 3)))))

(def customerMap (sorted-map))

(defn loadCustomerTable []
  (def customerData (slurp "cust.txt"))
  (def customerVector (str/split-lines customerData))
  (map (fn [x] (createCustomerMap x customerVector customerMap)) (range 0 (count customerVector))))

(defn createProductMap [index productVector productMap productPriceMap]
  (def productLine (get productVector index))
  (def innerList (str/split productLine #"\|"))
  (def productMap (assoc productMap (Integer/parseInt (re-find #"\A-?\d+" (get innerList 0)))
                                      (str (get innerList 1) "|" (get innerList 2))))
  (def productPriceMap (assoc productPriceMap (get innerList 1) (Float/parseFloat (re-find #"^-?\d+\.?\d*$"
                                                                                           (get innerList 2))))))

(def productMap (sorted-map))
(def productPriceMap (sorted-map))

(defn loadProductTable []
  (def productData (slurp "prod.txt"))
  (def productVector (str/split-lines productData))
  (map (fn [x] (createProductMap x productVector productMap productPriceMap)) (range 0 (count productVector))))

(defn createSalesMap [index salesVector salesMap]
  (def salesLine (get salesVector index))
  (def innerList (str/split salesLine #"\|"))
  (def key1 (Integer/parseInt (re-find #"\A-?\d+" (get innerList 0))))
  (def customerName (get (str/split (get customerMap (Integer/parseInt (re-find #"\A-?\d+"
                                                                                (get innerList 1)))) #"\|") 0))
  (def productName (get (str/split (get productMap (Integer/parseInt (re-find #"\A-?\d+"
                                                                              (get innerList 2)))) #"\|") 0))
  (def productQuantity (Integer/parseInt (re-find #"\A-?\d+" (get innerList 3))))
  (def salesMap (assoc salesMap key1 (str customerName "|" productName "|" productQuantity))))

(def salesMap (sorted-map))

(defn loadSalesTable []
  (def salesData (slurp "sales.txt"))
  (def salesVector (str/split-lines salesData))
  (map (fn [x] (createSalesMap x salesVector salesMap)) (range 0 (count salesVector))))

(def record "")

; print the customer table.
(defn printCustomerTable []
  (loop [i 1]
    (when (<= i (count customerMap))
      (def record (str i "|" (get customerMap i)))
      (println record)
      (recur (inc i)))))

; print the product table.
(defn printProductTable []
  (loop [i 1]
    (when (<= i (count productMap))
      (def record (str i "|" (get productMap i)))
      (println record)
      (recur (inc i)))))

; print the sales table.
(defn printSalesTable []
  (loop [i 1]
    (when (<= i (count salesMap))
      (def record (str i "|" (get salesMap i)))
      (println record)
      (recur (inc i)))))

; calculate total sales for particular customer
(defn totalSales []
  (def totalAmount 0)
  (println "Enter the name of the customer: ")
  (def customerName (read-line))
  (loop [i 1]
    (when (<= i (count salesMap))
      (def record (get salesMap i))
      (def innerList (str/split record #"\|"))
      (def compareDigit (compare customerName (get innerList 0)))
      (if (= compareDigit 0)
        (do (def productType (get innerList 1))
            (def productQuant (Integer/parseInt (re-find #"\A-?\d+" (get innerList 2))))
            (if (contains? productPriceMap productType)
              (def totalAmount (+ totalAmount (* productQuant (get productPriceMap productType)))))))
      (recur (inc i))))

  (if (= totalAmount 0)
    (println (str "Total Sales : $" totalAmount))
    (println (str customerName ": $" (format "%.2f" totalAmount)))))

;calculate total number of count for particular product.
(defn totalProductCount []
  (def totalCount 0)
  (println "Enter the name of the product: ")
  (def productName (read-line))
  (loop [i 1]
    (when (<= i (count salesMap))
      (def record (get salesMap i))
      (def innerList (str/split record #"\|"))
      (def compareDigit (compare productName (get innerList 1)))
      (if (= compareDigit 0)
        (def totalCount (+ totalCount (Integer/parseInt (re-find #"\A-?\d+" (get innerList 2))))))
      (recur (inc i))))

  (if (= totalCount 0)
    (println (str "Total Count : " totalCount))
    (println (str productName ": " totalCount))))

; switch case for option method calling according to option.
(defn switchMenu [option]
  (case option 1 (printCustomerTable)
               2 (printProductTable)
               3 (printSalesTable)
               4 (totalSales)
               5 (totalProductCount)))

; main menu for the selection.
(defn printMenu []
  (println "------------------")
  (println "*** Sales Menu ***")
  (println "------------------")
  (println)
  (println "1. Display Customer Table")
  (println "2. Display Product Table")
  (println "3. Display Sales Table")
  (println "4. Total Sales for Customer")
  (println "5. Total Count for Product")
  (println "6. Exit")
  (println)
  (println "Enter an option?")
  (flush)
  (def chosenOptionText (read-line))
  (def option (Integer/parseInt (re-find #"\A-?\d+" chosenOptionText)))
  (if (= option 6) (do (println "THANK YOU.....GOOD BYE.....")
                       (System/exit 0)))
  (if (or (< option 1) (> option 6)) (do (println "Please choose option between 1 to 6")
                                         (printMenu)))
  (switchMenu option)
  (println "------------------")
  (printMenu))


(defn main []
  (loadCustomerTable)
  (loadProductTable)
  (loadSalesTable)
  (printMenu))

(main)
