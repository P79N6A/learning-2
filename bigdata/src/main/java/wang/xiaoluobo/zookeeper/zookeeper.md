### zk集群部署 version 3.4.13

1. 配置hosts
2. cd zookeeper-3.4.13/conf
3. mv zoo.sample.cfg zoo.cfg
    ```
    dataDir=/mnt/data/zookeeper
    server.1=bigdata01:2888:3888
    server.2=bigdata02:2888:3888
    server.3=bigdata03:2888:3888
    ```
4. 通过iTerm2分屏同时启动zk server  
$ sudo ./bin/zkServer.sh start

5. 查看zk server状态  
$ sudo ./bin/zkServer.sh status 
 
![bigdata01](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/images/zk01.png)

![bigdata02](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/images/zk02.png)

![bigdata03](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/images/zk03.png)

### zookeeper command
> connect zk client
```text
$ ./bin/zkCli.sh -server 127.0.0.1:2181

$ ./bin/zkCli.sh
```

> zk client command
```text
[zk: localhost:2181(CONNECTED) 11] help
ZooKeeper -server host:port cmd args
	stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history
	redo cmdno
	printwatches on|off     # 设置和显示监视状态
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit
	getAcl path
	close
	connect host:port
```

> ZooKeeper Stat Structure
1. czxid
The zxid of the change that caused this znode to be created.
2. ctime
The time in milliseconds from epoch when this znode was created.
3. mzxid
The zxid of the change that last modified this znode.
4. mtime
The time in milliseconds from epoch when this znode was last modified.
5. pzxid
The zxid of the change that last modified children of this znode.
6. cversion
The number of changes to the children of this znode.
7. dataVersion
The number of changes to the data of this znode.
8. aclVersion
The number of changes to the ACL of this znode.
9. ephemeralOwner
The session id of the owner of this znode if the znode is an ephemeral node. If it is not an ephemeral node, it will be zero.
10. dataLength
The length of the data field of this znode.
11. numChildren
The number of children of this znode.


> 创建节点
```text
# 创建节点zk_test，并设置初始值为my_data
[zk: localhost:2181(CONNECTED) 12] create /zk_test my_data

[zk: localhost:2181(CONNECTED) 17] ls /
[dubbo, zookeeper, zk_test]

[zk: localhost:2181(CONNECTED) 85] ls2 /
[p, dubbo, zookeeper]
cZxid = 0x0
ctime = Thu Jan 01 08:00:00 CST 1970
mZxid = 0x0
mtime = Thu Jan 01 08:00:00 CST 1970
pZxid = 0x20f
cversion = 13
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 3
```

> 获取值
```
# 获取值
[zk: localhost:2181(CONNECTED) 16] get /zk_test
my_data
cZxid = 0x1f9
ctime = Mon Jan 14 15:42:14 CST 2019
mZxid = 0x1f9
mtime = Mon Jan 14 15:42:14 CST 2019
pZxid = 0x1f9
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 7
numChildren = 0
```

> 设置值
```
# 设置值
[zk: localhost:2181(CONNECTED) 18] set /zk_test junk
cZxid = 0x1f9
ctime = Mon Jan 14 15:42:14 CST 2019
mZxid = 0x1fa
mtime = Mon Jan 14 15:44:32 CST 2019
pZxid = 0x1f9
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0

[zk: localhost:2181(CONNECTED) 19] get /zk_test
junk
cZxid = 0x1f9
ctime = Mon Jan 14 15:42:14 CST 2019
mZxid = 0x1fa
mtime = Mon Jan 14 15:44:32 CST 2019
pZxid = 0x1f9
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0
```

> 删除节点
```
# 删除节点(叶子节点)
[zk: localhost:2181(CONNECTED) 20] delete /zk_test
[zk: localhost:2181(CONNECTED) 21] ls /
[dubbo, zookeeper]

[zk: localhost:2181(CONNECTED) 40] create /p ""
[zk: localhost:2181(CONNECTED) 41] create /p/c ""
[zk: localhost:2181(CONNECTED) 42] ls /
[p, dubbo, zookeeper]

[zk: localhost:2181(CONNECTED) 43] delete /p
Node not empty: /p
# 删除当前节点及子节点
[zk: localhost:2181(CONNECTED) 44] rmr /p
[zk: localhost:2181(CONNECTED) 45] ls /
[dubbo, zookeeper]
```
