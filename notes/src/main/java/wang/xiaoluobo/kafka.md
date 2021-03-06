## [kafka_2.12-2.0.0](http://kafka.apache.org/documentation.html)
[kafka install](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/kafka.md)
https://blog.csdn.net/suifeng3051/article/details/48053965  
http://www.jasongj.com/tags/Kafka/  

### 一、kafka介绍
1. Kafka通常用于两类应用：  
    1. 构建可在系统或应用程序之间可靠获取数据的实时流数据管道
    2. 构建转换或响应数据流的实时流应用程序
    
2. kafka特性
    1. 高吞吐量、低延迟：kafka每秒可以处理几十万条消息，它的延迟最低只有几毫秒
    2. 可扩展性：kafka集群支持热扩展
    3. 持久性、可靠性：消息被持久化到本地磁盘，并且支持数据备份防止数据丢失
    4. 容错性：允许集群中节点失败(若副本数量为n,则允许n-1个节点失败)
    5. 高并发：支持数千个客户端同时读写
    6. 直观地在简单读取上构建持久队列，并将其附加到文件，这与日志记录解决方案的情况一样。
    该结构的优点是所有操作都是O(1)并且读取不会阻止写入或相互阻塞。这具有明显的性能优势，因为性能完全与数据大小分离
    7. Kafka支持GZIP，Snappy，LZ4和ZStandard压缩协议
    
### 二、kafka架构
- zookeeper
    zk负责存储kafka broker信息
    
- Broker  
    即kafka server
    
- Topic  
    ![partition log](http://kafka.apache.org/21/images/log_anatomy.png)  
    topic是发布记录的类别或订阅源名称。Kafka的topic总是多用户; 也就是说，一个topic可以有零个，一个或多个消费者订阅写入它的数据。  
    ```Bash
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

两个服务器Kafka群集，托管四个分区(P0-P3)，包含两个使用者组。消费者组A有两个消费者实例，B组有四个消费者实例。

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

需要说明的是，Kafka只解决”fail/recover”，不处理“Byzantine”(“拜占庭”)问题。
　　一条消息只有被“in sync” list里的所有follower都从leader复制过去才会被认为已提交。这样就避免了部分数据被写进了leader，还没来得及被任何follower复制就宕机了，而造成数据丢失(consumer无法消费这些数据)。而对于producer而言，它可以选择是否等待消息commit，这可以通过request.required.acks来设置。这种机制确保了只要“in sync” list有一个或以上的flollower，一条被commit的消息就不会丢失。
　　这里的复制机制即不是同步复制，也不是单纯的异步复制。事实上，同步复制要求“活着的”follower都复制完，这条消息才会被认为commit，这种复制方式极大的影响了吞吐率(高吞吐率是Kafka非常重要的一个特性)。而异步复制方式下，follower异步的从leader复制数据，数据只要被leader写入log就被认为已经commit，这种情况下如果follwer都落后于leader，而leader突然宕机，则会丢失数据。而Kafka的这种使用“in sync” list的方式则很好的均衡了确保数据不丢失以及吞吐率。follower可以批量的从leader复制数据，这样极大的提高复制性能(批量写磁盘)，极大减少了follower与leader的差距(前文有说到，只要follower落后leader不太远，则被认为在“in sync” list里)。

复制单元是主题分区。在非故障情况下，Kafka中的每个分区都有一个Leader和零个或多个follower。包括Leader在内的副本总数构成复制因子。所有读写都将转到分区的leader。通常，除broker之外还有更多的分区，leader在broker之间平均分配。follower的日志与leader的日志相同-具有相同顺序的偏移和消息

Kafka分区的核心是复制日志

- Leader election

http://kafka.apache.org/documentation.html#design_replicatedlog

Kafka所使用的leader election算法更像微软的PacificA算法。

Kafka在Zookeeper中动态维护了一个ISR(in-sync replicas) set，这个set里的所有replica都跟上了leader，只有ISR里的成员才有被选为leader的可能。在这种模式下，对于f+1个replica，一个Kafka topic能在保证不丢失已经ommit的消息的前提下容忍f个replica的失败。在大多数使用场景中，这种模式是非常有利的。事实上，为了容忍f个replica的失败，majority vote和ISR在commit前需要等待的replica数量是一样的，但是ISR需要的总的replica的个数几乎是majority vote的一半。

虽然majority vote与ISR相比有不需等待最慢的server这一优势，但是Kafka作者认为Kafka可以通过producer选择是否被commit阻塞来改善这一问题，并且节省下来的replica和磁盘使得ISR模式仍然值得。
　　
上文提到，在ISR中至少有一个follower时，Kafka可以确保已经commit的数据不丢失，但如果某一个partition的所有replica都挂了，就无法保证数据不丢失了。这种情况下有两种可行的方案：

等待ISR中的任一个replica“活”过来，并且选它作为leader，选择第一个“活”过来的replica(不一定是ISR中的)作为leader

这就需要在可用性和一致性当中作出一个简单的平衡。如果一定要等待ISR中的replica“活”过来，那不可用的时间就可能会相对较长。而且如果ISR中的所有replica都无法“活”过来了，或者数据都丢失了，这个partition将永远不可用。选择第一个“活”过来的replica作为leader，而这个replica不是ISR中的replica，那即使它并不保证已经包含了所有已commit的消息，它也会成为leader而作为consumer的数据源(前文有说明，所有读写都由leader完成)。Kafka0.8.*使用了第二种方式。根据Kafka的文档，在以后的版本中，Kafka支持用户通过配置选择这两种方式中的一种，从而根据不同的使用场景选择高可用性还是强一致性。
　　
上文说明了一个parition的replication过程，然而Kafka集群需要管理成百上千个partition，Kafka通过round-robin的方式来平衡partition从而避免大量partition集中在了少数几个节点上。同时Kafka也需要平衡leader的分布，尽可能的让所有partition的leader均匀分布在不同broker上。另一方面，优化leadership election的过程也是很重要的，毕竟这段时间相应的partition处于不可用状态。一种简单的实现是暂停宕机的broker上的所有partition，并为之选举leader。实际上，Kafka选举一个broker作为controller，这个controller通过watch Zookeeper检测所有的broker failure，并负责为所有受影响的parition选举leader，再将相应的leader调整命令发送至受影响的broker，过程如下图所示。



- Consumer Rebalance
Consumer挂掉或重新分配分区时会发生Consumer Rebalance


- kafka Security 
    ```text
    在0.9.0.0版中，Kafka社区添加了许多功能，这些功能可单独使用或一起使用，从而提高Kafka群集的安全性。目前支持以下安全措施：
    使用SSL或SASL验证来自客户(生产者和消费者)，其他经纪人和工具的经纪人的连接。 Kafka支持以下SASL机制：
    SASL / GSSAPI(Kerberos) - 从版本0.9.0.0开始
    SASL / PLAIN  - 从版本0.10.0.0开始
    SASL / SCRAM-SHA-256和SASL / SCRAM-SHA-512  - 从版本0.10.2.0开始
    SASL / OAUTHBEARER  - 从2.0版开始
    验证从代理到ZooKeeper的连接
    使用SSL加密在代理和客户端之间，代理之间或代理和工具之间传输的数据(请注意，启用SSL时性能会下降，其大小取决于CPU类型和JVM实现。)
    客户端对读/写操作的授权
    授权是可插拔的，并且支持与外部授权服务的集成
    ```

- 消息传递语义  
最多一次 - 消息可能会丢失，但永远不会重新传递  
至少一次 - 消息永远不会丢失，但可能会被重新传递  
恰好一次 - 这是人们真正想要的，每条消息只发送一次  

从0.11.0.0开始，生产者支持使用类似事务的语义向多个主题分区发送消息的能力：即，所有消息都被成功写入，或者都没有。这方面的主要用例是Kafka主题之间的一次性处理

恰好一次语义呢？当从Kafka主题消费并生成另一个主题时，Consumer的位置作为topic中的消息存储，因此我们可以在与接收处理数据的输出topic相同的事务中将offset写入Kafka。如果事务中止，则消费者的位置将恢复为其旧值，并且输出topic上生成的数据将不会被其他consumer看到，具体取决于其“隔离级别”。在默认的“read_uncommitted”隔离级别中，消费者可以看到所有消息，即使它们是中止事务的一部分，但在“read_committed”中，消费者只会从已提交的事务(以及任何不属于的消息)返回消息交易

### 三、kafka leader election
1. Kafka集群Leader选举原理  
    Kafka的Leader选举是通过在zookeeper上创建/controller临时节点来实现leader选举，并在该节点中写入当前broker的信息
    {“version”:1,”brokerid”:1,”timestamp”:”1512018424988”}
    利用Zookeeper的强一致性特性，一个节点只能被一个客户端创建成功，创建成功的broker即为leader，即先到先得原则，leader也就是集群中的controller，负责集群中所有大小事务。
    当leader和zookeeper失去连接时，临时节点会删除，而其他broker会监听该节点的变化，当节点删除时，其他broker会收到事件通知，重新发起leader选举。

2. KafkaController  
    KafkaController初始化ZookeeperLeaderElector对象，为ZookeeperLeaderElector设置两个回调方法，onControllerFailover和onControllerResignation
    onControllerFailover在选举leader成功后会回调，在onControllerFailover中进行leader依赖的模块初始化，包括向zookeeper上/controller_epoch节点上记录leader的选举次数，这个epoch数值在处理分布式脑裂的场景中很有用。
    而onControllerResignation在当前broker不再成为leader(即当前leader退位后)时会回调。
    KafkaController在启动后注册zookeeper的会话超时监听器，并尝试选举leader。

3. SessionExpirationListener  
    当broker和zookeeper重新建立连接后，SessionExpirationListener中的handleNewSession会被调用，这时先关闭之前的leader相关模块，然后重新尝试选举成为leader。
    
4. ZookeeperLeaderElector  
    ZookeeperLeaderElector类实现leader选举的功能，但是它并不负责处理broker和zookeeper的会话超时(连接超时)的情况，而是认为调用者应该在会话恢复(连接重新建立)时进行重新选举。
    
    ZookeeperLeaderElector的startup方法中调用elect方法选举leader
    
    有下面几种情况会调用elect方法
    
    broker启动时，第一次调用
    上一次创建节点成功，但是可能在等Zookeeper响应的时候，连接中断，resign方法中删除/controller节点后，触发了leaderChangeListener的handleDataDeleted
    上一次创建节点未成功，但是可能在等Zookeeper响应的时候，连接中断，而再次进入elect方法时，已有别的broker创建controller节点成功，成为了leader
    上一次创建节点成功，但是onBecomingLeader抛出了异常，而再次进入
    所以elect方法中先获取/controller节点信息，判断是否已经存在，然后再尝试选举leader

    在创建/controller节点时，若收到的异常是ZkNodeExistsException，则说明其他broker已经成为了leader。
    而若是onBecomingLeader的回调方法里出了异常，一般是初始化leader的相关的模块出了问题，如果初始化失败，则调用resign方法先删除/controller节点。
    当/controller节点被删除时，会触发leaderChangeListener的handleDataDeleted，会重新尝试选举成Leader。
    更重要的是也让其他broker有机会成为leader，避免某一个broker的onBecomingLeader一直失败造成整个集群一直处于“群龙无首”的尴尬局面。

5. LeaderChangeListener  
    在startup方法中，注册了/controller节点的IZkDataListener监听器即LeaderChangeListener。
    若节点数据有变化时，则有可能别的broker成为了leader，则调用onResigningAsLeader方法。
    若节点被删除，则是leader已经出了故障下线了，如果当前broker之前是leader，则调用onResigningAsLeader方法，然后重新尝试选举成为leader。
    
    onBecomingLeader方法对应KafkaController里的onControllerFailover方法，当成为新的leader后，要初始化leader所依赖的功能模块
    onResigningAsLeader方法对应KafkaController里的onControllerResignation方法，当leader退位后，要关闭leader所依赖的功能模块