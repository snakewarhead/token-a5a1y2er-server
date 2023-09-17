# m-mongodb

## install

```cmd
docker run --name mongodb -d -p 27017:27017 mongodb/mongodb-community-server:latest
docker run --name mongodb -d -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=test -e MONGO_INITDB_ROOT_PASSWORD=Qwe456 mongodb/mongodb-community-server:latest
```

## management

MongoDB Compass

`mongodb://test:Qwe456@192.168.0.200:27017/`

``
