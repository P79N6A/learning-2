# hadoop version 2.8.5

## [hdfs 启动流程](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)
### NameNode
org.apache.hadoop.hdfs.server.namenode.NameNode
```bash
"$HADOOP_PREFIX/sbin/hadoop-daemons.sh" \
  --config "$HADOOP_CONF_DIR" \
  --hostnames "$NAMENODES" \
  --script "$bin/hdfs" start namenode $nameStartOpt
```

NameNode#main() --> NameNode#createNameNode() --> DefaultMetricsSystem.initialize() --> new MetricsSystemImpl() --> new NameNode()


### DataNode
```bash
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

### Secondary NameNode
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
# start resourceManager
"$bin"/yarn-daemon.sh --config $YARN_CONF_DIR  start resourcemanager
# start nodeManager
"$bin"/yarn-daemons.sh --config $YARN_CONF_DIR  start nodemanager
```

## [yarn 启动流程](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/YARN.html)

