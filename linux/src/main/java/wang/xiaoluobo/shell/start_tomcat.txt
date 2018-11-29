#!/bin/sh
name=tomcat8888
count=`ps -ef|grep $name|grep -v grep|wc -l`
pid=`ps -ef|grep $name|grep -v grep|grep -v "$0"|awk '{print $2}'`

echo "pid is $pid."

if [ 0 != $count ];then
  kill -9 $pid
  echo "$pid is killed."
fi

/data1/tomcat8888/bin/catalina.sh start