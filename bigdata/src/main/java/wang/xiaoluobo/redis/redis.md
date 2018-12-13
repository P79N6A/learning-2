# redis version 4.0.9

## 单点部署
```Bash
$ wget http://download.redis.io/releases/redis-4.0.9.tar.gz
$ tar xzf redis-4.0.9.tar.gz
$ cd redis-4.0.9
$ make

# 启动redis
$ src/redis-server

$ src/redis-cli
redis> set foo bar
OK
redis> get foo
"bar"
```

