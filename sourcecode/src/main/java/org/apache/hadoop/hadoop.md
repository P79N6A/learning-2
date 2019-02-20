# hadoop version 2.8.5

## [hdfs 启动流程](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)
### NameNode
1. NameNode shell
    ```bash
    # ./sbin/start-dfs.sh
    "$HADOOP_PREFIX/sbin/hadoop-daemons.sh" \
      --config "$HADOOP_CONF_DIR" \
      --hostnames "$NAMENODES" \
      --script "$bin/hdfs" start namenode $nameStartOpt
    ```
    
    ```bash
    # ./sbin/hadoop-daemons.sh
    exec "$bin/slaves.sh" --config $HADOOP_CONF_DIR cd "$HADOOP_PREFIX" \; "$bin/hadoop-daemon.sh" --config $HADOOP_CONF_DIR "$@"
    ```

    ```bash
    # ./bin/hdfs
    if [ "$COMMAND" = "namenode" ] ; then
      CLASS='org.apache.hadoop.hdfs.server.namenode.NameNode'
      HADOOP_OPTS="$HADOOP_OPTS $HADOOP_NAMENODE_OPTS"
    fi
    exec "$JAVA" -Dproc_$COMMAND $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"
    ```
    
2. NameNode java源码
    1. NameNode#main()
    2. NameNode#createNameNode()
    3. 初始化采集系统 DefaultMetricsSystem#initialize()
    4. MetricsSystemImpl#init()
    5. 创建 NameNode 对象


### DataNode
1. DataNode shell
    ```bash
    # ./sbin/start-dfs.sh
    if [ -n "$HADOOP_SECURE_DN_USER" ]; then
      echo \
        "Attempting to start secure cluster, skipping datanodes. " \
        "Run start-secure-dns.sh as root to complete startup."
    else
      "$HADOOP_PREFIX/sbin/hadoop-daemons.sh" \
        --config "$HADOOP_CONF_DIR" \
        --script "$bin/hdfs" start datanode $dataStartOpt
    fi
    ```
    
    ```bash
    # ./sbin/hadoop-daemons.sh
    exec "$bin/slaves.sh" --config $HADOOP_CONF_DIR cd "$HADOOP_PREFIX" \; "$bin/hadoop-daemon.sh" --config $HADOOP_CONF_DIR "$@"
    ```
    
    ```bash
    # ./bin/hdfs
    if [ "$COMMAND" = "datanode" ] ; then
      CLASS='org.apache.hadoop.hdfs.server.datanode.DataNode'
      HADOOP_OPTS="$HADOOP_OPTS $HADOOP_NAMENODE_OPTS"
    fi
    exec "$JAVA" -Dproc_$COMMAND $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"
    ```

2. DataNode java源码

### Secondary NameNode
1. Secondary NameNode shell
    ```bash
    SECONDARY_NAMENODES=$($HADOOP_PREFIX/bin/hdfs getconf -secondarynamenodes 2>/dev/null)
    
    if [ -n "$SECONDARY_NAMENODES" ]; then
      echo "Starting secondary namenodes [$SECONDARY_NAMENODES]"
    
      "$HADOOP_PREFIX/sbin/hadoop-daemons.sh" \
          --config "$HADOOP_CONF_DIR" \
          --hostnames "$SECONDARY_NAMENODES" \
          --script "$bin/hdfs" start secondarynamenode
    fi
    ```
    
    ```bash
    # ./sbin/hadoop-daemons.sh
    exec "$bin/slaves.sh" --config $HADOOP_CONF_DIR cd "$HADOOP_PREFIX" \; "$bin/hadoop-daemon.sh" --config $HADOOP_CONF_DIR "$@"
    ```
    
    ```bash
    # ./bin/hdfs
    if [ "$COMMAND" = "secondarynamenode" ] ; then
      CLASS='org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode'
      HADOOP_OPTS="$HADOOP_OPTS $HADOOP_NAMENODE_OPTS"
    fi
    exec "$JAVA" -Dproc_$COMMAND $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"
    ```

## [yarn 启动流程](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/YARN.html)
### ResourceManager
```bash
# start resourceManager
"$bin"/yarn-daemon.sh --config $YARN_CONF_DIR  start resourcemanager
```
```bash
# ./bin/yarn
if [ "$COMMAND" = "resourcemanager" ] ; then
  CLASSPATH=${CLASSPATH}:$YARN_CONF_DIR/rm-config/log4j.properties
  CLASS='org.apache.hadoop.yarn.server.resourcemanager.ResourceManager'
  YARN_OPTS="$YARN_OPTS $YARN_RESOURCEMANAGER_OPTS"
  if [ "$YARN_RESOURCEMANAGER_HEAPSIZE" != "" ]; then
    JAVA_HEAP_MAX="-Xmx""$YARN_RESOURCEMANAGER_HEAPSIZE""m"
  fi
fi
```

### NodeManager
```bash
# start nodeManager
"$bin"/yarn-daemons.sh --config $YARN_CONF_DIR  start nodemanager
```

```bash
if [ "$COMMAND" = "nodemanager" ] ; then
  CLASSPATH=${CLASSPATH}:$YARN_CONF_DIR/nm-config/log4j.properties
  CLASS='org.apache.hadoop.yarn.server.nodemanager.NodeManager'
  YARN_OPTS="$YARN_OPTS -server $YARN_NODEMANAGER_OPTS"
  if [ "$YARN_NODEMANAGER_HEAPSIZE" != "" ]; then
    JAVA_HEAP_MAX="-Xmx""$YARN_NODEMANAGER_HEAPSIZE""m"
  fi
fi
```


