# elasticsearch version 6.6.0
## es启动流程
1. org.elasticsearch.bootstrap.Elasticsearch

exec "$JAVA" $ES_JAVA_OPTS -Des.path.home="$ES_HOME" -cp "$ES_CLASSPATH" \
          org.elasticsearch.bootstrap.Elasticsearch "$@"




3:~/elasticsearch-5.3.0/bin$ ./elasticsearch -h
starts elasticsearch

Option                Description
------                -----------
-E <KeyValuePair>     Configure a setting
-V, --version         Prints elasticsearch version information and exits
-d, --daemonize       Starts Elasticsearch in the background
-h, --help            show help
-p, --pidfile <Path>  Creates a pid file in the specified path on start
-q, --quiet           Turns off standard ouput/error streams logging in console
-s, --silent          show minimal output
-v, --verbose         show verbose output


启动 main 参数 
/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/bin/java 
-Xms512m -Xmx512m -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -server -Xss1m -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError 
-Des.path.home=/Users/dongzai1005/soft/elasticsearch-5.3.0 
-cp /Users/dongzai1005/soft/elasticsearch-5.3.0/lib/elasticsearch-5.3.0.jar:/Users/dongzai1005/soft/elasticsearch-5.3.0/lib/* 
org.elasticsearch.bootstrap.Elasticsearch




