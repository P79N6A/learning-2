# rocketmq version 4.4.0

## [RocketMQ Architecture](http://rocketmq.apache.org/docs/rmq-arc/)
![RocketMQ Architecture](http://rocketmq.apache.org/assets/images/rmq-basic-arc.png)
### 1. Overview
Apache RocketMQ是一个分布式消息传递和流媒体平台，具有低延迟，高性能和可靠性，万亿级容量和灵活的可扩展性。它由四部分组成：NameServer，Broker，Producer 和 Consumer。它们中的每一个都可以水平扩展而没有单一的故障点。

### 2. NameServer Cluster
NameServer 提供轻量级服务发现和路由。每个名称服务器记录完整的路由信息，提供相应的读写服务，并支持快速存储扩展。

NameServer 是一个功能齐全的服务器，主要包括两个功能:
1. Broker Management: NameServer 接受来自 Broker 集群的寄存器，并提供心跳机制来检查代理是否存活
2. Routing Management: 每个 NameServer 将保存有关代理群集的整个路由信息和客户端查询的队列信息

RocketMQ 客户端(Producer/Consumer)将从 NameServer 查询队列路由信息，[客户端查找 NameServer 地址4种方式](http://rocketmq.apache.org/rocketmq/four-methods-to-feed-name-server-address-list/)
1. Programmatic Way: producer.setNamesrvAddr(ip:port)
2. Java Options: 使用 rocketmq.namesrv.addr
3. Environment Variable: 使用 NAMESRV_ADDR
4. HTTP Endpoint

### 3. Broker Cluster
Broker 通过提供轻量级的 TOPIC 和 QUEUE 机制来处理消息存储。它们支持 Push 和 Pull 模型，包含容错机制(2个副本或3个副本)，并提供强大的削峰填谷和按原始时间顺序累积数千亿条消息的能力。Brokers 还提供了灾难恢复，丰富的指标统计和警报机制。

![Broker Server](http://rocketmq.apache.org/assets/images/rmq-basic-component.png)

BrokerServer 负责消息存储和传递，消息查询，HA保证等，BrokerServer 重要的子模块
1. Remoting Module: 即代理的条目，处理来自客户端的请求
2. Client Manager: 管理客户端(Producer/Consumer)并维护消费者的主题订阅
3. Store Service: 提供简单的API来存储或查询物理磁盘中的消息
4. HA Service: 提供 master broker 和 slave broker之间的数据同步功能
5. Index Service: 按指定密钥构建消息索引，并提供快速消息查询

### 4. Producer Cluster
Producer 支持分布式部署。Distributed Producers 通过多种负载均衡模式向 Broker 集群发送消息。发送过程支持快速故障并具有低延迟。

### 5. Consumer Cluster
Consumer 支持 Push 和 Pull 模型中的分布式部署。 它还支持群集消费和消息广播。它提供实时消息订阅机制，可以满足大多数消费者的需求。


## rocketmq 源码分析
### mqnamesrv
1. shell 调用关系
[hadoop@hadoop02 bin]$ cat mqnamesrv
```bash
# mqnamesrv
sh ${ROCKETMQ_HOME}/bin/runserver.sh org.apache.rocketmq.namesrv.NamesrvStartup $@

# runserver.sh
$JAVA ${JAVA_OPT} $@
```

### broker
1. shell 调用关系
```bash
# broker
sh ${ROCKETMQ_HOME}/bin/runbroker.sh org.apache.rocketmq.broker.BrokerStartup $@

# runbroker.sh
numactl --interleave=all pwd > /dev/null 2>&1
if [ $? -eq 0 ]
then
	if [ -z "$RMQ_NUMA_NODE" ] ; then
		numactl --interleave=all $JAVA ${JAVA_OPT} $@
	else
		numactl --cpunodebind=$RMQ_NUMA_NODE --membind=$RMQ_NUMA_NODE $JAVA ${JAVA_OPT} $@
	fi
else
	$JAVA ${JAVA_OPT} $@
fi
```










