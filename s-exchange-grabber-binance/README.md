# s-exchange-grabber

## summary

A tool which grab exchange data.

- initial action

- dynamic action
  - receive action msg from mq

## run

```cmd
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"OrderBook\", \"symbols\":[\"ZECUSDT\",\"OMGUSDT\"]}}" --threadPoolSize=4
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"AggTrade\", \"symbols\":[\"BTCUSDT\"]}}" --threadPoolSize=4
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"ForceOrder\", \"symbols\":[\"BTCUSDT\"]}}" --threadPoolSize=4
```
