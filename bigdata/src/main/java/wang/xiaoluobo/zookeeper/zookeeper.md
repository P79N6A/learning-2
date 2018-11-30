### zk集群部署 version 3.4.13

1. 配置 hosts
2. cd zookeeper-3.4.13/conf
3. mv zoo.template.cfg zoo.cfg
    ```
    dataDir=/mnt/data/zookeeper
    server.1=bigdata01:2888:3888
    server.2=bigdata02:2888:3888
    server.3=bigdata03:2888:3888
    ```
4. 通过item2分屏同时启动zk server  
$ sudo ./bin/zkServer.sh start

5. 查看zk server状态
$ sudo ./bin/zkServer.sh status  
[](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/images/zk01.png)
[](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/images/zk02.png)
[](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/zookeeper/images/zk03.png)
