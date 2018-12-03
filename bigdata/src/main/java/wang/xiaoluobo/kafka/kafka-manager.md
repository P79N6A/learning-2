## kafka-manager version 1.3.3.18

##### 配置[application.conf](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/application.conf)
```sbtshell
[root@mvare01 opt]# unzip -q kafka-manager-1.3.3.18.zip
[root@mvare01 conf]# vim application.conf
```

#### 启动kafka-manager
```sbtshell
[root@mvare01 kafka-manager-1.3.3.18]# nohup bin/kafka-manager -Dconfig.file=conf/application.conf -Dhttp.port=8080 &
```

#### 停止kafka-manager
```sbtshell
[root@mware01 kafka-manager-1.3.3.18]# ps -ef| grep kafka-manager | grep -v grep | awk '{print $2}'
2385
[root@mvare01 kafka-manager-1.3.3.18]# kill -4 2385
[root@mware01 kafka-manager-1.3.3.18]# rm -f RUNNING_PID
[root@mvare01 kafka-manager-1.3.3.18]# nohup bin/kafka-manager -Dconfig.file=conf/application.conf -Dhttp.port=8080 &
```

#### kafka-manager管理页面
![add cluster](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager01.png)  
![cluster view](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager02.png)  
![cluster detail view](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager03.png)  
![topic list](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager04.png)  
![topic detail](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager05.png)  
![broker list](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager06.png)  
![consumer list](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager07.png)  
![group consumer detail](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/kafka/images/kafka-manager08.png)  
