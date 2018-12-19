# [flink version 1.6.0](https://ci.apache.org/projects/flink/flink-docs-release-1.6/quickstart/setup_quickstart.html)
## 抽象类(StreamExecutionEnvironment)共有5个子类
- LegacyLocalStreamEnvironment(遗留本地流环境)  
LocalFlinkMiniCluster(scala实现)
- LocalStreamEnvironment(本地流环境)  
MiniCluster(java 实现)
- RemoteStreamEnvironment(远程流环境)  
可以向指定JobManager提交任务
- StreamContextEnvironment(流上下文环境)  
需要先创建流环境StreamExecutionEnvironment.getExecutionEnvironment()，主要用于客户端或测试应用程序所创建的流环境
- StreamPlanEnvironment(流计划环境)  
生成用户可检查的流式传输作业图时，在Web前端中使用的特殊StreamExecutionEnvironment

## [flink应用启动过程](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/flink/flink.md)
### 1. yarn session方式启动flink
```Bash
$ ./bin/yarn-session.sh -d -n 2 -tm1024 -s 1 -nm flink-test  
$ ./bin/flink run ./app/bigdata-statistics-1.0.jar --kafka mware01:9092,mware02:9092 --interval 20
```

### 2. flink cluster(非HA模式)方式启动flink
- 命令行参数
```Bash
$ ./bin/start-cluster.sh
$ ./bin/flink run ./app/bigdata-statistics-1.0.jar --kafka mware01:9092,mware02:9092 --interval 20
```

- 启动日志
    - jobmanager启动类(org.apache.flink.runtime.entrypoint.StandaloneSessionClusterEntrypoint)
    - taskmanager启动类(org.apache.flink.runtime.taskexecutor.TaskManagerRunner)
```Bash
$ wyd@wangyandong:~/soft/flink-1.6.0/bin$ ./start-cluster.sh
Starting cluster.
/Users/wyd/soft/flink-1.6.0/bin/flink-daemon.sh start standalonesession --configDir /Users/wyd/soft/flink-1.6.0/conf --executionMode cluster
Starting standalonesession daemon on host wangyandong.local.
        /Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/bin/java  -Xms1024m -Xmx1024m  -Dlog.file=/Users/wyd/soft/flink-1.6.0/log/flink-wyd-standalonesession-0-wangyandong.local.log -Dlog4j.configuration=file:/Users/wyd/soft/flink-1.6.0/conf/log4j.properties -Dlogback.configurationFile=file:/Users/wyd/soft/flink-1.6.0/conf/logback.xml -classpath /Users/wyd/soft/flink-1.6.0/lib/flink-python_2.11-1.6.0.jar:/Users/wyd/soft/flink-1.6.0/lib/log4j-1.2.17.jar:/Users/wyd/soft/flink-1.6.0/lib/slf4j-log4j12-1.7.7.jar:/Users/wyd/soft/flink-1.6.0/lib/flink-dist_2.11-1.6.0.jar::/Users/wyd/soft/hadoop-2.8.4/etc/hadoop: org.apache.flink.runtime.entrypoint.StandaloneSessionClusterEntrypoint --configDir /Users/wyd/soft/flink-1.6.0/conf --executionMode cluster > /Users/wyd/soft/flink-1.6.0/log/flink-wyd-standalonesession-0-wangyandong.local.out 200<&- 2>&1 < /dev/null &

/Users/wyd/soft/flink-1.6.0/bin/taskmanager.sh start
/Users/wyd/soft/flink-1.6.0/bin/flink-daemon.sh start taskexecutor --configDir /Users/wyd/soft/flink-1.6.0/conf        
Starting taskexecutor daemon on host wangyandong.local.
        /Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/bin/java  -XX:+UseG1GC -Xms922M -Xmx922M -XX:MaxDirectMemorySize=8388607T  -Dlog.file=/Users/wyd/soft/flink-1.6.0/log/flink-wyd-taskexecutor-0-wangyandong.local.log -Dlog4j.configuration=file:/Users/wyd/soft/flink-1.6.0/conf/log4j.properties -Dlogback.configurationFile=file:/Users/wyd/soft/flink-1.6.0/conf/logback.xml -classpath /Users/wyd/soft/flink-1.6.0/lib/flink-python_2.11-1.6.0.jar:/Users/wyd/soft/flink-1.6.0/lib/log4j-1.2.17.jar:/Users/wyd/soft/flink-1.6.0/lib/slf4j-log4j12-1.7.7.jar:/Users/wyd/soft/flink-1.6.0/lib/flink-dist_2.11-1.6.0.jar::/Users/wyd/soft/hadoop-2.8.4/etc/hadoop: org.apache.flink.runtime.taskexecutor.TaskManagerRunner --configDir /Users/wyd/soft/flink-1.6.0/conf > /Users/wyd/soft/flink-1.6.0/log/flink-wyd-taskexecutor-0-wangyandong.local.out 200<&- 2>&1 < /dev/null &
```

- shell脚本调用关系
```text
# 启动jobmanager
1. start-cluster.sh-->"$FLINK_BIN_DIR"/jobmanager.sh start
2. jobmanager.sh-->"${FLINK_BIN_DIR}"/flink-daemon.sh $STARTSTOP $JOBMANAGER_TYPE "${args[@]}"
3. flink-daemon.sh-->$JAVA_RUN $JVM_ARGS ${FLINK_ENV_JAVA_OPTS} "${log_setting[@]}" -classpath "`manglePathList "$FLINK_TM_CLASSPATH:$INTERNAL_HADOOP_CLASSPATHS"`" ${CLASS_TO_RUN} "${ARGS[@]}" > "$out" 200<&- 2>&1 < /dev/null &

# 启动taskmanager
4. start-cluster.sh-->TMSlaves start
5. config.sh-->TMSlaves()方法
6. TMSlaves()-->"${FLINK_BIN_DIR}"/taskmanager.sh "${CMD}"
7. taskmanager.sh-->"${FLINK_BIN_DIR}"/flink-daemon.sh $STARTSTOP $TYPE "${ARGS[@]}"
8. flink-daemon.sh-->$JAVA_RUN $JVM_ARGS ${FLINK_ENV_JAVA_OPTS} "${log_setting[@]}" -classpath "`manglePathList "$FLINK_TM_CLASSPATH:$INTERNAL_HADOOP_CLASSPATHS"`" ${CLASS_TO_RUN} "${ARGS[@]}" > "$out" 200<&- 2>&1 < /dev/null &
```

## flink程序启动过程
>ParameterTool.fromArgs(args) --> StreamExecutionEnvironment.getExecutionEnvironment() --> ExecutionEnvironment.getExecutionEnvironment() --> ContextEnvironmentFactory.createExecutionEnvironment() 
--> new ContextEnvironment(client, jarFilesToAttach, classpathsToAttach, userCodeClassLoader, savepointSettings) --> (StreamExecutionEnvironment)new StreamContextEnvironment((ContextEnvironment) env) 

### 1. 读取命令行参数ParameterTool.fromArgs(args)
解析获取该命令行参数kafka与interval的值
>bin/flink run ./app/bigdata-statistics-1.0.jar --kafka mware01:9092,mware02:9092 --interval 20

### 2. 创建StreamExecutionEnvironment
```java
ParameterTool parameters = ParameterTool.fromArgs(args);
kafka = parameters.get("kafka");
interval = parameters.getInt("interval");

StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

contextEnvironmentFactory == null ? createLocalEnvironment() : contextEnvironmentFactory.createExecutionEnvironment(); // ContextEnvironmentFactory

lastEnvCreated = isDetached
   ? new DetachedEnvironment(client, jarFilesToAttach, classpathsToAttach, userCodeClassLoader, savepointSettings)
   : new ContextEnvironment(client, jarFilesToAttach, classpathsToAttach, userCodeClassLoader, savepointSettings); // 默认为 false
   
new StreamContextEnvironment((ContextEnvironment) env);  // 创建流上下文环境完毕

env.enableCheckpointing(Constant.CheckPoint);
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

fromKafka2HDFS(env);

env.execute("bigdata service");
```

### 3. 创建数据源(kafka)
```java
// 初始化kafka配置
Properties props = new Properties();
props.setProperty("bootstrap.servers", kafka);
props.setProperty("group.id", "test_topic");

// 创建kafka consumer
FlinkKafkaConsumer011<HDFSObject> source = new FlinkKafkaConsumer011<>(Constant.Topic, new HDFSSchema(), props);
source.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<HDFSObject>() {
    @Override
    public long extractAscendingTimestamp(HDFSObject element) {
        log.info(element.toString());
        if (element != null) {
            return element.ts;
        }
        return 0L;
    }
});
```

### 4. 创建DataStream
```java
DataStream<String> dataStream = env
        .addSource(source)
        .keyBy("clientId")
        .window(TumblingEventTimeWindows.of(Time.seconds(interval)))
        .allowedLateness(Time.seconds(Constants.Delay))
        .apply(new WindowFunction<HDFSObject, String, Tuple, TimeWindow>() {
            @Override
            public void apply(Tuple tuple, TimeWindow window, Iterable<HDFSObject> input, Collector<String> out) throws Exception {
                for (HDFSObject hdfsObject : input) {
                    logger.info("fromKafka2HDFS is {}", hdfsObject.toString());
                    out.collect(hdfsObject.toString());
                }
            }
        });
```

### 5. 创建HDFS Sink
```java
BucketingSink bucketingSink = new BucketingSink<String>(Constants.HDFS_BASE_DIR)
        .setBucketer(new DateTimeBucketer("yyyy-MM-dd"))
        // bucket size: 500MB
        .setBatchSize(500 * 1024 * 1024L)
        // 设置文件前缀，默认为 part
        .setPartPrefix("bigdata-")
        // 一小时
        .setBatchRolloverInterval(60 * 60 * 1000);

dataStream.addSink(bucketingSink);
```

### 6. 执行flink程序
```java
env.execute("bigdata service");

ctx
   .getClient()
   .run(streamGraph, ctx.getJars(), ctx.getClasspaths(), ctx.getUserCodeClassLoader(), ctx.getSavepointRestoreSettings())
   .getJobExecutionResult();
```

## StandaloneSessionClusterEntrypoint
StandaloneSessionClusterEntrypoint.main()

## TaskManagerRunner
TaskManagerRunner.main()