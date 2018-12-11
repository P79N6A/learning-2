### mongodb version 4.0.2

##### 1. 配置环境变量
```text
export MONGODB_HOME=/mnt/opt/mongodb-4.0.2
export PATH=$MONGODB_HOME/bin:$PATH
```

##### 2. 创建数据与日志目录
```Bash
[wangyandong@mvare01 mongodb-linux-x86_64-rhel62-4.0.2]$ sudo mkdir -p /mnt/data/mongodb/
[wangyandong@mvare01 mongodb-linux-x86_64-rhel62-4.0.2]$ sudo mkdir -p /mnt/log/mongodb/
```

##### 3. mongodb配置文件
```Bash
[wangyandong@mvare01 conf]$ cat mongodb.conf
bind_ip = 192.168.1.10
dbpath = /mnt/data/mongodb/
logpath = /mnt/log/mongodb/mongodb.log
port = 27017
fork = true
```

##### 4. 启动mongodb
```Bash
[wangyandong@mvare01 mongodb-4.0.2]$ sudo ./bin/mongod --config conf/mongodb.conf
2018-09-05T15:22:40.078+0800 I CONTROL  [main] Automatically disabling TLS 1.0, to force-enable TLS 1.0 specify --sslDisabledProtocols 'none'
about to fork child process, waiting until server is ready for connections.
forked process: 22379
child process started successfully, parent exiting
```

##### 5. 远程连接mongodb
```Bash
[root@mvare01 bin]# ps -ef| grep mongo | grep -v grep | awk '{print $2}'
22819
[root@mvare01 bin]# kill -4 22819
[root@mware01 mongodb-4.0.2]# ./bin/mongod --config conf/mongodb.conf
2018-09-11T17:51:48.640+0800 I CONTROL  [main] Automatically disabling TLS 1.0, to force-enable TLS 1.0 specify --sslDisabledProtocols 'none'
about to fork child process, waiting until server is ready for connections.
forked process: 2549
child process started successfully, parent exiting


[root@mware01 mongodb-4.0.2]# ./bin/mongod --config conf/mongodb.conf
2018-09-11T17:51:48.640+0800 I CONTROL  [main] Automatically disabling TLS 1.0, to force-enable TLS 1.0 specify --sslDisabledProtocols 'none'
about to fork child process, waiting until server is ready for connections.
forked process: 2549
child process started successfully, parent exiting
[root@mware01 mongodb-4.0.2]# mongo mongodb://192.168.1.10:27017/
MongoDB shell version v4.0.2
connecting to: mongodb://192.168.1.10:27017/
MongoDB server version: 4.0.2
Server has startup warnings:
2018-09-11T17:51:48.685+0800 I STORAGE  [initandlisten]
2018-09-11T17:51:48.685+0800 I STORAGE  [initandlisten] ** WARNING: Using the XFS filesystem is strongly recommended with the WiredTiger storage engine
2018-09-11T17:51:48.685+0800 I STORAGE  [initandlisten] **          See http://dochub.mongodb.org/core/prodnotes-filesystem
2018-09-11T17:51:49.961+0800 I CONTROL  [initandlisten]
2018-09-11T17:51:49.961+0800 I CONTROL  [initandlisten] ** WARNING: Access control is not enabled for the database.
2018-09-11T17:51:49.961+0800 I CONTROL  [initandlisten] **          Read and write access to data and configuration is unrestricted.
2018-09-11T17:51:49.961+0800 I CONTROL  [initandlisten] ** WARNING: You are running this process as the root user, which is not recommended.
2018-09-11T17:51:49.961+0800 I CONTROL  [initandlisten]
---
Enable MongoDB's free cloud-based monitoring service, which will then receive and display
metrics about your deployment (disk utilization, CPU, operation statistics, etc).

The monitoring data will be available on a MongoDB website with a unique URL accessible to you
and anyone you share the URL with. MongoDB may use this information to make product
improvements and to suggest MongoDB products and deployment options to you.

To enable free monitoring, run the following command: db.enableFreeMonitoring()
To permanently disable this reminder, run the following command: db.disableFreeMonitoring()
---

> show databases;
admin   0.000GB
config  0.000GB
local   0.000GB
push    0.002GB
> exit
bye
```