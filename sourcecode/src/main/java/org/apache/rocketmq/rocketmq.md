# rocketmq version 4.4.0

## rocketmq 源码分析
### mqnamesrv
1. shell 调用关系
[hadoop@hadoop02 bin]$ cat mqnamesrv
```bash
# mqnamesrv
sh ${ROCKETMQ_HOME}/bin/runserver.sh org.apache.rocketmq.namesrv.NamesrvStartup $@

# runserver.sh
$JAVA ${JAVA_OPT} $@
```

### broker
1. shell 调用关系
```bash
# broker
sh ${ROCKETMQ_HOME}/bin/runbroker.sh org.apache.rocketmq.broker.BrokerStartup $@

# runbroker.sh
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
```










