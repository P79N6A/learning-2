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
```Bash
$ ./bin/zkCli.sh -server 127.0.0.1:2181

$ ./bin/zkCli.sh

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
	printwatches on|off
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


# This creates a new znode and associates the string "my_data" with the node. 
[zk: localhost:2181(CONNECTED) 12] create /zk_test my_data

[zk: localhost:2181(CONNECTED) 17] ls /
[zk_test]

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

[zk: localhost:2181(CONNECTED) 20] delete /zk_test
[zk: localhost:2181(CONNECTED) 21] ls /
[zookeeper]
```
