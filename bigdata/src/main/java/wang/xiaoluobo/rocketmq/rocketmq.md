# rocketmq version 4.4.0

rocketmq三种配置模式
2m-2s-async(主从异步) [m1](./config/m1)
2m-2s-sync(主从同步)  [m2](./config/m2)
2m-noslave(仅master) [m3](./config/m3)


```bash
vim .bash_profile 
ROCKETMQ_HOME=/mnt/opt/rocketmq-4.4.0
PATH=$PATH:$ROCKETMQ_HOME/bin
export ROCKETMQ_HOME PATH

source .bash_profile
```

mkdir -p /mnt/data/rocketmq/store/commitlog
mkdir -p /mnt/data/rocketmq/store/consumequeue
mkdir -p /mnt/data/rocketmq/store/index
mkdir -p /mnt/log/rocketmq/logs


cd /mnt/opt/rocketmq-4.4.0/conf && sed -i 's#${user.home}#/mnt/log/rocketmq#g' *.xml


https://rocketmq.apache.org/docs/rmq-deployment/


双主部署
vim /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-a.properties
vim /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-b.properties



vim bin/runbroker.sh
修改 jvm 参数
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512"

nohup ./bin/mqnamesrv &




[hadoop@iss-half-ali-f-app-001-18-13 rocketmq-4.4.0]$ jps
10304 HMaster
17877 Jps
26054 NameNode
17814 NamesrvStartup
26263 SecondaryNameNode
26427 ResourceManager


[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ jps
17410 NodeManager
24658 HRegionServer
17412 Jps
17301 NamesrvStartup
17292 DataNode


[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ jps
18722 Jps
17780 NodeManager
18662 NamesrvStartup
17662 DataNode
26911 HRegionServer



[hadoop@iss-half-ali-f-app-002-18-14 bin]$ cat mqnamesrv
sh ${ROCKETMQ_HOME}/bin/runserver.sh org.apache.rocketmq.namesrv.NamesrvStartup $@


[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
[ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=$(dirname $0)/..
export CLASSPATH=.:${BASE_DIR}/conf:${CLASSPATH}

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8  -XX:-UseParNewGC"
JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:/dev/shm/rmq_srv_gc.log -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT}  -XX:-UseLargePages"
JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${BASE_DIR}/lib"
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"

$JAVA ${JAVA_OPT} $@


nohup ./bin/mqnamesrv &


[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-a.properties &
[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c /mnt/opt/rocketmq-4.4.0/conf/2m-noslave/broker-b.properties &

sh ${ROCKETMQ_HOME}/bin/runbroker.sh org.apache.rocketmq.broker.BrokerStartup $@

[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
[ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=$(dirname $0)/..
export CLASSPATH=.:${BASE_DIR}/conf:${CLASSPATH}

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -server -Xms512m -Xmx512m -Xmn256m"
JAVA_OPT="${JAVA_OPT} -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:SoftRefLRUPolicyMSPerMB=0"
JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:/dev/shm/mq_gc_%p.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintAdaptiveSizePolicy"
JAVA_OPT="${JAVA_OPT} -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=30m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -XX:+AlwaysPreTouch"
JAVA_OPT="${JAVA_OPT} -XX:MaxDirectMemorySize=15g"
JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages -XX:-UseBiasedLocking"
JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${BASE_DIR}/lib"
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"

numactl --interleave=all pwd > /dev/null 2>&1
if [ $? -eq 0 ]
then
	if [ -z "$RMQ_NUMA_NODE" ] ; then
		numactl --interleave=all $JAVA ${JAVA_OPT} $@
	else
		numactl --cpunodebind=$RMQ_NUMA_NODE --membind=$RMQ_NUMA_NODE $JAVA ${JAVA_OPT} $@
	fi
else
	$JAVA ${JAVA_OPT} $@
fi







[hadoop@iss-half-ali-f-app-001-18-14 rocketmq-4.4.0]$ ./bin/mqshutdown namesrv
[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ ./bin/mqshutdown broker



[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ jps
17410 NodeManager
18290 NamesrvStartup
24658 HRegionServer
18534 Jps
17292 DataNode
18462 BrokerStartup

[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ jps
17780 NodeManager
19526 NamesrvStartup
19751 Jps
17662 DataNode
19678 BrokerStartup
26911 HRegionServer


/mnt/log/rocketmq/logs/rocketmqlogs
tail -100f broker.log





### 2m-2s-async
[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ mkdir -p /mnt/data/rocketmq/store/broker-a /mnt/data/rocketmq/store/broker-a/nsumequeue /mnt/data/rocketmq/store/broker-a/commitlog /mnt/data/rocketmq/store/broker-a/index /mnt/data/rocketmq/logs /mnt/data/rocketmq/store/broker-b-s /mnt/data/rocketmq/store/broker-b-s/nsumequeue /mnt/data/rocketmq/store/broker-b-s/commitlog /mnt/data/rocketmq/store/broker-b-s/index

[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ mkdir -p /mnt/data/rocketmq/store/broker-a-s /mnt/data/rocketmq/store/broker-a-s/nsumequeue /mnt/data/rocketmq/store/broker-a-s/commitlog /mnt/data/rocketmq/store/broker-a-s/index /mnt/data/rocketmq/logs /mnt/data/rocketmq/store/broker-b /mnt/data/rocketmq/store/broker-b/nsumequeue /mnt/data/rocketmq/store/broker-b/commitlog /mnt/data/rocketmq/store/broker-b/index



nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-a.properties &
nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-b-s.properties &


nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-b.properties &
nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-a-s.properties &




[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-a.properties &
[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-b.properties &

[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-b-s.properties &
[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-async/broker-a-s.properties &



[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ ./bin/mqadmin clusterlist -n 172.16.18.14:9876
#Cluster Name     #Broker Name            #BID  #Addr                  #Version                #InTPS(LOAD)       #OutTPS(LOAD) #PCWait(ms) #Hour #SPACE
rocketmq-cluster  broker-a                0     172.16.18.14:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
rocketmq-cluster  broker-a                1     172.16.18.15:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
rocketmq-cluster  broker-b                0     172.16.18.15:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290
rocketmq-cluster  broker-b                1     172.16.18.14:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.04 0.0290




# 2m-2s-sync
[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-a.properties &
[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-b.properties &

[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-b-s.properties &
[hadoop@iss-half-ali-f-app-003-18-15 rocketmq-4.4.0]$ nohup ./bin/mqbroker -c ./conf/2m-2s-sync/broker-a-s.properties &
[hadoop@iss-half-ali-f-app-002-18-14 rocketmq-4.4.0]$ ./bin/mqadmin clusterlist -n 172.16.18.14:9876
#Cluster Name     #Broker Name            #BID  #Addr                  #Version                #InTPS(LOAD)       #OutTPS(LOAD) #PCWait(ms) #Hour #SPACE
rocketmq-cluster  broker-a                0     172.16.18.14:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
rocketmq-cluster  broker-a                1     172.16.18.15:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
rocketmq-cluster  broker-b                0     172.16.18.15:10911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290
rocketmq-cluster  broker-b                1     172.16.18.14:11911     V4_4_0                   0.00(0,0ms)         0.00(0,0ms)          0 430898.48 0.0290





















