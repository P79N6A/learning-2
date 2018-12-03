## flink-1.6.0
### 一、flink cluster
https://archive.apache.org/dist/flink/flink-1.6.0/flink-1.6.0-bin-scala_2.11.tgz
#### 1. [flink cluster setup](https://ci.apache.org/projects/flink/flink-docs-release-1.6/quickstart/setup_quickstart.html)
```sbtshell
$ tar -zxvf flink-1.6.0-bin-scala_2.11.tgz
$ cd flink-1.6.0
$ ./bin/start-cluster.sh
```

#### 2. flink启动环境检验
![flink web](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/flink/images/flink01.png)  
```text
wyd@wangyandong:~/soft/flink-1.6.0$ tail log/flink-*-standalonesession-*.log
2018-12-03 14:28:54,736 INFO  org.apache.flink.runtime.rpc.akka.AkkaRpcService              - Starting RPC endpoint for org.apache.flink.runtime.resourcemanager.StandaloneResourceManager at akka://flink/user/resourcemanager .
2018-12-03 14:28:54,876 INFO  org.apache.flink.runtime.rpc.akka.AkkaRpcService              - Starting RPC endpoint for org.apache.flink.runtime.dispatcher.StandaloneDispatcher at akka://flink/user/dispatcher .
2018-12-03 14:28:54,905 INFO  org.apache.flink.runtime.resourcemanager.StandaloneResourceManager  - ResourceManager akka.tcp://flink@localhost:6123/user/resourcemanager was granted leadership with fencing token 00000000000000000000000000000000
2018-12-03 14:28:54,906 INFO  org.apache.flink.runtime.resourcemanager.slotmanager.SlotManager  - Starting the SlotManager.
2018-12-03 14:28:54,939 INFO  org.apache.flink.runtime.dispatcher.StandaloneDispatcher      - Dispatcher akka.tcp://flink@localhost:6123/user/dispatcher was granted leadership with fencing token 00000000-0000-0000-0000-000000000000
2018-12-03 14:28:54,940 INFO  org.apache.flink.runtime.dispatcher.StandaloneDispatcher      - Recovering all persisted jobs.
2018-12-03 14:28:57,251 INFO  org.apache.flink.runtime.resourcemanager.StandaloneResourceManager  - Replacing old registration of TaskExecutor 812d1acfd2ed20a7747ed480ec45a8ed.
2018-12-03 14:28:57,256 INFO  org.apache.flink.runtime.resourcemanager.slotmanager.SlotManager  - Unregister TaskManager 80acc3c34d7c2389df03402a32b1801a from the SlotManager.
2018-12-03 14:28:57,257 INFO  org.apache.flink.runtime.resourcemanager.StandaloneResourceManager  - The target with resource ID 812d1acfd2ed20a7747ed480ec45a8ed is already been monitored.
2018-12-03 14:28:57,294 INFO  org.apache.flink.runtime.resourcemanager.slotmanager.SlotManager  - Registering TaskManager 812d1acfd2ed20a7747ed480ec45a8ed under 5b9facf883b5fae17d01c07627d912e6 at the SlotManager.
```

#### 3. SocketWindowWordCount
```java
public class SocketWindowWordCount {

    public static void main(String[] args) throws Exception {

        // the port to connect to
        final int port;
        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            port = params.getInt("port");
        } catch (Exception e) {
            System.err.println("No port specified. Please run 'SocketWindowWordCount --port <port>'");
            return;
        }

        // get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream("localhost", port, "\n");

        // parse the data, group it, window it, and aggregate the counts
        DataStream<WordWithCount> windowCounts = text
            .flatMap(new FlatMapFunction<String, WordWithCount>() {
                @Override
                public void flatMap(String value, Collector<WordWithCount> out) {
                    for (String word : value.split("\\s")) {
                        out.collect(new WordWithCount(word, 1L));
                    }
                }
            })
            .keyBy("word")
            .timeWindow(Time.seconds(5), Time.seconds(1))
            .reduce(new ReduceFunction<WordWithCount>() {
                @Override
                public WordWithCount reduce(WordWithCount a, WordWithCount b) {
                    return new WordWithCount(a.word, a.count + b.count);
                }
            });

        // print the results with a single thread, rather than in parallel
        windowCounts.print().setParallelism(1);

        env.execute("Socket Window WordCount");
    }

    // Data type for words with count
    public static class WordWithCount {

        public String word;
        public long count;

        public WordWithCount() {}

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return word + " : " + count;
        }
    }
}
```

#### 4. run flink example
```sbtshell
# netcat 启动本地服务器
$ nc -l 9000

# 启动flink word count程序
# $ ./bin/flink run examples/streaming/SocketWindowWordCount.jar --port 9000
Starting execution of program

# 输入数据
$ nc -l 9000
hi
hi
hi
hello
hello
good
test
text
go
go
go
go
go
go

# 查看统计结果
$ tail -f log/flink-*-taskexecutor-*.out
hi : 2
hi : 1
hello : 2
good : 1
text : 1
test : 1
go : 4
go : 2
```

#### 5. stop flink cluster
```sbtshell
# 停止flink cluster
$ ./bin/stop-cluster.sh
```

### 二、[flink on yarn](https://ci.apache.org/projects/flink/flink-docs-release-1.6/ops/deployment/yarn_setup.html)
#### 1. 配置
```sbtshell
[root@bigdata01 opt]# chown -R hadoop:hadoop flink-1.6.0
[hadoop@bigdata01 conf]$ vim flink-conf.yaml

[hadoop@bigdata01 conf]$ vim slaves
bigdata02
bigdata03

[hadoop@bigdata01 conf]$ vim masters
bigdata01:8081
```

#### 2. [flink config logback](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/best_practices.html)
```sbtshell
[hadoop@bigdata01 flink-1.6.0]$ sudo mkdir /mnt/log/flink
[hadoop@bigdata01 flink-1.6.0]$ sudo chown -R hadoop:hadoop /mnt/log/flink
```

#### 3. 启动和停止flink
```sbtshell
# 启动flink
[hadoop@bigdata02 flink-1.6.0]$ ./bin/yarn-session.sh -n 3 -jm 1024 -tm 4096  -s 3 -nm FlinkOnYarnSession -d -st
2018-09-10 10:32:53,118 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: yarn.application-attempts, 10
2018-09-10 10:32:53,119 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: jobmanager.rpc.address, bigdata01
2018-09-10 10:32:53,119 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: jobmanager.rpc.port, 6123
2018-09-10 10:32:53,119 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: jobmanager.heap.size, 1024m
2018-09-10 10:32:53,119 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: taskmanager.heap.size, 1024m
2018-09-10 10:32:53,120 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: taskmanager.numberOfTaskSlots, 1
2018-09-10 10:32:53,120 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: parallelism.default, 1
2018-09-10 10:32:53,120 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: fs.default-scheme, hdfs://bigdata01:9000
2018-09-10 10:32:53,121 INFO  org.apache.flink.configuration.GlobalConfiguration            - Loading configuration property: rest.port, 8081
2018-09-10 10:32:53,625 WARN  org.apache.hadoop.util.NativeCodeLoader                       - Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
2018-09-10 10:32:53,720 INFO  org.apache.flink.runtime.security.modules.HadoopModule        - Hadoop user set to hadoop (auth:SIMPLE)
2018-09-10 10:32:53,792 INFO  org.apache.hadoop.yarn.client.RMProxy                         - Connecting to ResourceManager at bigdata01/10.27.223.1:8032
2018-09-10 10:32:54,093 WARN  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - The JobManager or TaskManager memory is below the smallest possible YARN Container size. The value of 'yarn.scheduler.minimum-allocation-mb' is '2048'. Please increase the memory size.YARN will allocate the smaller containers but the scheduler will account for the minimum-allocation-mb, maybe not all instances you requested will start.
2018-09-10 10:32:54,093 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Cluster specification: ClusterSpecification{masterMemoryMB=2048, taskManagerMemoryMB=4096, numberTaskManagers=3, slotsPerTaskManager=3}
2018-09-10 10:32:54,552 WARN  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - The configuration directory ('/mnt/opt/flink-1.6.0/conf') contains both LOG4J and Logback configuration files. Please delete or rename one of them.
2018-09-10 10:32:56,170 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Submitting application master application_1536545538166_0002
2018-09-10 10:32:56,198 INFO  org.apache.hadoop.yarn.client.api.impl.YarnClientImpl         - Submitted application application_1536545538166_0002
2018-09-10 10:32:56,198 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Waiting for the cluster to be allocated
2018-09-10 10:32:56,200 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - Deploying cluster, current state ACCEPTED
2018-09-10 10:33:01,256 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - YARN application has been deployed successfully.
2018-09-10 10:33:01,257 INFO  org.apache.flink.yarn.AbstractYarnClusterDescriptor           - The Flink YARN client has been started in detached mode. In order to stop Flink on YARN, use the following command or a YARN web interface to stop it:
yarn application -kill application_1536545538166_0002
Please also note that the temporary files of the YARN session in the home directory will not be removed.
2018-09-10 10:33:01,648 INFO  org.apache.flink.runtime.rest.RestClient                      - Rest client endpoint started.
Flink JobManager is now running on bigdata02:22254 with leader id 00000000-0000-0000-0000-000000000000.
JobManager Web Interface: http://bigdata02:22254
2018-09-10 10:33:01,670 INFO  org.apache.flink.yarn.cli.FlinkYarnSessionCli                 - The Flink YARN client has been started in detached mode. In order to stop Flink on YARN, use the following command or a YARN web interface to stop it:
yarn application -kill application_1536545538166_0002

# 停止flink
[hadoop@bigdata02 flink-1.6.0]$ yarn application -kill application_1536647813549_0002
```

#### 4. 环境测试
```sbtshell
# hdfs创建测试目录并上传文件到hdfs
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hadoop fs -mkdir -p /mnt/data/hadoop/wordcount/
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hadoop fs -copyFromLocal LICENSE.txt hdfs:///mnt/data/hadoop/wordcount/
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hadoop fs -ls /mnt/data/hadoop/wordcount/
Found 1 items
-rw-r--r--   2 hadoop supergroup      99253 2018-09-10 10:58 /mnt/data/hadoop/wordcount/LICENSE.txt

# 结果输出到文本
[hadoop@bigdata02 flink-1.6.0]$ ./bin/flink run examples/batch/WordCount.jar --input hdfs:///mnt/data/hadoop/wordcount/LICENSE.txt > result.txt


# 结果输出到hdfs
[hadoop@bigdata02 flink-1.6.0]$ ./bin/flink run examples/batch/WordCount.jar --input hdfs:///mnt/data/hadoop/wordcount/LICENSE.txt --output hdfs:///mnt/data/hadoop/wordcount/result.txt
[hadoop@bigdata02 flink-1.6.0]$ hadoop fs -get /mnt/data/hadoop/wordcount/result-hdfs.txt

# 同一台机器执行
# kafka消费数据-->数据写入hdfs-->使用hive查询-->写入mongodb-->前端查询mongodb统计结果
[hadoop@bigdata02 flink-1.6.0]$ ./bin/yarn-session.sh -d -n 2 -tm1024 -s 1 -nm flink-test
[hadoop@bigdata02 flink-1.6.0]$ bin/flink run ./app/bigdata-statistics-1.0.jar --kafka mware01:9092,mware02:9092 --interval 20
```

#### 5. yarn-session
```text
./bin/yarn-session.sh
Usage:
   Required
     -n,--container <arg>   Number of YARN container to allocate (=Number of Task Managers)
   Optional
     -D <arg>                        Dynamic properties
     -d,--detached                   Start detached
     -jm,--jobManagerMemory <arg>    Memory for JobManager Container [in MB]
     -nm,--name                      Set a custom name for the application on YARN
     -q,--query                      Display available YARN resources (memory, cores)
     -qu,--queue <arg>               Specify YARN queue.
     -s,--slots <arg>                Number of slots per TaskManager
     -tm,--taskManagerMemory <arg>   Memory per TaskManager Container [in MB]
     -z,--zookeeperNamespace <arg>   Namespace to create the Zookeeper sub-paths for HA mode
```