# m-mongodb

## install

```cmd
docker run --name mongodb -d -p 27017:27017 mongodb/mongodb-community-server:latest
docker run --name mongodb -d -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=test -e MONGO_INITDB_ROOT_PASSWORD=Qwe456 mongodb/mongodb-community-server:latest
```

## management

```cmd
docker exec -it mongo mongosh "mongodb://username:password@clusterURL/database"
```

MongoDB Compass

`mongodb://test:Qwe456@192.168.0.200:27017/`

## test

```js
db.exchange_order_book.updateOne(
    {symbol: 'BTCUSDT'},
    {
        $set: {
            'asks.$[ele0].amount': 1,
            'asks.$[ele1].amount': 2,
        }
    },
    {
        upsert: true,
        arrayFilters: [
            {'ele0.price': NumberDecimal('26854.30')},
            {'ele1.price': NumberDecimal('26854.50')},
        ]
    }
)
```
