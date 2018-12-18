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


## flink程序启动过程
StreamExecutionEnvironment.getExecutionEnvironment() --> ExecutionEnvironment.getExecutionEnvironment() --> ContextEnvironmentFactory.createExecutionEnvironment() 
--> new ContextEnvironment(client, jarFilesToAttach, classpathsToAttach, userCodeClassLoader, savepointSettings) --> (StreamExecutionEnvironment)new StreamContextEnvironment((ContextEnvironment) env) 

### 1. 创建StreamExecutionEnvironment
```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

contextEnvironmentFactory == null ? createLocalEnvironment() : contextEnvironmentFactory.createExecutionEnvironment(); // ContextEnvironmentFactory

lastEnvCreated = isDetached
   ? new DetachedEnvironment(client, jarFilesToAttach, classpathsToAttach, userCodeClassLoader, savepointSettings)
   : new ContextEnvironment(client, jarFilesToAttach, classpathsToAttach, userCodeClassLoader, savepointSettings); // 默认为 false
   
new StreamContextEnvironment((ContextEnvironment) env);  // 创建流环境完毕

env.enableCheckpointing(Constant.CheckPoint);
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
```

### 2. 创建数据源(kafka)
```java
Properties props = new Properties();
props.setProperty("bootstrap.servers", kafka);
props.setProperty("group.id", "test_topic");

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

### 3. 创建DataStream
```java
DataStream<Tuple4<Long, String, String, Integer>> s = env
        .addSource(source)
        .keyBy("id", "event")
        .window(TumblingEventTimeWindows.of(Time.seconds(interval)))
        .allowedLateness(Time.seconds(Constant.Delay))
        .apply(new WindowFunction<FeedbackObject, Tuple4<Long, String, String, Integer>, Tuple, TimeWindow>() {
            @Override
            public void apply(Tuple tuple, TimeWindow window, Iterable<FeedbackObject> input, Collector<Tuple4<Long, String, String, Integer>> out) throws Exception {
                int sum = 0;
                for (FeedbackObject fo : input) {
                    log.info("event time : " + fo.ts);
                    sum++;
                }

                Tuple4<Long, String, String, Integer> record = new Tuple4();
                record.f0 = window.getStart();
                record.f1 = tuple.getField(0);
                record.f2 = tuple.getField(1);
                record.f3 = sum;

                log.info("key : " + tuple.getField(0) + " | " + tuple.getField(1));
                log.info("window start: " + window.getStart());
                log.info("window end: " + window.getEnd());
                log.info("window max ts: " + window.maxTimestamp());
                log.info("sum is : " + sum);

                out.collect(record);
            }
        });
```

### 4. 创建Sink
```java
BucketingSink bucketingSink = new BucketingSink<Tuple2<IntWritable, Text>>(Constant.HDFS_BASE_DIR)
        // 存储为 hadoop hdfs 文件
        .setWriter(new SequenceFileWriter<>())
        .setBucketer(new DateTimeBucketer("yyyy-MM-dd"))
        // bucket size: 500MB
        .setBatchSize(500 * 1024 * 1024L)
        // 设置文件前缀，默认为 part
        .setPartPrefix("flink_")
        // 一小时
        .setBatchRolloverInterval(60 * 60 * 1000)
        ;

s.addSink(bucketingSink);
```

### 5. 执行flink程序
```java
env.execute("test service");

ctx
   .getClient()
   .run(streamGraph, ctx.getJars(), ctx.getClasspaths(), ctx.getUserCodeClassLoader(), ctx.getSavepointRestoreSettings())
   .getJobExecutionResult();
```