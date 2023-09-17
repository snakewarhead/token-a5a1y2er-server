# m-rabbitmq

## install

```cmd
docker run -d --hostname test0 --name rabbitmq -p 15672:15672 -p 5672:5672 -e RABBITMQ_DEFAULT_USER=test -e RABBITMQ_DEFAULT_PASS=Qwe456 -e RABBITMQ_DEFAULT_VHOST=token rabbitmq:3-management
```

## management

`http://192.168.1.105:15672`
