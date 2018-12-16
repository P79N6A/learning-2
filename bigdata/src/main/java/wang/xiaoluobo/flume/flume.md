# flume version 1.7.0
```Bash
# flume写入log配置：
$ ./flume-ng agent -n a1 -f ./../conf/flume.conf


# flume写入hbase配置：
[root@bz39 data1]# wget http://apache.claz.org/flume/1.7.0//data1/flume.tar.gz
[root@bz39 data1]# tar -zxvf /data1/flume.tar.gz 
[root@bz39 data1]# mv /data1/flume flume

[root@bz39 data1]# cp /data1/hbase/lib/hbase-client-1.3.1.jar /data1/flume/lib/
[root@bz39 data1]# cp /data1/hbase/lib/hbase-common-1.3.1.jar /data1/flume/lib/
[root@bz39 data1]# cp /data1/hbase/lib/hbase-protocol-1.3.1.jar /data1/flume/lib/
[root@bz39 data1]# cp /data1/hbase/lib/hbase-server-1.3.1.jar /data1/flume/lib/
[root@bz39 data1]# cp /data1/hbase/lib/hbase-hadoop2-compat-1.3.1.jar /data1/flume/lib/
[root@bz39 data1]# cp /data1/hbase/lib/hbase-hadoop-compat-1.3.1.jar /data1/flume/lib/
[root@bz39 data1]# cp /data1/hbase/lib/htrace-core-3.1.0-incubating.jar /data1/flume/lib/


[root@bz39 conf]# cp flume-conf.properties.template flume.conf
[root@bz39 conf]# cp flume-env.sh.template flume-env.sh
[root@bz39 conf]# vi flume.conf
[root@bz39 conf]# vi flume-env.sh
[root@bz39 bin]# ./flume-ng agent -n a1 -f ./../conf/flume.conf &


[root@bz39 bin]# for i in {1..100}; do   echo "hello Dongzai1005 to flume $i" | nc localhost 8602; done;

[root@bz39 bin]# /data1/hbase/bin/hbase shell
hbase(main):011:0> scan 'scores'


# flume写入kafka配置：
[root@bz39 bin]# ./flume-ng agent -n a1 -f ./../conf/flume-kafka.conf -Dflume.root.logger=DEBUG,console &
[root@bz39 bin]# for i in `seq 1 10`; do  echo "abcd1234" >> /data1/flume/test.txt; done;
[root@bz39 bin]# cd /data1/kafka/bin
[root@bz39 bin]# ./kafka-console-consumer.sh --bootstrap-server zk1:9092 --topic topicTest --from-beginning
```