## elasticsearch-5.3.0
#### 1. 配置elasticsearch
```sbtshell
[root@bigdata01 ~]# groupadd elastic
[root@bigdata01 ~]# useradd -d /home/elastic -g elastic -m elastic
[root@bigdata01 opt]# chown -R elastic:elastic elasticsearch-5.3.0

[root@bigdata01 config]# mkdir -p /mnt/data/elastic
[root@bigdata01 config]# chown -R elastic:elastic /mnt/data/elastic
[root@bigdata01 config]# mkdir -p /mnt/log/elastic
[root@bigdata01 config]# chown -R elastic:elastic /mnt/log/elastic

[elastic@bigdata01 config]$ vim elasticsearch.yml
path.data: /mnt/data/elastic
path.logs: /mnt/log/elastic
network.host: bigdata01
bootstrap.memory_lock: false
bootstrap.system_call_filter: false
```

#### 2. 问题处理
```sbtshell
# 错误：max file descriptors [65535] for elasticsearch process is too low, increase to at least [65536]
# memory locking requested for elasticsearch process but memory is not locked
# max number of threads [1024] for user [hadoop] is too low, increase to at least [2048]
# max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
[root@bigdata01 mnt]# echo "vm.max_map_count=262144" > /etc/sysctl.conf
[root@bigdata01 mnt]# sysctl -p

[root@bigdata01 mnt]# vim /etc/security/limits.conf 
elastic soft nofile 300000 
elastic hard nofile 300000 
elastic soft nproc 102400 
elastic soft memlock unlimited 
elastic hard memlock unlimited

# 错误：system call filters failed to install; check the logs and fix your configuration or disable system call filters at your own risk
[elastic@bigdata01 config]$ vim elasticsearch.yml
bootstrap.memory_lock: false
bootstrap.system_call_filter: false
```

#### 3. 环境测试
```text
1. postman put 请求，create an index named "customer"
http://es.benz.bingex.com/customer?pretty
{
    "acknowledged": true,
    "shards_acknowledged": true
}

2. postman put 请求，put data into our customer index
http://es.benz.bingex.com/customer/doc/1?pretty
{
    "_index": "customer",
    "_type": "doc",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "created": true
}

3. http://es.benz.bingex.com/customer/doc/1?pretty
{
  "_index" : "customer",
  "_type" : "doc",
  "_id" : "1",
  "_version" : 1,
  "found" : true,
  "_source" : {
    "name" : "John Doe"
  }
}
```

#### 4. head插件
```sbtshell
$ yum install git npm  
$ npm install -g grunt-cli
$ npm run start
或者
$ cd elasticsearch-head/node_modules/grunt/bin
$ ./grunt server
```

#### 5. head web页面
![elasticsearch01](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/elasticsearch/images/elasticsearch01.png)    
![elasticsearch02](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/elasticsearch/images/elasticsearch01.png)    
