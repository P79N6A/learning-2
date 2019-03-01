## elasticsearch-5.3.0
### elasticsearch 单点
#### 1. 配置elasticsearch
```Bash
[root@bigdata01 ~]# groupadd elastic
[root@bigdata01 ~]# useradd -d /home/elastic -g elastic -m elastic
[root@bigdata01 opt]# chown -R elastic:elastic elasticsearch-5.3.0

[root@bigdata01 config]# mkdir -p /mnt/data/elastic
[root@bigdata01 config]# chown -R elastic:elastic /mnt/data/elastic
[root@bigdata01 config]# mkdir -p /mnt/log/elastic
[root@bigdata01 config]# chown -R elastic:elastic /mnt/log/elastic

[elastic@bigdata01 config]$ vim elasticsearch.yml
cluster.name: test
node.name: node-1
path.data: /mnt/data/elastic
path.logs: /mnt/log/elastic
network.host: bigdata01
bootstrap.memory_lock: false
bootstrap.system_call_filter: false

[elastic@bigdata01 config]$ nohup ./bin/elasticsearch &
```

#### 2. 问题处理
```Bash
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
```Bash
$ yum -y install git npm  
$ git clone git://github.com/mobz/elasticsearch-head.git
$ npm install -g grunt-cli
$ npm run start
或者
$ cd elasticsearch-head/node_modules/grunt/bin
$ ./grunt server
```

#### 5. head web页面
![elasticsearch01](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/elasticsearch/images/elasticsearch01.png)    

![elasticsearch02](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/elasticsearch/images/elasticsearch02.png)    

![elasticsearch03](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/elasticsearch/images/elasticsearch03.png)    

![elasticsearch04](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/elasticsearch/images/elasticsearch04.png)    


#### mac安装elasticsearch-head
```Bash
$ brew install node
$ git clone git://github.com/mobz/elasticsearch-head.git
$ cd elasticsearch-head
# 端口9100
$ vim Gruntfile.js
# 端口9200
$ vim _site/app.js
$ npm install
$ npm install grunt --save
$ ./node_modules/grunt/bin/grunt server &
$ vim elasticsearch.yml
http.cors.enabled: true
http.cors.allow-origin: "*"
```

[web ui](http://localhost:9100/)

### elasticsearch 集群部署
https://blog.csdn.net/iloveyin/article/details/48312767



[root@iss-half-ali-f-app-001-18-13 ~]# vim /etc/sysctl.conf
vm.max_map_count = 262144
sysctl -p

[root@iss-half-ali-f-app-001-18-13 ~]# vim /etc/security/limits.conf
hadoop soft nofile 65536
hadoop hard nofile 65536

[root@iss-half-ali-f-app-001-18-13 ~]# vim /etc/security/limits.d/90-nproc.conf
*          soft    nproc     2048


[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ mkdir -p /mnt/data/elastic /mnt/log/elastic

[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ vim config/elasticsearch.yml

[hadoop@iss-half-ali-f-app-003-18-15 elasticsearch-5.3.0]$ nohup ./bin/elasticsearch &
[hadoop@iss-half-ali-f-app-003-18-15 elasticsearch-5.3.0]$ nohup ./bin/elasticsearch &
[hadoop@iss-half-ali-f-app-003-18-15 elasticsearch-5.3.0]$ nohup ./bin/elasticsearch &

[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl http://172.16.18.13:9200
{
  "name" : "node-1",
  "cluster_name" : "es_cluster",
  "cluster_uuid" : "aGi9qM1DSaqmhE0IHVLfOA",
  "version" : {
    "number" : "5.3.0",
    "build_hash" : "3adb13b",
    "build_date" : "2017-03-23T03:31:50.652Z",
    "build_snapshot" : false,
    "lucene_version" : "6.4.1"
  },
  "tagline" : "You Know, for Search"
}


[hadoop@iss-half-ali-f-app-002-18-14 elasticsearch-5.3.0]$ curl http://172.16.18.14:9200
{
  "name" : "node-2",
  "cluster_name" : "es_cluster",
  "cluster_uuid" : "aGi9qM1DSaqmhE0IHVLfOA",
  "version" : {
    "number" : "5.3.0",
    "build_hash" : "3adb13b",
    "build_date" : "2017-03-23T03:31:50.652Z",
    "build_snapshot" : false,
    "lucene_version" : "6.4.1"
  },
  "tagline" : "You Know, for Search"
}


[hadoop@iss-half-ali-f-app-003-18-15 elasticsearch-5.3.0]$ curl http://172.16.18.15:9200
{
  "name" : "node-3",
  "cluster_name" : "es_cluster",
  "cluster_uuid" : "aGi9qM1DSaqmhE0IHVLfOA",
  "version" : {
    "number" : "5.3.0",
    "build_hash" : "3adb13b",
    "build_date" : "2017-03-23T03:31:50.652Z",
    "build_snapshot" : false,
    "lucene_version" : "6.4.1"
  },
  "tagline" : "You Know, for Search"
}

https://blog.51cto.com/msiyuetian/1926325


1.查看集群的健康状态。
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl http://172.16.18.13:9200/_cat/health?v
epoch      timestamp cluster    status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1551431015 17:03:35  es_cluster green           3         3      0   0    0    0        0             0                  -                100.0%

[hadoop@iss-half-ali-f-app-002-18-14 elasticsearch-5.3.0]$ curl http://172.16.18.14:9200/_cat/health?v
epoch      timestamp cluster    status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1551431046 17:04:06  es_cluster green           3         3      0   0    0    0        0             0                  -                100.0%



[hadoop@iss-half-ali-f-app-003-18-15 elasticsearch-5.3.0]$ curl http://172.16.18.15:9200/_cat/health?v
epoch      timestamp cluster    status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1551431052 17:04:12  es_cluster green           3         3      0   0    0    0        0             0                  -                100.0%


2.查看集群的索引数。

[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl http://172.16.18.13:9200/_cat/indices?v
health status index uuid pri rep docs.count docs.deleted store.size pri.store.size
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$


https://blog.csdn.net/deliciousion/article/details/78076882


3.查看集群所在磁盘的分配状况

[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl http://172.16.18.13:9200/_cat/allocation?v
shards disk.indices disk.used disk.avail disk.total disk.percent host         ip           node
     0           0b       8gb     90.2gb     98.3gb            8 172.16.18.15 172.16.18.15 node-3
     0           0b     7.9gb     90.3gb     98.3gb            8 172.16.18.13 172.16.18.13 node-1
     0           0b       8gb     90.2gb     98.3gb            8 172.16.18.14 172.16.18.14 node-2


[hadoop@iss-half-ali-f-app-002-18-14 elasticsearch-5.3.0]$ curl http://172.16.18.14:9200/_cat/allocation?v
shards disk.indices disk.used disk.avail disk.total disk.percent host         ip           node
     0           0b     7.9gb     90.3gb     98.3gb            8 172.16.18.13 172.16.18.13 node-1
     0           0b       8gb     90.2gb     98.3gb            8 172.16.18.15 172.16.18.15 node-3
     0           0b       8gb     90.2gb     98.3gb            8 172.16.18.14 172.16.18.14 node-2
     
     
     
[hadoop@iss-half-ali-f-app-003-18-15 elasticsearch-5.3.0]$ curl http://172.16.18.15:9200/_cat/allocation?v
shards disk.indices disk.used disk.avail disk.total disk.percent host         ip           node
     0           0b       8gb     90.2gb     98.3gb            8 172.16.18.15 172.16.18.15 node-3
     0           0b       8gb     90.2gb     98.3gb            8 172.16.18.14 172.16.18.14 node-2
     0           0b     7.9gb     90.3gb     98.3gb            8 172.16.18.13 172.16.18.13 node-1



查看集群信息目录
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl http://172.16.18.13:9200/_cat/
=^.^=
/_cat/allocation
/_cat/shards
/_cat/shards/{index}
/_cat/master
/_cat/nodes
/_cat/tasks
/_cat/indices
/_cat/indices/{index}
/_cat/segments
/_cat/segments/{index}
/_cat/count
/_cat/count/{index}
/_cat/recovery
/_cat/recovery/{index}
/_cat/health
/_cat/pending_tasks
/_cat/aliases
/_cat/aliases/{alias}
/_cat/thread_pool
/_cat/thread_pool/{thread_pools}
/_cat/plugins
/_cat/fielddata
/_cat/fielddata/{fields}
/_cat/nodeattrs
/_cat/repositories
/_cat/snapshots/{repository}
/_cat/templates


[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl -XGET http://172.16.18.13:9200/_cluster/health?pretty
{
  "cluster_name" : "es_cluster",
  "status" : "green",
  "timed_out" : false,
  "number_of_nodes" : 3,
  "number_of_data_nodes" : 3,
  "active_primary_shards" : 0,
  "active_shards" : 0,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 0,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 100.0
}

查询主节点信息
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl -XGET http://172.16.18.13:9200/_cat/master?v
id                     host         ip           node
AD9UQzMeSU-jm0_xw7czlw 172.16.18.13 172.16.18.13 node-1





查看节点信息
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl -XGET http://172.16.18.13:9200/_cat/shards?v
index shard prirep state docs store ip node


创建索引
curl -XPUT http://172.16.18.13:9200/test -d '
{
    "settings" : {
        "number_of_shards" : 1
    },
    "mappings" : {
        "type1" : {
            "properties" : {
                "field1" : { "type" : "text" }
            }
        }
    }
}'
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl -XPUT http://172.16.18.13:9200/test -d '
> {
>     "settings" : {
>         "number_of_shards" : 1
>     },
>     "mappings" : {
>         "type1" : {
>             "properties" : {
>                 "field1" : { "type" : "text" }
>             }
>         }
>     }
> }'
{"acknowledged":true,"shards_acknowledged":true}




[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl -XGET http://172.16.18.13:9200/_cat/shards?v
index shard prirep state   docs store ip           node
test  0     p      STARTED    0  130b 172.16.18.14 node-2
test  0     r      STARTED    0  130b 172.16.18.13 node-1



172.16.18.13

curl -XGET 172.16.18.13:9200/_cluster/stats?pretty=true
[hadoop@iss-half-ali-f-app-001-18-13 elasticsearch-5.3.0]$ curl -XGET 172.16.18.13:9200/_cluster/stats?pretty=true
{
  "_nodes" : {
    "total" : 3,
    "successful" : 3,
    "failed" : 0
  },
  "cluster_name" : "es_cluster",
  "timestamp" : 1551431873434,
  "status" : "green",
  "indices" : {
    "count" : 1,
    "shards" : {
      "total" : 2,
      "primaries" : 1,
      "replication" : 1.0,
      "index" : {
        "shards" : {
          "min" : 2,
          "max" : 2,
          "avg" : 2.0
        },
        "primaries" : {
          "min" : 1,
          "max" : 1,
          "avg" : 1.0
        },
        "replication" : {
          "min" : 1.0,
          "max" : 1.0,
          "avg" : 1.0
        }
      }
    },
    "docs" : {
      "count" : 0,
      "deleted" : 0
    },
    "store" : {
      "size_in_bytes" : 260,
      "throttle_time_in_millis" : 0
    },
    "fielddata" : {
      "memory_size_in_bytes" : 0,
      "evictions" : 0
    },
    "query_cache" : {
      "memory_size_in_bytes" : 0,
      "total_count" : 0,
      "hit_count" : 0,
      "miss_count" : 0,
      "cache_size" : 0,
      "cache_count" : 0,
      "evictions" : 0
    },
    "completion" : {
      "size_in_bytes" : 0
    },
    "segments" : {
      "count" : 0,
      "memory_in_bytes" : 0,
      "terms_memory_in_bytes" : 0,
      "stored_fields_memory_in_bytes" : 0,
      "term_vectors_memory_in_bytes" : 0,
      "norms_memory_in_bytes" : 0,
      "points_memory_in_bytes" : 0,
      "doc_values_memory_in_bytes" : 0,
      "index_writer_memory_in_bytes" : 0,
      "version_map_memory_in_bytes" : 0,
      "fixed_bit_set_memory_in_bytes" : 0,
      "max_unsafe_auto_id_timestamp" : -1,
      "file_sizes" : { }
    }
  },
  "nodes" : {
    "count" : {
      "total" : 3,
      "data" : 3,
      "coordinating_only" : 0,
      "master" : 3,
      "ingest" : 3
    },
    "versions" : [
      "5.3.0"
    ],
    "os" : {
      "available_processors" : 12,
      "allocated_processors" : 12,
      "names" : [
        {
          "name" : "Linux",
          "count" : 3
        }
      ],
      "mem" : {
        "total_in_bytes" : 25168982016,
        "free_in_bytes" : 7038984192,
        "used_in_bytes" : 18129997824,
        "free_percent" : 28,
        "used_percent" : 72
      }
    },
    "process" : {
      "cpu" : {
        "percent" : 0
      },
      "open_file_descriptors" : {
        "min" : 216,
        "max" : 219,
        "avg" : 217
      }
    },
    "jvm" : {
      "max_uptime_in_millis" : 1554142,
      "versions" : [
        {
          "version" : "1.8.0_112",
          "vm_name" : "Java HotSpot(TM) 64-Bit Server VM",
          "vm_version" : "25.112-b15",
          "vm_vendor" : "Oracle Corporation",
          "count" : 3
        }
      ],
      "mem" : {
        "heap_used_in_bytes" : 801257768,
        "heap_max_in_bytes" : 6337855488
      },
      "threads" : 118
    },
    "fs" : {
      "total_in_bytes" : 316665593856,
      "free_in_bytes" : 307022688256,
      "available_in_bytes" : 290916560896,
      "spins" : "true"
    },
    "plugins" : [ ],
    "network_types" : {
      "transport_types" : {
        "netty4" : 3
      },
      "http_types" : {
        "netty4" : 3
      }
    }
  }
}



