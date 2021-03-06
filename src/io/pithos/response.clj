(ns io.pithos.response
  (:require [lamina.core :refer [enqueue close]]
            [clojure.tools.logging :refer [debug error]]))

(defn response
  ([]
     {:status 200 :headers {}})
  ([body]
     {:status 200 :headers {} :body body}))

(defn header
  [resp header val]
  (let [strval (if (keyword? val) (name val) (str val))]
    (assoc-in resp [:headers header] strval)))

(defn content-type
  [resp type]
  (header resp "Content-Type" type))

(defn status
  [resp status]
  (assoc resp :status status))

(defn xml-response
  [body]
  (-> body
      response
      (header "Content-Type" "application/xml")))

(defn html-response
  [body]
  (-> (response body)
      (header "Content-Type" "text/html")))

(defn request-id
  [resp {:keys [reqid]}]
  (-> resp
      (header "Server" "Pithos")
      (header "x-amz-id-2" (str reqid))
      (header "x-amz-request-id" (str reqid))))

(defn exception-status
  [resp details]
  (let [{:keys [status-code] :or {status-code 500}} details]
    (-> resp
        (status status-code))))

(defn send!
  [response chan]
  (try
    (enqueue chan response)
    (catch Exception e
      (error e "exception in enqueue"))))
