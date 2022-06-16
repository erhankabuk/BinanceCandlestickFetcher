##### https://developers.binance.com/docs/binance-trading-api/spot#general-api-information
### General API Information
* The base endpoint is: https://api.binance.com
* If there are performance issues with the endpoint above, these API clusters are also available:
```
https://api1.binance.com
https://api2.binance.com
https://api3.binance.com
```
* All endpoints return either a JSON object or array.
* Data is returned in ascending order. Oldest first, newest last.
* All time and timestamp related fields are in milliseconds.

### Kline/Candlestick chart intervals:
```
m -> minutes; h -> hours; d -> days; w -> weeks; M -> months

1m
3m
5m
15m
30m
1h
2h
4h
6h
8h
12h
1d
3d
1w
1M
```


---

##### https://developers.binance.com/docs/binance-trading-api/spot#klinecandlestick-data
### Kline/CandleStick Data
* Response
```
[
  [
    1499040000000,      // Open time
    "0.01634790",       // Open
    "0.80000000",       // High
    "0.01575800",       // Low
    "0.01577100",       // Close
    "148976.11427815",  // Volume
    1499644799999,      // Close time
    "2434.19055334",    // Quote asset volume
    308,                // Number of trades
    "1756.87402397",    // Taker buy base asset volume
    "28.46694368",      // Taker buy quote asset volume
    "17928899.62484339" // Ignore.
  ]
]
```
* https://api.binance.com/api/v3/klines?symbol=EOSUSDT&interval=1d
  https://api.binance.com/sapi/v1/lending/project/position/list
---

##### https://developers.binance.com/docs/binance-trading-api/spot#24hr-ticker-price-change-statistics
### 24hr Ticker Price Change Statistics
* Response
```
{
  "symbol": "BNBBTC",
  "priceChange": "-94.99999800",
  "priceChangePercent": "-95.960",
  "weightedAvgPrice": "0.29628482",
  "prevClosePrice": "0.10002000",
  "lastPrice": "4.00000200",
  "lastQty": "200.00000000",
  "bidPrice": "4.00000000",
  "bidQty": "100.00000000",
  "askPrice": "4.00000200",
  "askQty": "100.00000000",
  "openPrice": "99.00000000",
  "highPrice": "100.00000000",
  "lowPrice": "0.10000000",
  "volume": "8913.30000000",
  "quoteVolume": "15.30000000",
  "openTime": 1499783499040,
  "closeTime": 1499869899040,
  "firstId": 28385,   // First tradeId
  "lastId": 28460,    // Last tradeId
  "count": 76         // Trade count
}
```
* GET /api/v3/ticker/24hr
* 24 hour rolling window price change statistics. Careful when accessing this with no symbol.
* https://api.binance.com/api/v3/ticker/24hr?symbol=EOSUSDT

---

