#!/bin/bash
appName="open-api"
ps -ef | grep $appName | grep -v grep | awk '{print $2}' | while read pid
do
    echo "$appName pid is $pid"
    kill -9 $pid
    echo "kill result is $?"
done

nohup java -Xms2048m -Xmx2048m -jar open-api-1.2.6.jar --spring.profiles.active=prod &
echo "$appName start success."
