# jdk
### 多版本jdk配置
```Bash
sudo vi ~/.bash_profile

export JAVA_8_HOME=`/usr/libexec/java_home -v 1.8`
export JAVA_11_HOME=`/usr/libexec/java_home -v 11.0.1`

export JAVA_HOME=$JAVA_11_HOME

alias jdk8="export JAVA_HOME=$JAVA_8_HOME"
alias jdk11="export JAVA_HOME=$JAVA_11_HOME"
```

### linux配置jdk
```Bash
# 删除jdk
[root@mvare02 config]# rpm -qa | grep jdk
jdk-1.7.0_79-fcs.x86_64
[root@bigdata01 config]# rpm -e --nodeps jdk-1.7.0_79-fcs.x86_64

[root@bigdata01 config]# vim /etc/profile
export JAVA_HOME=/mnt/opt/jdk1.8.0_181
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
[root@bigdata01 config]# source /etc/profile

[root@bigdata01 config]# java -version
openjdk version "1.8.0_181"
OpenJDK Runtime Environment (build 1.8.0_181-b13)
OpenJDK 64-Bit Server VM (build 25.181-b13, mixed mode)
```

### /usr/java/default/bin/java: No such file or directory
```Bash
JDK错误解决方法：软连接不存在，重新添加即可
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

### jdk8
https://www.jianshu.com/p/5b800057f2d8