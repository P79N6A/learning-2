## [kafka_2.12-2.0.0](http://kafka.apache.org/documentation/)
[参考文章](http://www.jasongj.com/tags/Kafka/)

#### 一、kafka介绍
1. Kafka通常用于两大类应用：  
    1. 构建可在系统或应用程序之间可靠获取数据的实时流数据管道
    2. 构建转换或响应数据流的实时流应用程序
2. Kafka是一种分布式的，基于发布/订阅的消息系统。主要设计目标如下：  
    1. 以时间复杂度为O(1)的方式提供消息持久化能力，即使对TB级以上数据也能保证常数时间的访问性能
    2. 高吞吐率。即使在非常廉价的商用机器上也能做到单机支持每秒100K条消息的传输
    3. 支持Kafka Server间的消息分区，及分布式消费，同时保证每个partition内的消息顺序传输
    4. 同时支持离线数据处理和实时数据处理
3. kafka
    1. Kafka作为一个集群运行在一个或多个可跨多个数据中心的服务器上。
    2. Kafka集群以称为主题的类别存储记录流。
    3. 每条记录都包含一个键，一个值和一个时间戳。

#### 二、kafka架构
- Broker  
    即kafka server
- Topic  
    ![partition log](http://kafka.apache.org/21/images/log_anatomy.png)  
    topic是发布记录的类别或订阅源名称。Kafka的topic总是多用户; 也就是说，一个topic可以有零个，一个或多个消费者订阅写入它的数据。  
    ```sbtshell
    # topic在kafka server的存储位置
    $ cat server.properties | grep "log.dir"
    log.dirs=/tmp/kafka-logs
    ```
- Partition  
    ![log consumer](http://kafka.apache.org/21/images/log_consumer.png)
    kafka的每个分区都是一个有序的，不可变的记录序列，不断附加到结构化的提交日志中。
    分区中的记录每个都分配了一个称为offset的顺序ID号，它唯一地标识分区中的每个记录。
    
    日志中的分区有多种用途。
    1. 它们允许日志扩展到超出适合单个服务器的大小。每个单独的分区必须适合托管它的服务器，但主题可能有许多分区，
    因此它可以处理任意数量的数据。
    2. 充当了并行性的单位
    
- Producer

- Consumer
