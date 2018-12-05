## [kafka_2.12-2.0.0](http://kafka.apache.org/documentation/)
[kafka install](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/kafka.md)
https://blog.csdn.net/suifeng3051/article/details/48053965  
http://www.jasongj.com/tags/Kafka/  

#### 一、kafka介绍
1. Kafka通常用于两类应用：  
    1. 构建可在系统或应用程序之间可靠获取数据的实时流数据管道
    2. 构建转换或响应数据流的实时流应用程序
    
2. kafka特性
    1. 高吞吐量、低延迟：kafka每秒可以处理几十万条消息，它的延迟最低只有几毫秒
    2. 可扩展性：kafka集群支持热扩展
    3. 持久性、可靠性：消息被持久化到本地磁盘，并且支持数据备份防止数据丢失
    4. 容错性：允许集群中节点失败（若副本数量为n,则允许n-1个节点失败）
    5. 高并发：支持数千个客户端同时读写

#### 二、kafka架构
- zookeeper
    zk负责存储kafka broker信息
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
    
    日志中的分区用途:
    1. 它们允许日志扩展到超出适合单个服务器的大小。每个单独的分区必须适合托管它的服务器，但主题可能有许多分区，
    因此它可以处理任意数量的数据。
    2. 充当了并行性的单位
    
    日志的分区分布在Kafka集群中的server上，每个server处理数据并请求分区的共享。每个分区都在可配置数量的server上进行复制，以实现容错。
    
    每个分区都有一个kafka server充当leader，零个或多个kafka server充当follower。
    leader处理分区的所有读取和写入请求，而follow被动地复制leader。 如果leader失败，其中一个follower将自动成为新的leader。
    每个kafka server都充当其某些分区的leader和其他kafka server的follower，因此负载在群集中得到很好的平衡。
- Producer
Producer将数据发布到指定的topic(可以指定partition)

- Consumer  
**在一个topic中，Consumer与Partition是一对多的关系，Consumer与Partition一一对应消费速度慢快**  
**在一个topic中，一条消息只能被同一个Consumer group中的一个Consumer消费，多个Consumer group可以同时消费该条消息**

如果所有使用者实例具有相同的使用者组，则记录将有效地在使用者实例上进行负载平衡。

如果所有Consumer实例具有不同的Consumer group，则每个记录将广播到所有消费者进程。
![consumer-groups](http://kafka.apache.org/21/images/consumer-groups.png)

两个服务器Kafka群集，托管四个分区（P0-P3），包含两个使用者组。消费者组A有两个消费者实例，B组有四个消费者实例。

然而，更常见的是，我们发现主题具有少量的消费者群体，每个“逻辑订户”一个。每个组由许多用于可伸缩性和容错的消费者实例组成。这只不过是发布 - 订阅语义，其中订阅者是消费者群集而不是单个进程。

在Kafka中实现消费的方式是通过在消费者实例上划分日志中的分区，以便每个实例在任何时间点都是分配的“公平份额”的独占消费者。维护组中成员资格的过程由Kafka协议动态处理。如果新实例加入该组，他们将从该组的其他成员接管一些分区;如果实例死亡，其分区将分发给其余实例。

Kafka仅提供分区内记录的总订单，而不是主题中不同分区之间的记录。对于大多数应用程序而言，按分区排序与按键分区数据的能力相结合就足够了。但是，如果您需要对记录进行总订单，则可以使用仅包含一个分区的主题来实现，但这将意味着每个使用者组只有一个使用者进程。

- Kafka MirrorMaker 
Kafka MirrorMaker为您的群集提供地理复制支持。使用MirrorMaker，可以跨多个数据中心或云区域复制邮件。 
您可以在主动/被动方案中使用它进行备份和恢复; 或者在主动/主动方案中，将数据放置在离用户较近的位置，或支持数据位置要求。

- Replication
该 Replication与leader election配合提供了自动的failover机制。replication对Kafka的吞吐率是有一定影响的，但极大的增强了可用性。默认情况下，Kafka的replication数量为1。　　每个partition都有一个唯一的leader，所有的读写操作都在leader上完成，leader批量从leader上pull数据。一般情况下partition的数量大于等于broker的数量，并且所有partition的leader均匀分布在broker上。follower上的日志和其leader上的完全一样。
　　和大部分分布式系统一样，Kakfa处理失败需要明确定义一个broker是否alive。对于Kafka而言，Kafka存活包含两个条件，一是它必须维护与Zookeeper的session(这个通过Zookeeper的heartbeat机制来实现)。二是follower必须能够及时将leader的writing复制过来，不能“落后太多”。
　　leader会track“in sync”的node list。如果一个follower宕机，或者落后太多，leader将把它从”in sync” list中移除。这里所描述的“落后太多”指follower复制的消息落后于leader后的条数超过预定值，该值可在$KAFKA_HOME/config/server.properties中配置

replica.lag.max.messages=4000
replica.lag.time.max.ms=10000

需要说明的是，Kafka只解决”fail/recover”，不处理“Byzantine”（“拜占庭”）问题。
　　一条消息只有被“in sync” list里的所有follower都从leader复制过去才会被认为已提交。这样就避免了部分数据被写进了leader，还没来得及被任何follower复制就宕机了，而造成数据丢失（consumer无法消费这些数据）。而对于producer而言，它可以选择是否等待消息commit，这可以通过request.required.acks来设置。这种机制确保了只要“in sync” list有一个或以上的flollower，一条被commit的消息就不会丢失。
　　这里的复制机制即不是同步复制，也不是单纯的异步复制。事实上，同步复制要求“活着的”follower都复制完，这条消息才会被认为commit，这种复制方式极大的影响了吞吐率（高吞吐率是Kafka非常重要的一个特性）。而异步复制方式下，follower异步的从leader复制数据，数据只要被leader写入log就被认为已经commit，这种情况下如果follwer都落后于leader，而leader突然宕机，则会丢失数据。而Kafka的这种使用“in sync” list的方式则很好的均衡了确保数据不丢失以及吞吐率。follower可以批量的从leader复制数据，这样极大的提高复制性能（批量写磁盘），极大减少了follower与leader的差距（前文有说到，只要follower落后leader不太远，则被认为在“in sync” list里）。

- Leader election

Kafka所使用的leader election算法更像微软的PacificA算法。
　　Kafka在Zookeeper中动态维护了一个ISR（in-sync replicas） set，这个set里的所有replica都跟上了leader，只有ISR里的成员才有被选为leader的可能。在这种模式下，对于f+1个replica，一个Kafka topic能在保证不丢失已经ommit的消息的前提下容忍f个replica的失败。在大多数使用场景中，这种模式是非常有利的。事实上，为了容忍f个replica的失败，majority vote和ISR在commit前需要等待的replica数量是一样的，但是ISR需要的总的replica的个数几乎是majority vote的一半。
　　虽然majority vote与ISR相比有不需等待最慢的server这一优势，但是Kafka作者认为Kafka可以通过producer选择是否被commit阻塞来改善这一问题，并且节省下来的replica和磁盘使得ISR模式仍然值得。
　　
　　上文提到，在ISR中至少有一个follower时，Kafka可以确保已经commit的数据不丢失，但如果某一个partition的所有replica都挂了，就无法保证数据不丢失了。这种情况下有两种可行的方案：

等待ISR中的任一个replica“活”过来，并且选它作为leader
选择第一个“活”过来的replica（不一定是ISR中的）作为leader
　　这就需要在可用性和一致性当中作出一个简单的平衡。如果一定要等待ISR中的replica“活”过来，那不可用的时间就可能会相对较长。而且如果ISR中的所有replica都无法“活”过来了，或者数据都丢失了，这个partition将永远不可用。选择第一个“活”过来的replica作为leader，而这个replica不是ISR中的replica，那即使它并不保证已经包含了所有已commit的消息，它也会成为leader而作为consumer的数据源（前文有说明，所有读写都由leader完成）。Kafka0.8.*使用了第二种方式。根据Kafka的文档，在以后的版本中，Kafka支持用户通过配置选择这两种方式中的一种，从而根据不同的使用场景选择高可用性还是强一致性。
　　
　　上文说明了一个parition的replication过程，然尔Kafka集群需要管理成百上千个partition，Kafka通过round-robin的方式来平衡partition从而避免大量partition集中在了少数几个节点上。同时Kafka也需要平衡leader的分布，尽可能的让所有partition的leader均匀分布在不同broker上。另一方面，优化leadership election的过程也是很重要的，毕竟这段时间相应的partition处于不可用状态。一种简单的实现是暂停宕机的broker上的所有partition，并为之选举leader。实际上，Kafka选举一个broker作为controller，这个controller通过watch Zookeeper检测所有的broker failure，并负责为所有受影响的parition选举leader，再将相应的leader调整命令发送至受影响的broker，过程如下图所示。



- Consumer Rebalance


















