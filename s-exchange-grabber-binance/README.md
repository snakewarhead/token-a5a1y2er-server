# s-exchange-grabber

## summary

A tool which grab exchange data.

- initial action

- dynamic action
  - receive action msg from mq

## run

```cmd
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"type\":2, \"actions\":[{\"name\":\"OrderBook\", \"symbols\":[\"ZECUSDT\",\"OMGUSDT\"]}]}" --threadPoolSize=1
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"type\":2, \"actions\":[{\"name\":\"AggTrade\", \"symbols\":[\"BTCUSDT\"]}]}" --threadPoolSize=1
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"type\":2, \"actions\":[{\"name\":\"ForceOrder\", \"symbols\":[\"BTCUSDT\"]}]}" --threadPoolSize=1
```
