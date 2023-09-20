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

## run

```cmd
java -jar s-exchange-analyser-1.0.0.jar --threadPoolSize=1
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"TradeVolumeTime\", \"symbols\":[\"BTCUSDT\"],\"params\":[\"5m\"]}}" --threadPoolSize=4
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"VolumeChangeQuick\", \"symbols\":[],\"params\":[\"5m\", \"7\"]}}" --threadPoolSize=1
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"CoinInfoShort\", \"symbols\":[],\"params\":[\"5m\"]}}" --threadPoolSize=1
java -jar s-exchange-analyser-1.0.0.jar --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"VolumeChangeQuick\", \"symbols\":[],\"params\":[\"5m\", \"7\"]}}" --params="{\"exchange\":1, \"tradeType\":2, \"action\":{\"name\":\"CoinInfoShort\", \"symbols\":[],\"params\":[\"5m\"]}}" --threadPoolSize=4
```
