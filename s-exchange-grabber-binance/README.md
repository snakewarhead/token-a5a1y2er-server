# s-exchange-grabber

## summary

A tool which grab exchange data.

- initial action

- dynamic action
  - receive action msg from mq

## run

```cmd
mkdir -p logs/logName_IS_UNDEFINED

java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"All\", \"symbols\":[\"BTCUSDT\"]}}" --threadPoolSize=4
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"OrderBook\", \"symbols\":[\"BTCUSDT\"]}}" --threadPoolSize=4
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"AggTrade\", \"symbols\":[\"BTCUSDT\"]}}" --threadPoolSize=4
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"ForceOrder\", \"symbols\":[\"BTCUSDT\"]}}" --threadPoolSize=4
java -jar s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1,\"tradeType\":2,\"action\":{\"name\":\"TakerLongShortRatio\",\"symbols\":[\"BTCUSDT\"],\"params\":[\"5m\",\"15m\"]}}" --threadPoolSize=4
pm2 start -n grabber-kline-5m 'java -jar s-exchange-grabber-binance/target/s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"KLine\", \"symbols\":[], \"params\":[\"5m\"]}}" --threadPoolSize=2'
pm2 start -n grabber-coin-info-raw 'java -jar s-exchange-grabber-binance/target/s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"CoinInfoRaw\", \"symbols\":[], \"params\":[]}}" --threadPoolSize=1'

java -jar s-exchange-grabber-binance/target/s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"AllTicker\", \"symbols\":[]}}" --threadPoolSize=1
java -jar s-exchange-grabber-binance/target/s-exchange-grabber-binance-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":1, \"action\":{\"name\":\"AllTicker\", \"symbols\":[]}}" --threadPoolSize=1
```
