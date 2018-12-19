### [Zookeeper](http://zookeeper.apache.org/doc/r3.4.3/zookeeperAdmin.html)
    zk version 3.4.13

#### zk启动流程
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

#### 启动细节
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


