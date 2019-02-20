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

## [hdfs 体系架构](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)

![hdfs01.png](../images/hdfs01.png)

### hdfs 目录结构
> dfs.name.dir 是 hdfs-site.xml 里配置的若干个目录组成的列表。
```text
${dfs.name.dir}/current/VERSION
                       /edits
                       /fsimage
                       /fstime
1. VERSION
#Mon Jan 21 14:33:37 CST 2019
namespaceID=1129112127
clusterID=CID-a94c1a9d-e727-4ff5-abb1-5c153fe43afa
cTime=1548052417630
storageType=NAME_NODE
blockpoolID=BP-1246397011-172.16.18.13-1548052417630
layoutVersion=-63

2. edits
               
```

#### NameNode
> NameNode
```text
Namenode 上保存着 HDFS 的名字空间。对于任何对文件系统元数据产生修改的操作，Namenode 都会使用一种称为 EditLog 的事务日志记录下来。
例如，在 HDFS 中创建一个文件， Namenode 就会在 Editlog 中插入一条记录来表示；同样地，修改文件的副本系数也将往 Editlog 插入一条记录。
Namenode 在本地操作系统的文件系统中存储这个 Editlog 。整个文件系统的名字空间，包括数据块到文件的映射、文件的属性等，都存储在一个称为 FsImage 的文件中，
这个文件也是放在 Namenode 所在的本地文件系统上。


Namenode 在内存中保存着整个文件系统的名字空间和文件数据块映射 (Blockmap) 的映像。这个关键的元数据结构设计得很紧凑，
因而一个有 4G 内存的 Namenode 足够支撑大量的文件 和目录。当 Namenode 启动时，它从硬盘中读取 Editlog 和 FsImage，
将所有 Editlog 中的事务作 用在内存中的 FsImage 上，并将这个新版本的 FsImage 从内存中保存到本地磁盘上，然后删除旧的 Editlog ，
因为这个旧的 Editlog 的事务都已经作用在 FsImage 上了。这个过程称为一个检查点(checkpoint)。
在当前实现中，检查点只发生在 Namenode 启动时，在不久的将来将实现支持 周期性的检查点。
```

> namenode 目录结构
```text
[hadoop@hadoop01 hadoop]$ ls -RF /mnt/data/hadoop/hdfs/name
/mnt/data/hadoop/hdfs/name:
current/  in_use.lock

/mnt/data/hadoop/hdfs/name/current:
edits_0000000000000000001-0000000000000000002  edits_0000000000000007588-0000000000000007619  edits_0000000000000015474-0000000000000015506  edits_0000000000000021564-0000000000000021596
edits_0000000000000000003-0000000000000000013  edits_0000000000000007620-0000000000000007651  edits_0000000000000015507-0000000000000015539  edits_0000000000000021597-0000000000000021629
edits_0000000000000007332-0000000000000007363  edits_0000000000000015210-0000000000000015242  edits_0000000000000021300-0000000000000021332  edits_0000000000000027307-0000000000000027353
edits_0000000000000007364-0000000000000007395  edits_0000000000000015243-0000000000000015275  edits_0000000000000021333-0000000000000021365  edits_inprogress_0000000000000027354
edits_0000000000000007396-0000000000000007427  edits_0000000000000015276-0000000000000015308  edits_0000000000000021366-0000000000000021398  fsimage_0000000000000027306
edits_0000000000000007428-0000000000000007459  edits_0000000000000015309-0000000000000015341  edits_0000000000000021399-0000000000000021431  fsimage_0000000000000027306.md5
edits_0000000000000007460-0000000000000007491  edits_0000000000000015342-0000000000000015374  edits_0000000000000021432-0000000000000021464  fsimage_0000000000000027353
edits_0000000000000007492-0000000000000007523  edits_0000000000000015375-0000000000000015407  edits_0000000000000021465-0000000000000021497  fsimage_0000000000000027353.md5
edits_0000000000000007524-0000000000000007555  edits_0000000000000015408-0000000000000015440  edits_0000000000000021498-0000000000000021530  seen_txid
edits_0000000000000007556-0000000000000007587  edits_0000000000000015441-0000000000000015473  edits_0000000000000021531-0000000000000021563  VERSION
```

#### SecondaryNamenode
> SecondaryNamenode
```text
Secondary NameNode 定期合并 fsimage 和 edits 日志，将 edits 日志文件大小控制在一个限度下。

Secondary NameNode合并日志时机受core-site.xml 中 fs.checkpoint.period(1小时)和fs.checkpoint.size(64MB)控制

Secondary NameNode 合并日志处理流程  
1. NameNode 响应 Secondary NameNode 请求，将 edit log 推送给 Secondary NameNode，开始重新写一个新的 edit log
2. Secondary NameNode 收到来自 NameNode 的 fsimage 文件和 edit log
3. Secondary NameNode 将 fsimage 加载到内存，应用 edit log，并生成一个新的 fsimage 文件
4. Secondary NameNode 将新的 fsimage 推送给 NameNode
5. NameNode 用新的 fsimage 取代旧的 fsimage，在 fstime 文件中记下检查点发生的时间
```

> SecondaryNamenode 目录结构
```text
[hadoop@hadoop01 hadoop]$ ls -RF /mnt/log/hadoop/tmp/dfs/namesecondary/
/mnt/log/hadoop/tmp/dfs/namesecondary/:
current/  in_use.lock

/mnt/log/hadoop/tmp/dfs/namesecondary/current:
edits_0000000000000000001-0000000000000000002  edits_0000000000000007588-0000000000000007619  edits_0000000000000015474-0000000000000015506  edits_0000000000000021564-0000000000000021596
edits_0000000000000000003-0000000000000000013  edits_0000000000000007620-0000000000000007651  edits_0000000000000015507-0000000000000015539  edits_0000000000000021597-0000000000000021629
edits_0000000000000007332-0000000000000007363  edits_0000000000000015210-0000000000000015242  edits_0000000000000021300-0000000000000021332  edits_0000000000000027307-0000000000000027353
edits_0000000000000007364-0000000000000007395  edits_0000000000000015243-0000000000000015275  edits_0000000000000021333-0000000000000021365  fsimage_0000000000000027306
edits_0000000000000007396-0000000000000007427  edits_0000000000000015276-0000000000000015308  edits_0000000000000021366-0000000000000021398  fsimage_0000000000000027306.md5
edits_0000000000000007428-0000000000000007459  edits_0000000000000015309-0000000000000015341  edits_0000000000000021399-0000000000000021431  fsimage_0000000000000027353
edits_0000000000000007460-0000000000000007491  edits_0000000000000015342-0000000000000015374  edits_0000000000000021432-0000000000000021464  fsimage_0000000000000027353.md5
edits_0000000000000007492-0000000000000007523  edits_0000000000000015375-0000000000000015407  edits_0000000000000021465-0000000000000021497  VERSION
edits_0000000000000007524-0000000000000007555  edits_0000000000000015408-0000000000000015440  edits_0000000000000021498-0000000000000021530
edits_0000000000000007556-0000000000000007587  edits_0000000000000015441-0000000000000015473  edits_0000000000000021531-0000000000000021563
```

#### DataNode
> DataNode
```text
Datanode 将 HDFS 数据以文件的形式存储在本地的文件系统中，它并不知道有 关 HDFS 文件的信息。它把每个 HDFS 数据块存储在本地文件系统的一个单独的文件中。
Datanode 并不在同一个目录创建所有的文件，实际上，它用试探的方法来确定 每个目录的最佳文件数目，并且在适当的时候创建子目录。在同一个目录中创建所有的本地文件
并不是最优的选择，这是因为本地文件系统可能无法高效地在单个目 录中支持大量的文件。

当一个 Datanode 启动时，它会扫描本地文件系统，产生一个这些本地文件对应 的所有 HDFS 数据块的列表，然后作为报告发送到 Namenode，
这个报告就是块状态报告。
```
