# hbase version 2.1.2

## hbase启动流程
### HMaster
1. shell调用关系
> $ ./bin/start-hbase.sh --> hbase-daemon.sh --> hbase

```bash
# start-hbase.sh
# ./bin/start-hbase.sh 
if [ "$1" = "autostart" ]
then
  commandToRun="--autostart-window-size ${AUTOSTART_WINDOW_SIZE} --autostart-window-retry-limit ${AUTOSTART_WINDOW_RETRY_LIMIT} autostart"
else
  # commandToRun为start
  commandToRun="start"
fi

# HBASE-6504 - only take the first line of the output in case verbose gc is on
distMode=`$bin/hbase --config "$HBASE_CONF_DIR" org.apache.hadoop.hbase.util.HBaseConfTool hbase.cluster.distributed | head -n 1`

if [ "$distMode" == 'false' ]
then
  "$bin"/hbase-daemon.sh --config "${HBASE_CONF_DIR}" $commandToRun master
else
  "$bin"/hbase-daemons.sh --config "${HBASE_CONF_DIR}" $commandToRun zookeeper
  "$bin"/hbase-daemon.sh --config "${HBASE_CONF_DIR}" $commandToRun master
  "$bin"/hbase-daemons.sh --config "${HBASE_CONF_DIR}" \
    --hosts "${HBASE_REGIONSERVERS}" $commandToRun regionserver
  "$bin"/hbase-daemons.sh --config "${HBASE_CONF_DIR}" \
    --hosts "${HBASE_BACKUP_MASTERS}" $commandToRun master-backup
fi
```

```bash
# hbase-daemon.sh
# 1)
(start)
    check_before_start
    hbase_rotate_log $HBASE_LOGOUT
    hbase_rotate_log $HBASE_LOGGC
    echo running $command, logging to $HBASE_LOGOUT
    # 调用foreground_start方法
    $thiscmd --config "${HBASE_CONF_DIR}" \
        foreground_start $command $args < /dev/null > ${HBASE_LOGOUT} 2>&1  &
    disown -h -r
    sleep 1; head "${HBASE_LOGOUT}"
  ;;
  
  
# 2)  
(foreground_start)
    trap cleanAfterRun SIGHUP SIGINT SIGTERM EXIT
    if [ "$HBASE_NO_REDIRECT_LOG" != "" ]; then
        # NO REDIRECT
        echo "`date` Starting $command on `hostname`"
        echo "`ulimit -a`"
        # in case the parent shell gets the kill make sure to trap signals.
        # Only one will get called. Either the trap or the flow will go through.
        nice -n $HBASE_NICENESS "$HBASE_HOME"/bin/hbase \
            --config "${HBASE_CONF_DIR}" \
            $command "$@" start &
    else
        # 调用bash脚本，并最终将执行log写入hbase-hadoop-master-hadoop01.log
        echo "`date` Starting $command on `hostname`" >> ${HBASE_LOGLOG}
        echo "`ulimit -a`" >> "$HBASE_LOGLOG" 2>&1
        # in case the parent shell gets the kill make sure to trap signals.
        # Only one will get called. Either the trap or the flow will go through.
        nice -n $HBASE_NICENESS "$HBASE_HOME"/bin/hbase \
            --config "${HBASE_CONF_DIR}" \
            $command "$@" start >> ${HBASE_LOGOUT} 2>&1 &
    fi
    # Add to the command log file vital stats on our environment.
    hbase_pid=$!
    echo $hbase_pid > ${HBASE_PID}
    wait $hbase_pid
  ;;  
```

```bash
# hbase
# figure out which class to run
if [ "$COMMAND" = "master" ] ; then
  # 指定HMaster启动类
  CLASS='org.apache.hadoop.hbase.master.HMaster'
  if [ "$1" != "stop" ] && [ "$1" != "clear" ] ; then
    HBASE_OPTS="$HBASE_OPTS $HBASE_MASTER_OPTS"
  fi
elif [ "$COMMAND" = "regionserver" ] ; then
  # 指定HRegionServer启动类
  CLASS='org.apache.hadoop.hbase.regionserver.HRegionServer'
  if [ "$1" != "stop" ] ; then
    HBASE_OPTS="$HBASE_OPTS $HBASE_REGIONSERVER_OPTS"
  fi
fi
```

2. 
3. 



hbase-hadoop-regionserver-hadoop02.log
hbase-hadoop-regionserver-hadoop03.log


















