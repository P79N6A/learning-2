# tomcat

## jmx config
```Bash
$ cd $JAVA_HOME/jre/lib/management
$ cp jmxremote.password.template /data1/tomcat8888/conf/jmxremote.password
$ cp jmxremote.access /data1/tomcat8888/conf/jmxremote.access

$ vim jmxremote.password 
$ vim jmxremote.access   

$ chmod 600 /data1/tomcat8888/conf/jmx*

$ vi $CATALINA_HOME/bin/catalina.sh

CATALINA_HOME=/data1/tomcat8888
CATALINA_OPTS="-Dfile.encoding=UTF-8 -server -Xms2048m -Xmx2048m -Xss512k -XX:+UseParallelGC -XX:MaxGCPauseMillis=100 -XX:NewRatio=2 -XX:SurvivorRatio=4 -XX:MaxTenuringThreshold=15"
CATALINA_PID=$CATALINA_HOME/catalina.pid
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=8100 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=true -Djava.rmi.server.hostname=114.215.131.39 -Dcom.sun.management.jmxremote.password.file=/data1/tomcat8888/conf/jmxremote.password -Dcom.sun.management.jmxremote.access.file=/data1/tomcat8888/conf/jmxremote.access"
```



## tomcat config
```xml
<Executor
        name="tomcatThreadPool"
        namePrefix="catalina-exec-"
        maxThreads="500"
        minSpareThreads="50"
        maxIdleTime="60000"
        prestartminSpareThreads = "true"
        maxQueueSize = "100"
/>


<Connector 
   executor="tomcatThreadPool"
   port="8888" 
   protocol="org.apache.coyote.http11.Http11Nio2Protocol" 
   connectionTimeout="20000" 
   maxConnections="10000" 
   redirectPort="8443" 
   enableLookups="false" 
   acceptCount="100" 
   maxPostSize="10485760" 
   maxHttpHeaderSize="8192" 
   compression="on" 
   disableUploadTimeout="true" 
   compressionMinSize="2048" 
   acceptorThreadCount="2" 
   compressableMimeType="text/html,text/xml,text/plain,text/css,text/javascript,application/javascript" 
   URIEncoding="UTF-8"
   server="bz-e.com App Server"
/>


<!-- 禁用AJP(如果你服务器没有使用 Apache) -->
<!-- <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" /> -->


<!-- 关闭tomcat热部署 -->
<!-- <Host name="localhost" appBase="webapps" unpackWARs="false" autoDeploy="false" reloadable="false"> -->
```


















