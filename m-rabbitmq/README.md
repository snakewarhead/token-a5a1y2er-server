# m-rabbitmq

## install

```cmd
docker run -d --hostname rabbit-xx --name rabbit-xx -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

## management

`http://192.168.1.105:15672`
