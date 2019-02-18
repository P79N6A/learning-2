https://chu888chu888.gitbooks.io/hadoopstudy/content/

org.apache.hadoop.hdfs.server.namenode.NameNode
```bash
"$HADOOP_PREFIX/sbin/hadoop-daemons.sh" \
  --config "$HADOOP_CONF_DIR" \
  --hostnames "$NAMENODES" \
  --script "$bin/hdfs" start namenode $nameStartOpt


if [ -n "$HADOOP_SECURE_DN_USER" ]; then
  echo \
    "Attempting to start secure cluster, skipping datanodes. " \
    "Run start-secure-dns.sh as root to complete startup."
else
  "$HADOOP_PREFIX/sbin/hadoop-daemons.sh" \
    --config "$HADOOP_CONF_DIR" \
    --script "$bin/hdfs" start datanode $dataStartOpt
fi


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