# rocketmq version 4.4.0
```bash
vim .bash_profile 
ROCKETMQ_HOME=/mnt/opt/rocketmq-4.4.0
PATH=$PATH:$ROCKETMQ_HOME/bin
export ROCKETMQ_HOME PATH

source .bash_profile
```

mkdir -p /mnt/data/rocketmq/store/commitlog
mkdir -p /mnt/data/rocketmq/store/consumequeue
mkdir -p /mnt/data/rocketmq/store/index
mkdir -p /mnt/data/rocketmq/store/abort
mkdir -p /mnt/log/rocketmq/logs


cd /mnt/opt/rocketmq-4.4.0/conf && sed -i 's#${user.home}#/mnt/log/rocketmq#g' *.xml


https://rocketmq.apache.org/docs/rmq-deployment/


双主部署
vim /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-a.properties
vim /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-b.properties

#所属集群名字
brokerClusterName=rocketmq-cluster
#broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-a
#0 表示 Master，>0 表示 Slave
brokerId=0
#nameServer地址，分号分割
namesrvAddr=iss-half-ali-f-app-002-18-14:9876;iss-half-ali-f-app-003-18-15:9876
#在发送消息时，自动创建服务器不存在的topic，默认创建的队列数
defaultTopicQueueNums=4
#是否允许 Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=10911
#删除文件时间点，默认凌晨 4点
deleteWhen=04
#文件保留时间，默认 48 小时
fileReservedTime=120
#commitLog每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000
#检测物理文件磁盘空间
diskMaxUsedSpaceRatio=88
#存储路径
storePathRootDir=/mnt/data/rocketmq/store/
#commitLog 存储路径
storePathCommitLog=/mnt/data/rocketmq/store/commitlog
#消费队列存储路径存储路径
storePathConsumeQueue=/mnt/data/rocketmq/store/consumequeue
#消息索引存储路径
storePathIndex=/mnt/data/rocketmq/store/index
#checkpoint 文件存储路径
storeCheckpoint=/mnt/data/rocketmq/store/checkpoint
#abort 文件存储路径
abortFile=/mnt/data/rocketmq/store/abort
#限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000
#Broker 的角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=ASYNC_MASTER
#刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH
#checkTransactionMessageEnable=false
#发消息线程池数量
#sendMessageThreadPoolNums=128
#拉消息线程池数量
#pullMessageThreadPoolNums=128




vim bin/runbroker.sh
修改 jvm 参数
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512"

nohup ./bin/mqnamesrv &




[hadoop@iss-half-ali-f-app-001-18-13 rocketmq-4.4.0]$ jps
10304 HMaster
17877 Jps
26054 NameNode
17814 NamesrvStartup
26263 SecondaryNameNode
26427 ResourceManager


[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ jps
17410 NodeManager
24658 HRegionServer
17412 Jps
17301 NamesrvStartup
17292 DataNode


[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ jps
18722 Jps
17780 NodeManager
18662 NamesrvStartup
17662 DataNode
26911 HRegionServer




nohup ./bin/mqnamesrv &


[hadoop@iss-half-ali-f-app-001-18-13 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-a.properties &
[hadoop@iss-half-ali-f-app-001-18-13 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-b.properties &





[hadoop@iss-half-ali-f-app-001-18-13 rocketmq-4.4.0]$ ./bin/mqshutdown namesrv




[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ jps
17410 NodeManager
18290 NamesrvStartup
24658 HRegionServer
18534 Jps
17292 DataNode
18462 BrokerStartup

[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ jps
17780 NodeManager
19526 NamesrvStartup
19751 Jps
17662 DataNode
19678 BrokerStartup
26911 HRegionServer


/mnt/log/rocketmq/logs/rocketmqlogs
tail -100f broker.log

