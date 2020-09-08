(ns comparativeclojure.sales
  (:require [clojure.string :as str]))

(def customerVector [])
(def productVector [])
(def salesVector [])
(def customerMap (sorted-map))
(def productMap (sorted-map))
(def productPriceMap (sorted-map))
(def salesMap (sorted-map))
(def totalAmount)
(def totalCount)
(def sizeCustomerMap)
(def sizeProductMap)
(def sizeSalesMap)

; print the customer table.
(defn printCustomerTable [index sizeCustomerMap]
  (if (>= sizeCustomerMap 1)
    (if (contains? customerMap index)
      (do (def record (str index "|" (get customerMap index)))
          (def innerList (str/split record #"\|"))
          (def recordNew (str "Customer ID: " (get innerList 0) " , Name: " (get innerList 1) " , Address: " (get innerList 2)
                              " , Phone: " (get innerList 3)))

          (println recordNew)
          (printCustomerTable (inc index) (dec sizeCustomerMap)))
      (printCustomerTable (inc index) sizeCustomerMap))))

; print the product table.
(defn printProductTable [index sizeProductMap]
  (if (>= sizeProductMap 1)
    (if (contains? productMap index)
      (do (def record (str index "|" (get productMap index)))
          (def innerList (str/split record #"\|"))
          (def recordNew (str "Product ID: " (get innerList 0) " , Item: " (get innerList 1) " , Cost: " (get innerList 2)))
          (println recordNew)
          (printProductTable (inc index) (dec sizeProductMap)))
      (printProductTable (inc index) sizeProductMap))))

; print the sales table.
(defn printSalesTable [index sizeSalesMap]
  (if (>= sizeSalesMap 1)
    (if (contains? salesMap index)
      (do (def record (str index "|" (get salesMap index)))
          (def innerList (str/split record #"\|"))
          (def recordNew (str "Sales ID: " (get innerList 0) " , Customer Name: " (get innerList 1) " , Item: " (get innerList 2)
                              " , Count: " (get innerList 3)))
          (println recordNew)
          (printSalesTable (inc index) (dec sizeSalesMap)))
      (printSalesTable (inc index) sizeSalesMap))))

; create hash map for customer table.
(defn createCustomerMap [index]
  (if (< index (count customerVector))
    (do (def customerLine (get customerVector index))
        (def innerList (str/split customerLine #"\|"))
        (def customerMap (assoc customerMap (Integer/parseInt (re-find #"\A-?\d+" (get innerList 0)))
                                            (str (get innerList 1) "|" (get innerList 2) "|" (get innerList 3))))
        (createCustomerMap (inc index)))))

; create hash map for product table.
(defn createProductMap [index]
  (if (< index (count productVector))
    (do (def productLine (get productVector index))
        (def innerList (str/split productLine #"\|"))
        (def productMap (assoc productMap (Integer/parseInt (re-find #"\A-?\d+" (get innerList 0)))
                                          (str (get innerList 1) "|" (get innerList 2))))
        (def productPriceMap (assoc productPriceMap (get innerList 1) (Float/parseFloat (re-find #"^-?\d+\.?\d*$"
                                                                                                 (get innerList 2)))))
        (createProductMap (inc index)))))

; create hash map for sales table.
(defn createSalesMap [index]
  (if (< index (count salesVector))
    (do (def salesLine (get salesVector index))
        (def innerList (str/split salesLine #"\|"))
        (def key1 (Integer/parseInt (re-find #"\A-?\d+" (get innerList 0))))
        (def customerName (get (str/split (get customerMap (Integer/parseInt (re-find #"\A-?\d+"
                                                                                      (get innerList 1)))) #"\|") 0))
        (def productName (get (str/split (get productMap (Integer/parseInt (re-find #"\A-?\d+"
                                                                                    (get innerList 2)))) #"\|") 0))
        (def productQuantity (Integer/parseInt (re-find #"\A-?\d+" (get innerList 3))))
        (def salesMap (assoc salesMap key1 (str customerName "|" productName "|" productQuantity)))
        (createSalesMap (inc index)))))

; load customer table in form of vector.
(defn loadCustomerTable []
  (def customerData (slurp "cust.txt"))
  (def customerVector (str/split-lines customerData))
  (createCustomerMap 0))

; load product table in form of vector.
(defn loadProductTable []
  (def productData (slurp "prod.txt"))
  (def productVector (str/split-lines productData))
  (createProductMap 0))

; load sales table in form of vector.
(defn loadSalesTable []
  (def salesData (slurp "sales.txt"))
  (def salesVector (str/split-lines salesData))
  (createSalesMap 0))

; calculate total sales for particular customer.
(defn totalSalesCalculate [index customerName sizeSalesMap]
  (if (>= sizeSalesMap 1)
    (if (contains? salesMap index)
      (do (def record (get salesMap index))
          (def innerList (str/split record #"\|"))
          (def compareDigit (compare customerName (get innerList 0)))
          (if (= compareDigit 0)
            (do (def productType (get innerList 1))
                (def productQuant (Integer/parseInt (re-find #"\A-?\d+" (get innerList 2))))
                (if (contains? productPriceMap productType)
                  (do (def totalAmount (+ totalAmount (* productQuant (get productPriceMap productType))))
                      (totalSalesCalculate (inc index) customerName (dec sizeSalesMap)))))
            (totalSalesCalculate (inc index) customerName (dec sizeSalesMap))))
      (totalSalesCalculate (inc index) customerName sizeSalesMap))))

; take name of the customer for total sales calculation.
(defn totalSalesPreWork []
  (def totalAmount 0)
  (def sizeSalesMap (count salesMap))
  (println "Enter the name of the customer: ")
  (def customerName (read-line))
  (totalSalesCalculate 1 customerName sizeSalesMap)

  (if (= totalAmount 0)
    (println (str "Total Sales : $" totalAmount))
    (println (str customerName ": $" (format "%.2f" totalAmount)))))

;calculate total number of count for particular product.
(defn totalProductCountCalculate [index productName sizeSalesMap]
  (if (>= sizeSalesMap 1)
    (if (contains? salesMap index)
      (do (def record (get salesMap index))
          (def innerList (str/split record #"\|"))
          (def compareDigit (compare productName (get innerList 1)))
          (if (= compareDigit 0)
            (def totalCount (+ totalCount (Integer/parseInt (re-find #"\A-?\d+" (get innerList 2))))))
          (totalProductCountCalculate (inc index) productName (dec sizeSalesMap)))
      (totalProductCountCalculate (inc index) productName sizeSalesMap))))

; take name of the item for total count calculation.
(defn totalProductCountPreWork []
  (def totalCount 0)
  (def sizeSalesMap (count salesMap))
  (println "Enter the name of the product: ")
  (def productName (read-line))
  (totalProductCountCalculate 1 productName sizeSalesMap)

  (if (= totalCount 0)
    (println (str "Total Count : " totalCount))
    (println (str productName ": " totalCount))))

; switch case for option method calling according to option.
(defn switchMenu [option]
  (def sizeCustomerMap (count customerMap))
  (def sizeProductMap (count productMap))
  (def sizeSalesMap (count salesMap))
  (case option 1 (printCustomerTable 1 sizeCustomerMap)
               2 (printProductTable 1 sizeProductMap)
               3 (printSalesTable 1 sizeSalesMap)
               4 (totalSalesPreWork)
               5 (totalProductCountPreWork)
               ))

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
  (try
    (def chosenOptionText (read-line))
    (def option (Integer/parseInt (re-find #"\A-?\d+" chosenOptionText)))
    (if (= option 6) (do (println "THANK YOU.....GOOD BYE.....")
                         (System/exit 0)))
    (if (or (< option 1) (> option 6)) (do (println "ERROR: Please choose option between 1 to 6")
                                           (printMenu)))
    (switchMenu option)
    (printMenu)
    (catch NumberFormatException e (println "ERROR: Please choose option between 1 to 6")
                                   (printMenu)))
  )

; main method to call load and print function.
(defn main []
  (loadCustomerTable)
  (loadProductTable)
  (loadSalesTable)
  (printMenu))

(main)

