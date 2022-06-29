
### How to Run
* Call request in main method at BinanceCandlestickFetcherApplication.java
* Use currency name as string "EOSUSDT"
* Determine start time for request at "begin" => "2018-05-28T03:00:00"
* Determine interval as minutes => 1440
* Use limit max 1000
* Instance => service.updateData("EOSUSDT", startTime, 1440, 1000); 
* Run project