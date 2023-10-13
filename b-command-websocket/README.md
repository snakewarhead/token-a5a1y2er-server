# b-command-websocket

## summary

Publish data by websocket

## run

```cmd
java -jar b-command-websocket-1.0.0.jar --exchange=binance

wscat -c ws://192.168.1.102:8001/api/v1/exchange/data/binance
{"subscribe":"subscribe", "param": {"exchange":1, "tradeType":2, "action":{"name":"OrderBook", "symbols":["BTCUSDT"]}}}
{"subscribe":"unsubscribe", "param": {"exchange":1, "tradeType":2, "action":{"name":"OrderBook", "symbols":["BTCUSDT"]}}}
```
