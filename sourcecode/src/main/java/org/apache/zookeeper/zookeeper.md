# [Zookeeper](http://zookeeper.apache.org/doc/r3.4.3/zookeeperAdmin.html)
    zk version 3.4.13

## zk启动流程
1. zkServer.sh
通过zk启动脚本内容可以看出zk启动类是org.apache.zookeeper.server.quorum.QuorumPeerMain
2. QuorumPeerMain#initializeAndRun  
    1. 创建QuorumPeerConfig对象
    2. DatadirCleanupManager负责清理snapshots快照信息
    3. zk单点/集群方式启动
3. QuorumPeerConfig
    1. 读取zoo.cfg等配置
    2. 校验相关参数
        1. servers和observers是否正确
        2. 是否正确配置serverGroup与serverWeight(如果配置)
        3. 设置QuorumVerifier默认投票实现
        4. 确认当前server state
4. zk单点 ZooKeeperMain  
QuorumPeerMain#initializeAndRun中调用ZooKeeperServerMain.main(args)
初始化ServerConfig配置类，通过ServerCnxnFactory来启动ZooKeeperServer
5. zk集群  
QuorumPeerMain#initializeAndRun中调用runFromConfig(QuorumPeerConfig config) 
    1. 初始化QuorumPeer类(设置ServerCnxnFactory和创建ZKDatabase等)并启动(调用start()方法)
    2. 加载zk database  
    3. 通过ServerCnxnFactory来启动ZooKeeperServer 
    4. 开始leader选举 
    5. 启动线程 
```java
public synchronized void start() {
    loadDataBase();
    cnxnFactory.start();
    startLeaderElection();
    super.start();
}
```

## 启动细节
- zookeeper-3.4.13/bin/zkServer.sh  
zk server启动脚本
```
nohup "$JAVA" "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}" \
-cp "$CLASSPATH" $JVMFLAGS $ZOOMAIN "$ZOOCFG" > "$_ZOO_DAEMON_OUT" 2>&1 < /dev/null &

$ZOOMAIN   -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false org.apache.zookeeper.server.quorum.QuorumPeerMain
$ZOOCFG    /Users/wyd/soft/zookeeper-3.4.13/bin/../conf/zoo.cfg
```

- [QuorumPeerMain](http://people.apache.org/~larsgeorge/zookeeper-1075002/build/docs/dev-api/org/apache/zookeeper/server/quorum/QuorumPeerMain.html)  
zk server启动主类

- [QuorumPeerConfig#parseProperties](http://people.apache.org/~larsgeorge/zookeeper-1075002/build/docs/dev-api/org/apache/zookeeper/server/quorum/QuorumPeerConfig.html#parseProperties(java.util.Properties))  
该方法负责解析zoo.cfg配置文件  
**该类会创建zk server，但是不会在这里启动**  
**解析server.1=127.0.0.1:2888:3888，该类会创建QuorumServer**  
**解析server.1=127.0.0.1:2888:3888:observer，该类会创建QuorumServer(配置该节点为observer，不参与选举)**  
**解析server.1=127.0.0.1:2888:3888:participant，该类会创建QuorumServer(配置该节点为participant，参与选举)**

- [ZooKeeperMain](http://people.apache.org/~larsgeorge/zookeeper-1075002/build/docs/dev-api/org/apache/zookeeper/ZooKeeperMain.html)  
zookeeper启动类，从zkServer.sh脚本中获取启动参数，并尝试启动zookeeper server

- [ServerCnxnFactory](http://people.apache.org/~larsgeorge/zookeeper-1075002/build/docs/dev-api/org/apache/zookeeper/server/ServerCnxnFactory.html)  
ServerCnxnFacotry是ServerCnxn类的工厂，负责对zk server连接管理。有两个子类为NIOServerCnxnFactory(zk默认实现)和NettyServerCnxnFactory  

- [ServerCnxn](http://people.apache.org/~larsgeorge/zookeeper-1075002/build/docs/dev-api/org/apache/zookeeper/server/ServerCnxn.html)  
从客户端到服务器的连接，即每对zk server连接


## FastLeaderElection选举算法

FastLeaderElection是标准的fast paxos的实现，它首先向所有Server提议自己要成为leader，当其它Server收到提议以后，解决 epoch 和 zxid 的冲突，并接受对方的提议，然后向对方发送接受提议完成的消息。

FastLeaderElection算法通过异步的通信方式来收集其它节点的选票，同时在分析选票时又根据投票者的当前状态来作不同的处理，以加快Leader的选举进程。

每个Server都一个接收线程池和一个发送线程池, 在没有发起选举时，这两个线程池处于阻塞状态，直到有消息到来时才解除阻塞并处理消息，同时每个Serve r都有一个选举线程(可以发起选举的线程担任)。

```text
1. 主动发起选举端(选举线程)的处理
   首先当前节点的logicalclock加1，然后生成notification消息，并将消息放入发送队列中，集群配置有几个Server就生成几条消息，
   保证每个Server都能收到此消息，如果当前Server的状态是LOOKING就一直循环检查接收队列是否有消息，
   如果有消息，根据消息中对方的状态进行相应的处理。
2. 主动发送消息端(发送线程池)的处理
   将要发送的消息由Notification消息转换成ToSend消息，然后发送对方，并等待对方的回复。
3. 被动接收消息端(接收线程池)的处理
   将收到的消息转换成Notification消息放入接收队列中，如果对方Server的epoch小于logicalclock则向其发送一个消息(让其更新epoch)；
   如果对方Server处于Looking状态，自己则处于Following或Leading状态，则也发送一个消息(当前Leader已产生，让其尽快收敛)。
```
