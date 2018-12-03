## kafka_2.12-2.0.0集群部署
#### 一、kafka集群
1. 集群环境  
两个节点：mware01，mware02  
2. 安装 [jdk](https://github.com/Dongzai1005/learning/blob/master/notes/src/main/java/wang/xiaoluobo/jdk.md), [zookeeper](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/zookeeper.md)  
3. 修改[server.properties](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/server.properties)    
4. 启动kafka节点  
    
        [root@mvare01 kafka_2.12-2.0.0]$ ./bin/kafka-server-start.sh -daemon config/server.properties
    
5. 停止kafka节点

        [root@mware01 kafka_2.12-2.0.0]# ps -ef| grep kafka | grep -v grep | grep -v kafka-manager | awk '{print $2}'
        2203
        [root@mware01 kafka_2.12-2.0.0]# kill -4 2203
        
#### 二、[kafka命令行](http://kafka.apache.org/documentation/#operations)
##### 1. topic
- 创建topic  
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic courier_lbs

- topic列表  
bin/kafka-topics.sh --list --zookeeper localhost:2181

- 查看topic test明细  
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic test

- 修改topic test为5个分区(分区数只能增加不能减少)   
bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic test --partitions 5

- 删除topic test  
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic test

##### 2. producer
- 从控制台向topic test中写入数据  
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

- topic test从头消费  
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

##### 3. consumer
- 查看组信息  
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group test  
bin/kafka-consumer-groups.sh --zookeeper localhost:2181 --describe --group test  

##### 4. 导入导出数据
```sbtshell
$ cd kafka_2.11-2.0.0
# 向test.txt中写入两条数据
$ echo -e "foo\nbar\nAnother line" > test.txt，会将数据写到topic【connect-test】中
$ bin/connect-standalone.sh config/connect-standalone.properties config/connect-file-source.properties config/connect-file-sink.properties

# 生成一个test.sink.txt文件
$ more test.sink.txt
foo
bar
Another line

# 消费topic【connect-test】数据
$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-test --from-beginning
{"schema":{"type":"string","optional":false},"payload":"foo"}
{"schema":{"type":"string","optional":false},"payload":"bar"}
{"schema":{"type":"string","optional":false},"payload":"Another line"}

# 向test.txt中写入一条数据
$ echo "last data" >> test.txt
```
![kafka data](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka01.png)

#### 三、kafka help
```text
Tips:
config/connect-standalone.properties：可以修改kafka连接信息等
config/connect-file-source.properties：可以修改读取文件名称及topic名称等
config/connect-file-sink.properties：可以修改输出文件名称及topic名称等
```