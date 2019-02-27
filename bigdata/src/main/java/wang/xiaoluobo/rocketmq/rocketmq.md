# [rocketmq version 4.4.0](http://rocketmq.apache.org/docs/quick-start/)

## rocketmq三种配置模式
1. [2m-2s-async(主从异步)](./config/m1)
2. [2m-2s-sync(主从同步)](./config/m2)
3. [2m-noslave(仅master)](./config/m3)


## rocketmq 部署
### 一、 环境配置
1. 配置环境变量
    ```bash
    $ vim .bash_profile 
    ROCKETMQ_HOME=/mnt/opt/rocketmq-4.4.0
    PATH=$PATH:$ROCKETMQ_HOME/bin
    export ROCKETMQ_HOME PATH
    
    $ source .bash_profile
    ```
2. 创建目录，详见配置文件
3. 修改rocketmq log目录
```bash
$ cd /mnt/opt/rocketmq-4.4.0/conf && sed -i 's#${user.home}#/mnt/log/rocketmq#g' *.xml
```
4. 修改 rocketmq jvm 参数
```bash
$ vim bin/runbroker.sh
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512"
```

### 二、 rocketmq 集群部署
#### 启动 mqnamesrv
```bash
[hadoop@hadoop02 rocketmq-4.4.0]$ nohup ./bin/mqnamesrv &
[hadoop@hadoop03 rocketmq-4.4.0]$ nohup ./bin/mqnamesrv &
```

#### 修改 broker 配置
1. [2m-2s-async](./config/m1)
    ```bash
    # 创建目录
    [hadoop@hadoop02 rocketmq-4.4.0]$ mkdir -p /mnt/data/rocketmq/store/broker-a /mnt/data/rocketmq/store/broker-a/nsumequeue /mnt/data/rocketmq/store/broker-a/commitlog /mnt/data/rocketmq/store/broker-a/index /mnt/data/rocketmq/logs /mnt/data/rocketmq/store/broker-b-s /mnt/data/rocketmq/store/broker-b-s/nsumequeue /mnt/data/rocketmq/store/broker-b-s/commitlog /mnt/data/rocketmq/store/broker-b-s/index
    [hadoop@hadoop03 rocketmq-4.4.0]$ mkdir -p /mnt/data/rocketmq/store/broker-a-s /mnt/data/rocketmq/store/broker-a-s/nsumequeue /mnt/data/rocketmq/store/broker-a-s/commitlog /mnt/data/rocketmq/store/broker-a-s/index /mnt/data/rocketmq/logs /mnt/data/rocketmq/store/broker-b /mnt/data/rocketmq/store/broker-b/nsumequeue /mnt/data/rocketmq/store/broker-b/commitlog /mnt/data/rocketmq/store/broker-b/index
    
 
    # 配置文件
    [hadoop@hadoop02 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-a.properties
    [hadoop@hadoop03 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-b.properties
    
    [hadoop@hadoop02 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-b-s.properties
    [hadoop@hadoop03 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-a-s.properties
    
 
    # 先启动双主 master，再启动双 slave
    [hadoop@hadoop02 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-a.properties &
    [hadoop@hadoop03 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-b.properties &
    
    [hadoop@hadoop02 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-b-s.properties &
    [hadoop@hadoop03 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-a-s.properties &
    
 
    # 查看集群信息
    [hadoop@hadoop02 rocketmq-4.4.0]$ ./bin/mqadmin clusterlist -n 172.16.18.14:9876
    #Cluster Name     #Broker Name            #BID  #Addr                  #Version                #InTPS(LOAD)       #OutTPS(LOAD) #PCWait(ms) #Hour #SPACE
    rocketmq-cluster  broker-a                0     172.16.18.14:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
    rocketmq-cluster  broker-a                1     172.16.18.15:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
    rocketmq-cluster  broker-b                0     172.16.18.15:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
    rocketmq-cluster  broker-b                1     172.16.18.14:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
    ```
2. [2m-2s-sync](./config/m2)
    ```bash
    # 创建目录
    [hadoop@hadoop02 rocketmq-4.4.0]$ mkdir -p /mnt/data/rocketmq/store/broker-a /mnt/data/rocketmq/store/broker-a/nsumequeue /mnt/data/rocketmq/store/broker-a/commitlog /mnt/data/rocketmq/store/broker-a/index /mnt/data/rocketmq/logs /mnt/data/rocketmq/store/broker-b-s /mnt/data/rocketmq/store/broker-b-s/nsumequeue /mnt/data/rocketmq/store/broker-b-s/commitlog /mnt/data/rocketmq/store/broker-b-s/index
    [hadoop@hadoop03 rocketmq-4.4.0]$ mkdir -p /mnt/data/rocketmq/store/broker-a-s /mnt/data/rocketmq/store/broker-a-s/nsumequeue /mnt/data/rocketmq/store/broker-a-s/commitlog /mnt/data/rocketmq/store/broker-a-s/index /mnt/data/rocketmq/logs /mnt/data/rocketmq/store/broker-b /mnt/data/rocketmq/store/broker-b/nsumequeue /mnt/data/rocketmq/store/broker-b/commitlog /mnt/data/rocketmq/store/broker-b/index
    
 
    # 配置文件
    [hadoop@hadoop02 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-a.properties
    [hadoop@hadoop03 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-b.properties
    
    [hadoop@hadoop02 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-b-s.properties
    [hadoop@hadoop03 rocketmq-4.4.0]$ vim ./conf/2m-2s-async/broker-a-s.properties
 
 
    # 先启动双主 master，再启动双 slave
    [hadoop@hadoop02 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-a.properties &
    [hadoop@hadoop03 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-b.properties &
    
    [hadoop@hadoop02 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-b-s.properties &
    [hadoop@hadoop03 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-a-s.properties &
    
 
    # 查看集群信息
    [hadoop@hadoop02 rocketmq-4.4.0]$ ./bin/mqadmin clusterlist -n 172.16.18.14:9876
    #Cluster Name     #Broker Name            #BID  #Addr                  #Version                #InTPS(LOAD)       #OutTPS(LOAD) #PCWait(ms) #Hour #SPACE
    rocketmq-cluster  broker-a                0     172.16.18.14:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
    rocketmq-cluster  broker-a                1     172.16.18.15:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
    rocketmq-cluster  broker-b                0     172.16.18.15:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
    rocketmq-cluster  broker-b                1     172.16.18.14:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
    ```
3. [2m-noslave](./config/m3)
    ```bash
    [hadoop@hadoop02 rocketmq-4.4.0]$ vim conf/2m-noslave/broker-a.properties
    [hadoop@hadoop03 rocketmq-4.4.0]$ vim conf/2m-noslave/broker-b.properties
    
    # 启动 broker
    [hadoop@hadoop02 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-noslave/broker-a.properties &
    [hadoop@hadoop03 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-noslave/broker-b.properties &
    
    # 查看进程
    [hadoop@hadoop02 rocketmq-4.4.0]$ jps
    17410 NodeManager
    24658 HRegionServer
    17412 Jps
    17301 NamesrvStartup
    17292 DataNode
    [hadoop@hadoop03 rocketmq-4.4.0]$ jps
    18722 Jps
    17780 NodeManager
    18662 NamesrvStartup
    17662 DataNode
    26911 HRegionServer
    ```


### [rocketmq 命令](http://rocketmq.apache.org/docs/cli-admin-tool/)
```bash
# 停止 namesrv
[hadoop@hadopp02 rocketmq-4.4.0]$ ./bin/mqshutdown namesrv
[hadoop@hadopp03 rocketmq-4.4.0]$ ./bin/mqshutdown namesrv

# 停止 broker
[hadoop@hadoop02 rocketmq-4.4.0]$ ./bin/mqshutdown broker
[hadoop@hadoop02 rocketmq-4.4.0]$ ./bin/mqshutdown broker

[hadoop@hadoop02 rocketmq-4.4.0]$ ./bin/mqadmin clusterlist -n 172.16.18.14:9876
#Cluster Name     #Broker Name            #BID  #Addr                  #Version                #InTPS(LOAD)       #OutTPS(LOAD) #PCWait(ms) #Hour #SPACE
rocketmq-cluster  broker-a                0     172.16.18.14:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
rocketmq-cluster  broker-a                1     172.16.18.15:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
rocketmq-cluster  broker-b                0     172.16.18.15:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
rocketmq-cluster  broker-b                1     172.16.18.14:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
```
