# s-exchange-analyser

## summary

A tool which analyse exchange data.

- trade record
  - buy/sell taker volume
    - by time, 5m, 15m, 1h, 4h
    - by price
    
  - grab big taker
  
  
- force order
  - price, volume, time

- over dc
  - 5m 

## run

```cmd
java -jar s-exchange-analyser-1.0.0.jar --threadPoolSize=1
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"TradeVolumeTime\", \"symbols\":[\"BTCUSDT\"],\"params\":[\"5m\"]}}" --threadPoolSize=4
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"VolumeChangeQuick\", \"symbols\":[],\"params\":[\"5m\", \"1m\", \"7\", \"200000\", \"100000\"]}}" --threadPoolSize=1
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"CoinInfoShort\", \"symbols\":[],\"params\":[\"5m\"]}}" --threadPoolSize=2
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"VolumeChangeQuick\", \"symbols\":[],\"params\":[\"5m\", \"1m\", \"7\", \"200000\", \"100000\"]}}" --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"CoinInfoShort\", \"symbols\":[],\"params\":[\"5m\"]}}" --threadPoolSize=4
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"DCOver\", \"symbols\":[],\"params\":[\"5m\", \"96\", \"1\", \"3\"]}}" --threadPoolSize=1
```
