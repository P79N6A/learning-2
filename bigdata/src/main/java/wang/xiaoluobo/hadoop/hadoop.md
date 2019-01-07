### hadoop-yarn集群部署
三个节点：bigdata01,bigdata02,bigdata03  
namenode节点-bigdata01
datanode节点-bigdata02,bigdata03

##### 1. 创建用户和用户组(三台机器都需要配置)
```Bash
[root@bigdata01 hadoop-2.8.4]# sudo visudo
hadoop ALL=(ALL) NOPASSWD: ALL
# 创建hadoop用户和用户组
[root@bigdata01 opt]# cat /etc/group | grep hadoop
[root@bigdata01 opt]# cat /etc/passwd | grep hadoop
[root@bigdata01 opt]# groupadd hadoop
[root@bigdata01 opt]# useradd -d /home/hadoop -g hadoop -m hadoop
```

##### 2. 三台机器hadoop用户相互免密码授权
```Bash
[root@bigdata01 opt]# su - hadoop
[hadoop@bigdata01 ~]$ ls -a
.  ..  .bash_history  .bash_logout  .bash_profile  .bashrc
[hadoop@bigdata01 ~]$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
Generating public/private rsa key pair.
Created directory '/home/hadoop/.ssh'.
Your identification has been saved in /home/hadoop/.ssh/id_rsa.
Your public key has been saved in /home/hadoop/.ssh/id_rsa.pub.
The key fingerprint is:
63:51:5f:6e:4f:58:0a:4d:43:39:b0:7d:15:58:fd:e3 hadoop@bigdata01
The key's randomart image is:
+--[ RSA 2048]----+
|          . o=B+=|
|         . . B+=o|
|        .   o *.+|
|         .   . =.|
|        S     . o|
|       . .     E |
|                 |
|                 |
|                 |
+-----------------+
[hadoop@bigdata01 ~]$ cd .ssh/
[hadoop@bigdata01 .ssh]$ ls
id_rsa  id_rsa.pub
[hadoop@bigdata01 .ssh]$ cat id_rsa.pub
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAz0bRbihEdVn32p985B1Ku2eQn+/Zn4zm8l0Sz73JGtE+m5zO+IwjrLYp5YyJXHrwZKT7Ep3/X2gXfbsHaz7gW/N6NzBnjMKV7VZHPW3sZU2wooCxE78CwzUEr79GsrpvmSYwFSWIuntzgvn4X4lHN87KEV7MqpgAZUup1dYAkOPhRDT25GXSlmVLJdtIgsY+vUxYtm+wWwIjzz9zVks9dIAnx3kIfIh9tzA5BgWm/+rrCPvCPNyTo093abbuGgm2fTk/Rv/KYyfvUAVwp7WL7wtHcEhz1kOOoA6pYRGRxLwzLgdjeH3E4pKMrhbjeFqEdSsSaFqc/ylE8xZNf9tv5Q== hadoop@bigdata01

[hadoop@bigdata01 .ssh]$ touch bigdata02.pub bigdata03.pub
[hadoop@bigdata01 .ssh]$ cat *.pub > authorized_keys
[hadoop@bigdata01 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA4e/RuUXeuiOHuFwoowD8IA+RQDXfK8y65zyGI2HgDpNt5hJ6/wO2DGGg3zb24z5pDu8q6maGE90EhyPupSpugGVcT9fW+j9S0ZDAyiqmjvHxlYYwprInH+3z+YuYLDDAZyEbUmRdokxCsoMuylxyOPqwhGlc32gngr8SXQJwKV+E/CfAhjdlaaVL+dAI6pYlgb15CtW1+RMaq+4xb/XCJpnSAULi0jwHA7NHlZy4QN0RnwQo2E/oLmJ0X5XKYuRav+9OZ1ipuI6zjLjtwHJn5pKL8YV8p2vDfEYlX0hObsp2nxMJ6ROa7Mzvhk9HH9OPXcUmJbuYY4mILX+jyTfw3w== hadoop@bigdata02
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA3JmLG5h0w4qy5Q6bHTFrEQs6BQ/ywz/+yLXEuGY5tmPP0dGiQBoVJUOa2b0mGZJVI+OjFGDuGcJwVsKbcbmjnY2QCIUZpmKlxNh5FxGLCsKPVtH5c30RLMySdXuQLBiwkNlei39m7/RyV39FYbgdPvOBeEQjHX+aaQLa30+A3Qme87E7h5g2MVKbnAub717IsxBc4H1D4TAAf9r9CQrtsD0hv/5oO9TKQ4lSJRwWt2oVJ8vzqWvIbWcALaLl+RUbeq3Cj6/GjGca6BYTjSC7OaYUl8nrtL6aq5+xwS9Mxf+oibc4sERkX1CO7a6fLOHdToHTYAHxyaNPRg9BmA2T3Q== hadoop@bigdata03
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAz0bRbihEdVn32p985B1Ku2eQn+/Zn4zm8l0Sz73JGtE+m5zO+IwjrLYp5YyJXHrwZKT7Ep3/X2gXfbsHaz7gW/N6NzBnjMKV7VZHPW3sZU2wooCxE78CwzUEr79GsrpvmSYwFSWIuntzgvn4X4lHN87KEV7MqpgAZUup1dYAkOPhRDT25GXSlmVLJdtIgsY+vUxYtm+wWwIjzz9zVks9dIAnx3kIfIh9tzA5BgWm/+rrCPvCPNyTo093abbuGgm2fTk/Rv/KYyfvUAVwp7WL7wtHcEhz1kOOoA6pYRGRxLwzLgdjeH3E4pKMrhbjeFqEdSsSaFqc/ylE8xZNf9tv5Q== hadoop@bigdata01

[hadoop@bigdata01 .ssh]$ chmod 600 authorized_keys


[hadoop@bigdata03 .ssh]$ ssh bigdata01
The authenticity of host 'bigdata01 (10.27.223.1)' can't be established.
RSA key fingerprint is f7:36:59:50:2b:32:ab:38:a2:19:0c:4f:95:df:4a:e5.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added 'bigdata01,10.27.223.1' (RSA) to the list of known hosts.
Last login: Thu Sep  6 10:14:53 2018 from 10.27.222.242

Welcome to Alibaba Cloud Elastic Compute Service !

[hadoop@bigdata01 ~]$ exit
logout
Connection to bigdata01 closed.
```

##### 3. 环境变量与目录(三台机器都需要配置)
```Bash
[root@bigdata01 opt]# chown -R hadoop:hadoop hadoop-2.8.4
[hadoop@bigdata01 hadoop]$ vim /home/hadoop/.bash_profile
export HADOOP_CONF_DIR=/mnt/opt/hadoop-2.8.4/etc/hadoop
export PATH=$PATH:$HADOOP_CONF_DIR/bin
export HADOOP_USER_NAME=hadoop
export HADOOP_HOME=/mnt/opt/hadoop-2.8.4
export PATH=$PATH:$HADOOP_HOME/bin
[hadoop@bigdata01 bin]$ source /home/hadoop/.bash_profile

# 创建数据与日志目录
[root@bigdata01 mnt]# mkdir -p /mnt/data/hadoop
[root@bigdata01 mnt]# chown -R hadoop:hadoop /mnt/data/hadoop
[root@bigdata01 mnt]# mkdir -p /mnt/log/hadoop
[root@bigdata01 mnt]# chown -R hadoop:hadoop /mnt/log/hadoop
[root@bigdata01 mnt]# mkdir -p /mnt/log/hadoop/tmp
[root@bigdata01 mnt]# chown -R hadoop:hadoop /mnt/log/hadoop/tmp
```

##### 4. hadoop配置文件(三台机器都需要配置)
```Bash
[hadoop@bigdata01 hadoop]$ vim slaves
bigdata02
bigdata03

[hadoop@bigdata01 hadoop]$ vim core-site.xml

[hadoop@bigdata01 hadoop]$ vim hdfs-site.xml
[hadoop@bigdata01 hadoop]$ mkdir -p /mnt/data/hadoop/hdfs/name
[hadoop@bigdata01 hadoop]$ mkdir -p /mnt/data/hadoop/hdfs/data

[hadoop@bigdata01 hadoop]$ vim yarn-site.xml

[hadoop@bigdata01 hadoop]$ mv mapred-site.xml.template mapred-site.xml
[hadoop@bigdata01 hadoop]$ vim mapred-site.xml
```

##### 5. 格式化namenode(bigdata01执行)
```Bash
只在bigdata01上执行
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hdfs namenode -format
18/09/06 14:57:50 INFO namenode.NameNode: STARTUP_MSG:
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   user = hadoop
STARTUP_MSG:   host = bigdata01/10.27.223.1
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.8.4
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r 17e75c2a11685af3e043aa5e604dc831e5b14674; compiled by 'jdu' on 2018-05-08T02:50Z
STARTUP_MSG:   java = 1.8.0_181
************************************************************/
18/09/06 14:57:50 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
18/09/06 14:57:50 INFO namenode.NameNode: createNameNode [-format]
18/09/06 14:57:50 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Formatting using clusterid: CID-78b5bc5d-cd21-44e7-8664-eee2d9a6e573
18/09/06 14:57:50 INFO namenode.FSEditLog: Edit logging is async:true
18/09/06 14:57:50 INFO namenode.FSNamesystem: KeyProvider: null
18/09/06 14:57:50 INFO namenode.FSNamesystem: fsLock is fair: true
18/09/06 14:57:50 INFO namenode.FSNamesystem: Detailed lock hold time metrics enabled: false
18/09/06 14:57:50 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
18/09/06 14:57:50 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
18/09/06 14:57:50 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
18/09/06 14:57:50 INFO blockmanagement.BlockManager: The block deletion will start around 2018 Sep 06 14:57:50
18/09/06 14:57:50 INFO util.GSet: Computing capacity for map BlocksMap
18/09/06 14:57:50 INFO util.GSet: VM type       = 64-bit
18/09/06 14:57:50 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
18/09/06 14:57:50 INFO util.GSet: capacity      = 2^21 = 2097152 entries
18/09/06 14:57:50 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
18/09/06 14:57:50 INFO blockmanagement.BlockManager: defaultReplication         = 2
18/09/06 14:57:50 INFO blockmanagement.BlockManager: maxReplication             = 512
18/09/06 14:57:50 INFO blockmanagement.BlockManager: minReplication             = 1
18/09/06 14:57:50 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
18/09/06 14:57:50 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
18/09/06 14:57:50 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
18/09/06 14:57:50 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
18/09/06 14:57:50 INFO namenode.FSNamesystem: fsOwner             = hadoop (auth:SIMPLE)
18/09/06 14:57:50 INFO namenode.FSNamesystem: supergroup          = supergroup
18/09/06 14:57:50 INFO namenode.FSNamesystem: isPermissionEnabled = true
18/09/06 14:57:50 INFO namenode.FSNamesystem: HA Enabled: false
18/09/06 14:57:50 INFO namenode.FSNamesystem: Append Enabled: true
18/09/06 14:57:50 INFO util.GSet: Computing capacity for map INodeMap
18/09/06 14:57:50 INFO util.GSet: VM type       = 64-bit
18/09/06 14:57:50 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
18/09/06 14:57:50 INFO util.GSet: capacity      = 2^20 = 1048576 entries
18/09/06 14:57:50 INFO namenode.FSDirectory: ACLs enabled? false
18/09/06 14:57:50 INFO namenode.FSDirectory: XAttrs enabled? true
18/09/06 14:57:50 INFO namenode.NameNode: Caching file names occurring more than 10 times
18/09/06 14:57:50 INFO util.GSet: Computing capacity for map cachedBlocks
18/09/06 14:57:50 INFO util.GSet: VM type       = 64-bit
18/09/06 14:57:50 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
18/09/06 14:57:50 INFO util.GSet: capacity      = 2^18 = 262144 entries
18/09/06 14:57:50 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
18/09/06 14:57:50 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
18/09/06 14:57:50 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
18/09/06 14:57:50 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
18/09/06 14:57:50 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
18/09/06 14:57:50 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
18/09/06 14:57:50 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
18/09/06 14:57:50 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
18/09/06 14:57:50 INFO util.GSet: Computing capacity for map NameNodeRetryCache
18/09/06 14:57:50 INFO util.GSet: VM type       = 64-bit
18/09/06 14:57:50 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
18/09/06 14:57:50 INFO util.GSet: capacity      = 2^15 = 32768 entries
18/09/06 14:57:51 INFO namenode.FSImage: Allocated new BlockPoolId: BP-476966115-10.27.223.1-1536217071007
18/09/06 14:57:51 INFO common.Storage: Storage directory /mnt/data/hadoop/hdfs/name has been successfully formatted.
18/09/06 14:57:51 INFO namenode.FSImageFormatProtobuf: Saving image file /mnt/data/hadoop/hdfs/name/current/fsimage.ckpt_0000000000000000000 using no compression
18/09/06 14:57:51 INFO namenode.FSImageFormatProtobuf: Image file /mnt/data/hadoop/hdfs/name/current/fsimage.ckpt_0000000000000000000 of size 323 bytes saved in 0 seconds.
18/09/06 14:57:51 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
18/09/06 14:57:51 INFO util.ExitUtil: Exiting with status 0
18/09/06 14:57:51 INFO namenode.NameNode: SHUTDOWN_MSG:
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at bigdata01/10.27.223.1
************************************************************/
```

##### 6. 启动hdfs(bigdata01执行)
```Bash
[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/start-dfs.sh
18/09/06 14:58:33 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Starting namenodes on [bigdata01]
bigdata01: starting namenode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-namenode-bigdata01.out
bigdata02: starting datanode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-datanode-bigdata02.out
bigdata03: starting datanode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-datanode-bigdata03.out
Starting secondary namenodes [bigdata01]
bigdata01: starting secondarynamenode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-secondarynamenode-bigdata01.out
18/09/06 14:58:49 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/stop-dfs.sh
Stopping namenodes on [bigdata01]
bigdata01: stopping namenode
bigdata02: stopping datanode
bigdata03: stopping datanode
Stopping secondary namenodes [bigdata01]
bigdata01: stopping secondarynamenode
[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/start-dfs.sh
Starting namenodes on [bigdata01]
bigdata01: starting namenode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-namenode-bigdata01.out
bigdata02: starting datanode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-datanode-bigdata02.out
bigdata03: starting datanode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-datanode-bigdata03.out
Starting secondary namenodes [bigdata01]
bigdata01: starting secondarynamenode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-secondarynamenode-bigdata01.out
```

##### 7. 启动yarn(bigdata01执行)
```Bash
[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/start-yarn.sh
starting yarn daemons
starting resourcemanager, logging to /mnt/opt/hadoop-2.8.4/logs/yarn-hadoop-resourcemanager-bigdata01.out
bigdata02: starting nodemanager, logging to /mnt/opt/hadoop-2.8.4/logs/yarn-hadoop-nodemanager-bigdata02.out
bigdata03: starting nodemanager, logging to /mnt/opt/hadoop-2.8.4/logs/yarn-hadoop-nodemanager-bigdata03.out

[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/stop-yarn.sh
stopping yarn daemons
stopping resourcemanager
bigdata02: stopping nodemanager
bigdata03: stopping nodemanager
bigdata02: nodemanager did not stop gracefully after 5 seconds: killing with kill -9
bigdata03: nodemanager did not stop gracefully after 5 seconds: killing with kill -9
no proxyserver to stop
[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/start-yarn.sh
starting yarn daemons
starting resourcemanager, logging to /mnt/opt/hadoop-2.8.4/logs/yarn-hadoop-resourcemanager-bigdata01.out
bigdata02: starting nodemanager, logging to /mnt/opt/hadoop-2.8.4/logs/yarn-hadoop-nodemanager-bigdata02.out
bigdata03: starting nodemanager, logging to /mnt/opt/hadoop-2.8.4/logs/yarn-hadoop-nodemanager-bigdata03.out
```

##### 8. 查看进程(三台机器都需要配置)
```Bash
[hadoop@bigdata01 hadoop-2.8.4]$ jps
26385 ResourceManager
26658 Jps
26197 SecondaryNameNode
25964 NameNode

[hadoop@bigdata02 hadoop-2.8.4]$ jps
26601 DataNode
26733 NodeManager
26879 Jps

[hadoop@bigdata03 hadoop-2.8.4]$ jps
26521 DataNode
26653 NodeManager
26799 Jps
```

##### 9. 测试hadoop环境
```Bash
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hadoop fs -mkdir -p /mnt/data/hadoop/wordcount/
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hadoop fs -copyFromLocal LICENSE.txt hdfs:///mnt/data/hadoop/wordcount/
[hadoop@bigdata01 hadoop-2.8.4]$ ./bin/hadoop fs -ls /mnt/data/hadoop/wordcount/
Found 1 items
-rw-r--r--   2 hadoop supergroup      99253 2018-09-10 10:58 /mnt/data/hadoop/wordcount/LICENSE.txt
```

##### 10. 安装hadoop遇到的问题
- jdk
```Bash
[hadoop@bigdata01 hadoop-2.8.4]$ ./sbin/start-dfs.sh
18/09/06 11:24:21 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Starting namenodes on [bigdata01]
bigdata01: starting namenode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-namenode-bigdata01.out
bigdata01: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: /usr/java/default/bin/java: No such file or directory
bigdata01: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: exec: /usr/java/default/bin/java: cannot execute: No such file or directory
localhost: starting datanode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-datanode-bigdata01.out
localhost: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: /usr/java/default/bin/java: No such file or directory
localhost: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: exec: /usr/java/default/bin/java: cannot execute: No such file or directory
Starting secondary namenodes [bigdata01]
bigdata01: starting secondarynamenode, logging to /mnt/opt/hadoop-2.8.4/logs/hadoop-hadoop-secondarynamenode-bigdata01.out
bigdata01: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: /usr/java/default/bin/java: No such file or directory
bigdata01: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: exec: /usr/java/default/bin/java: cannot execute: No such file or directory
18/09/06 11:24:37 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

JDK错误解决方法：软连接不存在，重新添加即可(网上大多数解决方法是在hadoop启动脚本上指定jdk环境，不建议这样处理，没有从本质上解决问题)
bigdata01: /mnt/opt/hadoop-2.8.4/bin/hdfs: line 305: /usr/java/default/bin/java: No such file or directory
[root@bigdata01 mnt]# ll /usr/java/default
[root@bigdata01 mnt]# ll /usr/java/latest/
total 0
[root@bigdata01 mnt]# which java
/mnt/opt/jdk1.8.0_181/bin/java
lrwxrwxrwx 1 root root 16 Sep  3 14:17 /usr/java/default -> /usr/java/latest
[root@bigdata01 mnt]# rm -rf /usr/java/default
[root@bigdata03 ~]# mkdir -p /usr/java
[root@bigdata01 mnt]# ln -s /mnt/opt/jdk1.8.0_181/ /usr/java/default
[root@bigdata01 mnt]# /usr/java/default/bin/java -version
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
```

- 查看hdfs目录提示警告信息  
下载安装包，[参考文章](https://www.cnblogs.com/kevinq/p/5103653.html)  
http://ftp.ntu.edu.tw/gnu/glibc/glibc-2.14.tar.gz  
http://ftp.ntu.edu.tw/gnu/glibc/glibc-ports-2.5.tar.bz2  

```Bash
[hadoop@bigdata01 hadoop-2.8.4]$ hadoop fs -ls /
18/09/10 10:58:26 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Found 2 items
drwxr-xr-x   - hadoop supergroup          0 2018-09-10 10:56 /mnt
drwxr-xr-x   - hadoop supergroup          0 2018-09-06 17:55 /user

[hadoop@bigdata02 flink-1.6.0]$ cd ../hadoop-2.8.4/lib/native/
[hadoop@bigdata02 native]$ ldd libhadoop.so
./libhadoop.so: /lib64/libc.so.6: version `GLIBC_2.14' not found (required by ./libhadoop.so)
	linux-vdso.so.1 =>  (0x00007fffac869000)
	libdl.so.2 => /lib64/libdl.so.2 (0x00007f3eb17a9000)
	libpthread.so.0 => /lib64/libpthread.so.0 (0x00007f3eb158b000)
	libc.so.6 => /lib64/libc.so.6 (0x00007f3eb11f7000)
	/lib64/ld-linux-x86-64.so.2 (0x00007f3eb1bd6000)
[hadoop@bigdata02 native]$ ldd --version
ldd (GNU libc) 2.12
Copyright (C) 2010 Free Software Foundation, Inc.
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
Written by Roland McGrath and Ulrich Drepper.

[root@bigdata01 opt]# mkdir glibc2.14
[root@bigdata01 opt]# cd glibc2.14/
[root@bigdata01 glibc2.14]# tar -zxvf glibc-2.14.tar.gz
[root@bigdata01 opt]# cd glibc-2.14/
[root@bigdata01 glibc-2.14]# tar -jxvf ../glibc-linuxthreads-2.5.tar.bz2
[root@bigdata01 glibc2.14]# export CFLAGS="-g -O2"
[root@bigdata01 glibc2.14]# ./configure --prefix=/usr --disable-profile --enable-add-ons --with-headers=/usr/include --with-binutils=/usr/bin --disable-sanity-checks
[root@bigdata01 glibc2.14]# make
[root@bigdata01 glibc2.14]# make install
CC="gcc -B/usr/bin/" /usr/bin/perl scripts/test-installation.pl /mnt/opt/glibc2.14/
/usr/bin/ld: cannot find -lnss_test1
collect2: ld returned 1 exit status
Execution of gcc -B/usr/bin/ failed!
The script has found some problems with your installation!
Please read the FAQ and the README file and check the following:
- Did you change the gcc specs file (necessary after upgrading from
  Linux libc5)?
- Are there any symbolic links of the form libXXX.so to old libraries?
  Links like libm.so -> libm.so.5 (where libm.so.5 is an old library) are wrong,
  libm.so should point to the newly installed glibc file - and there should be
  only one such link (check e.g. /lib and /usr/lib)
You should restart this script from your build directory after you've
fixed all problems!
Btw. the script doesn't work if you're installing GNU libc not as your
primary library!
make[1]: *** [install] Error 1
make[1]: Leaving directory `/mnt/opt/glibc2.14/glibc-2.14'
make: *** [install] Error 2
[root@bigdata01 glibc2.14]# ll /lib64/libc.so.6
lrwxrwxrwx 1 root root 12 Sep 10 14:54 /lib64/libc.so.6 -> libc-2.14.so

[hadoop@bigdata01 hadoop-2.8.4]$ hadoop fs -ls /
Found 2 items
drwxr-xr-x   - hadoop supergroup          0 2018-09-10 10:56 /mnt
drwxr-xr-x   - hadoop supergroup          0 2018-09-06 17:55 /user
```

##### 11. hadoop命令行
- hdfs
```Bash
[hadoop@bigdata01 bin]$ ./hdfs dfs -help
Usage: hadoop fs [generic options]
	[-appendToFile <localsrc> ... <dst>]
	[-cat [-ignoreCrc] <src> ...]
	[-checksum <src> ...]
	[-chgrp [-R] GROUP PATH...]
	[-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH...]
	[-chown [-R] [OWNER][:[GROUP]] PATH...]
	[-copyFromLocal [-f] [-p] [-l] [-d] <localsrc> ... <dst>]
	[-copyToLocal [-f] [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-count [-q] [-h] [-v] [-t [<storage type>]] [-u] [-x] <path> ...]
	[-cp [-f] [-p | -p[topax]] [-d] <src> ... <dst>]
	[-createSnapshot <snapshotDir> [<snapshotName>]]
	[-deleteSnapshot <snapshotDir> <snapshotName>]
	[-df [-h] [<path> ...]]
	[-du [-s] [-h] [-x] <path> ...]
	[-expunge]
	[-find <path> ... <expression> ...]
	[-get [-f] [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-getfacl [-R] <path>]
	[-getfattr [-R] {-n name | -d} [-e en] <path>]
	[-getmerge [-nl] [-skip-empty-file] <src> <localdst>]
	[-help [cmd ...]]
	[-ls [-C] [-d] [-h] [-q] [-R] [-t] [-S] [-r] [-u] [<path> ...]]
	[-mkdir [-p] <path> ...]
	[-moveFromLocal <localsrc> ... <dst>]
	[-moveToLocal <src> <localdst>]
	[-mv <src> ... <dst>]
	[-put [-f] [-p] [-l] [-d] <localsrc> ... <dst>]
	[-renameSnapshot <snapshotDir> <oldName> <newName>]
	[-rm [-f] [-r|-R] [-skipTrash] [-safely] <src> ...]
	[-rmdir [--ignore-fail-on-non-empty] <dir> ...]
	[-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>]]
	[-setfattr {-n name [-v value] | -x name} <path>]
	[-setrep [-R] [-w] <rep> <path> ...]
	[-stat [format] <path> ...]
	[-tail [-f] <file>]
	[-test -[defsz] <path>]
	[-text [-ignoreCrc] <src> ...]
	[-touchz <path> ...]
	[-truncate [-w] <length> <path> ...]
	[-usage [cmd ...]]

-appendToFile <localsrc> ... <dst> :
  Appends the contents of all the given local files to the given dst file. The dst
  file will be created if it does not exist. If <localSrc> is -, then the input is
  read from stdin.

-cat [-ignoreCrc] <src> ... :
  Fetch all files that match the file pattern <src> and display their content on
  stdout.

-checksum <src> ... :
  Dump checksum information for files that match the file pattern <src> to stdout.
  Note that this requires a round-trip to a datanode storing each block of the
  file, and thus is not efficient to run on a large number of files. The checksum
  of a file depends on its content, block size and the checksum algorithm and
  parameters used for creating the file.

-chgrp [-R] GROUP PATH... :
  This is equivalent to -chown ... :GROUP ...

-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH... :
  Changes permissions of a file. This works similar to the shell's chmod command
  with a few exceptions.

  -R           modifies the files recursively. This is the only option currently
               supported.
  <MODE>       Mode is the same as mode used for the shell's command. The only
               letters recognized are 'rwxXt', e.g. +t,a+r,g-w,+rwx,o=r.
  <OCTALMODE>  Mode specifed in 3 or 4 digits. If 4 digits, the first may be 1 or
               0 to turn the sticky bit on or off, respectively.  Unlike the
               shell command, it is not possible to specify only part of the
               mode, e.g. 754 is same as u=rwx,g=rx,o=r.

  If none of 'augo' is specified, 'a' is assumed and unlike the shell command, no
  umask is applied.

-chown [-R] [OWNER][:[GROUP]] PATH... :
  Changes owner and group of a file. This is similar to the shell's chown command
  with a few exceptions.

  -R  modifies the files recursively. This is the only option currently
      supported.

  If only the owner or group is specified, then only the owner or group is
  modified. The owner and group names may only consist of digits, alphabet, and
  any of [-_./@a-zA-Z0-9]. The names are case sensitive.

  WARNING: Avoid using '.' to separate user name and group though Linux allows it.
  If user names have dots in them and you are using local file system, you might
  see surprising results since the shell command 'chown' is used for local files.

-copyFromLocal [-f] [-p] [-l] [-d] <localsrc> ... <dst> :
  Identical to the -put command.

-copyToLocal [-f] [-p] [-ignoreCrc] [-crc] <src> ... <localdst> :
  Identical to the -get command.

-count [-q] [-h] [-v] [-t [<storage type>]] [-u] [-x] <path> ... :
  Count the number of directories, files and bytes under the paths
  that match the specified file pattern.  The output columns are:
  DIR_COUNT FILE_COUNT CONTENT_SIZE PATHNAME
  or, with the -q option:
  QUOTA REM_QUOTA SPACE_QUOTA REM_SPACE_QUOTA
        DIR_COUNT FILE_COUNT CONTENT_SIZE PATHNAME
  The -h option shows file sizes in human readable format.
  The -v option displays a header line.
  The -x option excludes snapshots from being calculated.
  The -t option displays quota by storage types.
  It should be used with -q or -u option, otherwise it will be ignored.
  If a comma-separated list of storage types is given after the -t option,
  it displays the quota and usage for the specified types.
  Otherwise, it displays the quota and usage for all the storage
  types that support quota. The list of possible storage types(case insensitive):
  ram_disk, ssd, disk and archive.
  It can also pass the value '', 'all' or 'ALL' to specify all the storage types.
  The -u option shows the quota and
  the usage against the quota without the detailed content summary.

-cp [-f] [-p | -p[topax]] [-d] <src> ... <dst> :
  Copy files that match the file pattern <src> to a destination.  When copying
  multiple files, the destination must be a directory. Passing -p preserves status
  [topax] (timestamps, ownership, permission, ACLs, XAttr). If -p is specified
  with no <arg>, then preserves timestamps, ownership, permission. If -pa is
  specified, then preserves permission also because ACL is a super-set of
  permission. Passing -f overwrites the destination if it already exists. raw
  namespace extended attributes are preserved if (1) they are supported (HDFS
  only) and, (2) all of the source and target pathnames are in the /.reserved/raw
  hierarchy. raw namespace xattr preservation is determined solely by the presence
  (or absence) of the /.reserved/raw prefix and not by the -p option. Passing -d
  will skip creation of temporary file(<dst>._COPYING_).

-createSnapshot <snapshotDir> [<snapshotName>] :
  Create a snapshot on a directory

-deleteSnapshot <snapshotDir> <snapshotName> :
  Delete a snapshot from a directory

-df [-h] [<path> ...] :
  Shows the capacity, free and used space of the filesystem. If the filesystem has
  multiple partitions, and no path to a particular partition is specified, then
  the status of the root partitions will be shown.

  -h  Formats the sizes of files in a human-readable fashion rather than a number
      of bytes.

-du [-s] [-h] [-x] <path> ... :
  Show the amount of space, in bytes, used by the files that match the specified
  file pattern. The following flags are optional:

  -s  Rather than showing the size of each individual file that matches the
      pattern, shows the total (summary) size.
  -h  Formats the sizes of files in a human-readable fashion rather than a number
      of bytes.
  -x  Excludes snapshots from being counted.

  Note that, even without the -s option, this only shows size summaries one level
  deep into a directory.

  The output is in the form
  	size	name(full path)

-expunge :
  Delete files from the trash that are older than the retention threshold

-find <path> ... <expression> ... :
  Finds all files that match the specified expression and
  applies selected actions to them. If no <path> is specified
  then defaults to the current working directory. If no
  expression is specified then defaults to -print.

  The following primary expressions are recognised:
    -name pattern
    -iname pattern
      Evaluates as true if the basename of the file matches the
      pattern using standard file system globbing.
      If -iname is used then the match is case insensitive.

    -print
    -print0
      Always evaluates to true. Causes the current pathname to be
      written to standard output followed by a newline. If the -print0
      expression is used then an ASCII NULL character is appended rather
      than a newline.

  The following operators are recognised:
    expression -a expression
    expression -and expression
    expression expression
      Logical AND operator for joining two expressions. Returns
      true if both child expressions return true. Implied by the
      juxtaposition of two expressions and so does not need to be
      explicitly specified. The second expression will not be
      applied if the first fails.

-get [-f] [-p] [-ignoreCrc] [-crc] <src> ... <localdst> :
  Copy files that match the file pattern <src> to the local name.  <src> is kept.
  When copying multiple files, the destination must be a directory. Passing -f
  overwrites the destination if it already exists and -p preserves access and
  modification times, ownership and the mode.

-getfacl [-R] <path> :
  Displays the Access Control Lists (ACLs) of files and directories. If a
  directory has a default ACL, then getfacl also displays the default ACL.

  -R      List the ACLs of all files and directories recursively.
  <path>  File or directory to list.

-getfattr [-R] {-n name | -d} [-e en] <path> :
  Displays the extended attribute names and values (if any) for a file or
  directory.

  -R             Recursively list the attributes for all files and directories.
  -n name        Dump the named extended attribute value.
  -d             Dump all extended attribute values associated with pathname.
  -e <encoding>  Encode values after retrieving them.Valid encodings are "text",
                 "hex", and "base64". Values encoded as text strings are enclosed
                 in double quotes ("), and values encoded as hexadecimal and
                 base64 are prefixed with 0x and 0s, respectively.
  <path>         The file or directory.

-getmerge [-nl] [-skip-empty-file] <src> <localdst> :
  Get all the files in the directories that match the source file pattern and
  merge and sort them to only one file on local fs. <src> is kept.

  -nl               Add a newline character at the end of each file.
  -skip-empty-file  Do not add new line character for empty file.

-help [cmd ...] :
  Displays help for given command or all commands if none is specified.

-ls [-C] [-d] [-h] [-q] [-R] [-t] [-S] [-r] [-u] [<path> ...] :
  List the contents that match the specified file pattern. If path is not
  specified, the contents of /user/<currentUser> will be listed. For a directory a
  list of its direct children is returned (unless -d option is specified).

  Directory entries are of the form:
  	permissions - userId groupId sizeOfDirectory(in bytes)
  modificationDate(yyyy-MM-dd HH:mm) directoryName

  and file entries are of the form:
  	permissions numberOfReplicas userId groupId sizeOfFile(in bytes)
  modificationDate(yyyy-MM-dd HH:mm) fileName

    -C  Display the paths of files and directories only.
    -d  Directories are listed as plain files.
    -h  Formats the sizes of files in a human-readable fashion
        rather than a number of bytes.
    -q  Print ? instead of non-printable characters.
    -R  Recursively list the contents of directories.
    -t  Sort files by modification time (most recent first).
    -S  Sort files by size.
    -r  Reverse the order of the sort.
    -u  Use time of last access instead of modification for
        display and sorting.

-mkdir [-p] <path> ... :
  Create a directory in specified location.

  -p  Do not fail if the directory already exists

-moveFromLocal <localsrc> ... <dst> :
  Same as -put, except that the source is deleted after it's copied.

-moveToLocal <src> <localdst> :
  Not implemented yet

-mv <src> ... <dst> :
  Move files that match the specified file pattern <src> to a destination <dst>.
  When moving multiple files, the destination must be a directory.

-put [-f] [-p] [-l] [-d] <localsrc> ... <dst> :
  Copy files from the local file system into fs. Copying fails if the file already
  exists, unless the -f flag is given.
  Flags:

  -p  Preserves access and modification times, ownership and the mode.
  -f  Overwrites the destination if it already exists.
  -l  Allow DataNode to lazily persist the file to disk. Forces
         replication factor of 1. This flag will result in reduced
         durability. Use with care.

  -d  Skip creation of temporary file(<dst>._COPYING_).

-renameSnapshot <snapshotDir> <oldName> <newName> :
  Rename a snapshot from oldName to newName

-rm [-f] [-r|-R] [-skipTrash] [-safely] <src> ... :
  Delete all files that match the specified file pattern. Equivalent to the Unix
  command "rm <src>"

  -f          If the file does not exist, do not display a diagnostic message or
              modify the exit status to reflect an error.
  -[rR]       Recursively deletes directories.
  -skipTrash  option bypasses trash, if enabled, and immediately deletes <src>.
  -safely     option requires safety confirmation, if enabled, requires
              confirmation before deleting large directory with more than
              <hadoop.shell.delete.limit.num.files> files. Delay is expected when
              walking over large directory recursively to count the number of
              files to be deleted before the confirmation.

-rmdir [--ignore-fail-on-non-empty] <dir> ... :
  Removes the directory entry specified by each directory argument, provided it is
  empty.

-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>] :
  Sets Access Control Lists (ACLs) of files and directories.
  Options:

  -b          Remove all but the base ACL entries. The entries for user, group
              and others are retained for compatibility with permission bits.
  -k          Remove the default ACL.
  -R          Apply operations to all files and directories recursively.
  -m          Modify ACL. New entries are added to the ACL, and existing entries
              are retained.
  -x          Remove specified ACL entries. Other ACL entries are retained.
  --set       Fully replace the ACL, discarding all existing entries. The
              <acl_spec> must include entries for user, group, and others for
              compatibility with permission bits.
  <acl_spec>  Comma separated list of ACL entries.
  <path>      File or directory to modify.

-setfattr {-n name [-v value] | -x name} <path> :
  Sets an extended attribute name and value for a file or directory.

  -n name   The extended attribute name.
  -v value  The extended attribute value. There are three different encoding
            methods for the value. If the argument is enclosed in double quotes,
            then the value is the string inside the quotes. If the argument is
            prefixed with 0x or 0X, then it is taken as a hexadecimal number. If
            the argument begins with 0s or 0S, then it is taken as a base64
            encoding.
  -x name   Remove the extended attribute.
  <path>    The file or directory.

-setrep [-R] [-w] <rep> <path> ... :
  Set the replication level of a file. If <path> is a directory then the command
  recursively changes the replication factor of all files under the directory tree
  rooted at <path>.

  -w  It requests that the command waits for the replication to complete. This
      can potentially take a very long time.
  -R  It is accepted for backwards compatibility. It has no effect.

-stat [format] <path> ... :
  Print statistics about the file/directory at <path>
  in the specified format. Format accepts filesize in
  blocks (%b), type (%F), group name of owner (%g),
  name (%n), block size (%o), replication (%r), user name
  of owner (%u), modification date (%y, %Y).
  %y shows UTC date as "yyyy-MM-dd HH:mm:ss" and
  %Y shows milliseconds since January 1, 1970 UTC.
  If the format is not specified, %y is used by default.

-tail [-f] <file> :
  Show the last 1KB of the file.

  -f  Shows appended data as the file grows.

-test -[defsz] <path> :
  Answer various questions about <path>, with result via exit status.
    -d  return 0 if <path> is a directory.
    -e  return 0 if <path> exists.
    -f  return 0 if <path> is a file.
    -s  return 0 if file <path> is greater         than zero bytes in size.
    -w  return 0 if file <path> exists         and write permission is granted.
    -r  return 0 if file <path> exists         and read permission is granted.
    -z  return 0 if file <path> is         zero bytes in size, else return 1.

-text [-ignoreCrc] <src> ... :
  Takes a source file and outputs the file in text format.
  The allowed formats are zip and TextRecordInputStream and Avro.

-touchz <path> ... :
  Creates a file of zero length at <path> with current time as the timestamp of
  that <path>. An error is returned if the file exists with non-zero length

-truncate [-w] <length> <path> ... :
  Truncate all files that match the specified file pattern to the specified
  length.

  -w  Requests that the command wait for block recovery to complete, if
      necessary.

-usage [cmd ...] :
  Displays the usage for given command or all commands if none is specified.

Generic options supported are
-conf <configuration file>     specify an application configuration file
-D <property=value>            use value for given property
-fs <file:///|hdfs://namenode:port> specify default filesystem URL to use, overrides 'fs.defaultFS' property from configurations.
-jt <local|resourcemanager:port>    specify a ResourceManager
-files <comma separated list of files>    specify comma separated files to be copied to the map reduce cluster
-libjars <comma separated list of jars>    specify comma separated jar files to include in the classpath.
-archives <comma separated list of archives>    specify comma separated archives to be unarchived on the compute machines.

The general command line syntax is
command [genericOptions] [commandOptions]
```

- 查看hadoop配置
```Bash
[hadoop@bigdata01 data]$ du -ha /mnt/data/hadoop/hdfs
[hadoop@bigdata01 bin]$ ./hadoop fs -du -s /
3943039008  /
[hadoop@bigdata01 bin]$ ./hdfs getconf -namenodes
bigdata01
[hadoop@bigdata01 bin]$ ./hdfs getconf -secondaryNameNodes
bigdata01

[hadoop@bigdata01 bin]$ ./hdfs dfs -ls -R / | more

[hadoop@bigdata01 bin]$ ./hdfs fsck / -files -blocks
/user/hadoop/books <dir>
/user/hive <dir>
/user/hive/warehouse <dir>
Status: HEALTHY
 Total size:	9296561520 B
 Total dirs:	163
 Total files:	1906
 Total symlinks:		0
 Total blocks (validated):	1906 (avg. block size 4877524 B)
 Minimally replicated blocks:	1906 (100.0 %)
 Over-replicated blocks:	0 (0.0 %)
 Under-replicated blocks:	1265 (66.36936 %)
 Mis-replicated blocks:		0 (0.0 %)
 Default replication factor:	2
 Average block replication:	2.0
 Corrupt blocks:		0
 Missing replicas:		10120 (72.63853 %)
 Number of data-nodes:		2
 Number of racks:		1
FSCK ended at Mon Oct 22 14:45:09 CST 2018 in 171 milliseconds


The filesystem under path '/' is HEALTHY
```

