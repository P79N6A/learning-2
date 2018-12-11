## hive2.3.3 hiveserver2 metastore部署
#### 一、部署环境
hiveserver2: bigdata01
metastore: bigdata02,bigdata03

#### 二、配置环境变量
```Bash
[hadoop@bigdata01 hive-2.3.3]$ sudo vim /etc/profile
export HIVE_HOME=/mnt/opt/hive-2.3.3
export HIVE_CONF_DIR=$HIVE_HOME/conf
PATH=$HIVE_HOME/bin:$PATH
[hadoop@bigdata01 hive-2.3.3]$ source /etc/profile
```

#### 三、hive权限配置
```Bash
[root@bigdata01 opt]# chown -R hadoop.hadoop hive-2.3.3

[hadoop@bigdata01 bin]$ ./hdfs dfs -mkdir -p /user/hive/warehouse
[hadoop@bigdata01 bin]$ ./hdfs dfs -chmod -R 777 /user/hive/warehouse
[hadoop@bigdata01 bin]$ ./hdfs dfs -mkdir -p /tmp/hive
[hadoop@bigdata01 bin]$ ./hdfs dfs -chmod -R 777 /tmp/hive
[hadoop@bigdata01 bin]$ ./hdfs dfs -ls /
Found 3 items
drwxr-xr-x   - hadoop supergroup          0 2018-09-10 10:56 /mnt
drwxr-xr-x   - hadoop supergroup          0 2018-09-11 10:06 /tmp
drwxr-xr-x   - hadoop supergroup          0 2018-09-11 10:05 /user
```

#### 四、[hive-env.sh](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/hive/config/hive-env.sh)
```Bash
[hadoop@bigdata01 conf]$ mv hive-default.xml.template hive-site.xml
[hadoop@bigdata01 conf]# vim hive-env.sh
```

#### 五、[hive-site.sh](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/hive/config/hive-site.sh)
```Bash
# hiveserver2配置，只配置metastore信息，不配置mysql连接信息，替换内容metastore节点也需要修改
[hadoop@bigdata01 conf]$ vim hive-site.xml
<property>
    <name>hive.server2.thrift.bind.host</name>
    <value>bigdata01<value/>
    <description>Bind host on which to run the HiveServer2 Thrift service.</description>
  </property>
<property>
    <name>system:java.io.tmpdir</name>
    <value>/mnt/data/hive/tmp</value>
</property>
执行替换
%s#${system:java.io.tmpdir}#/mnt/data/hive/tmp#g

<property>
    <name>system:user.name</name>
    <value>${user.name}</value>
</property>
执行替换
%s#${system:user.name}#hadoop#g

<property>
    <name>hive.metastore.uris</name>
    <value>thrift://bigdata02:9083,thrift://bigdata03:9083</value>
    <description>Thrift URI for the remote metastore. Used by metastore client to connect to remote metastore.</description>
</property>


# 修改bigdata02和bigdata03
[hadoop@bigdata02 conf]$ vim hive-site.xml
<property>
    <name>javax.jdo.option.ConnectionDriverName</name>
    <value>com.mysql.cj.jdbc.Driver</value>
    <description>Driver class name for a JDBC metastore</description>
 </property>
<property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://bigdata01:3306/hive?createDatabaseIfNotExist=true&amp;useSSL=false</value>
    <description>
      JDBC connect string for a JDBC metastore.
      To use SSL to encrypt/authenticate the connection, provide database-specific SSL flag in the connection URL.
      For example, jdbc:postgresql://myhost/db?ssl=true for postgres database.
    </description>
 </property>
<property>
    <name>javax.jdo.option.ConnectionUserName</name>
    <value>hive</value>
    <description>Username to use against metastore database</description>
 </property>
<property>
    <name>javax.jdo.option.ConnectionPassword</name>
    <value>bigdata01_hive</value>
    <description>password to use against metastore database</description>
</property>

```

#### 六、配置log目录
```Bash
[hadoop@bigdata01 conf]$ sudo mkdir -p /mnt/data/hive/tmp
[hadoop@bigdata01 conf]$ sudo chown -R hadoop:hadoop /mnt/data/hive/tmp
[wangyandong@bigdata01 ~]$ sudo cp mysql-connector-java-8.0.12.jar /mnt/opt/hive-2.3.3/lib/
[wangyandong@bigdata01 ~]$ sudo chown -R hadoop:hadoop /mnt/opt/hive-2.3.3/lib/mysql-connector-java-8.0.12.jar

[hadoop@bigdata01 conf]$ vim hive-log4j2.properties
property.hive.log.dir = /mnt/log/hive
[hadoop@bigdata01 conf]$ sudo mkdir -p /mnt/log/hive
[hadoop@bigdata01 conf]$ sudo chown -R hadoop:hadoop /mnt/log/hive
```

#### 七、初始化hive数据源mysql数据库
```Bash
# bigdata01执行
[hadoop@bigdata01 bin]$ ./schematool -initSchema -dbType mysql
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/mnt/opt/hive-2.3.3/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/mnt/opt/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Metastore connection URL:	 jdbc:mysql://bigdata01:3306/hive?createDatabaseIfNotExist=true&useSSL=false
Metastore Connection Driver :	 com.mysql.cj.jdbc.Driver
Metastore connection User:	 hive
Starting metastore schema initialization to 2.3.0
Initialization script hive-schema-2.3.0.mysql.sql
Initialization script completed
schemaTool completed


mysql> use hive;
mysql> show tables;
+---------------------------+
| Tables_in_hive            |
+---------------------------+
| aux_table                 |
| bucketing_cols            |
| cds                       |
| columns_v2                |
| compaction_queue          |
| completed_compactions     |
| completed_txn_components  |
| database_params           |
| db_privs                  |
| dbs                       |
| delegation_tokens         |
| func_ru                   |
| funcs                     |
| global_privs              |
| hive_locks                |
| idxs                      |
| index_params              |
| key_constraints           |
| master_keys               |
| next_compaction_queue_id  |
| next_lock_id              |
| next_txn_id               |
| notification_log          |
| notification_sequence     |
| nucleus_tables            |
| part_col_privs            |
| part_col_stats            |
| part_privs                |
| partition_events          |
| partition_key_vals        |
| partition_keys            |
| partition_params          |
| partitions                |
| role_map                  |
| roles                     |
| sd_params                 |
| sds                       |
| sequence_table            |
| serde_params              |
| serdes                    |
| skewed_col_names          |
| skewed_col_value_loc_map  |
| skewed_string_list        |
| skewed_string_list_values |
| skewed_values             |
| sort_cols                 |
| tab_col_stats             |
| table_params              |
| tbl_col_privs             |
| tbl_privs                 |
| tbls                      |
| txn_components            |
| txns                      |
| type_fields               |
| types                     |
| version                   |
| write_set                 |
+---------------------------+
57 rows in set (0.00 sec)
```

#### 八、启动hiveserver2和metastore
```Bash
# 启动hiveserver2
[hadoop@bigdata01 bin]$ ./hiveserver2 &
# 启动metastore
[hadoop@bigdata02 bin]$ ./hive --service metastore &
[hadoop@bigdata03 bin]$ ./hive --service metastore &
```

#### 八、连接hive
```Bash
[hadoop@bigdata03 bin]$ ./beeline -u jdbc:hive2://bigdata01:10000
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/mnt/opt/hive-2.3.3/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/mnt/opt/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Connecting to jdbc:hive2://bigdata01:10000
Connected to: Apache Hive (version 2.3.3)
Driver: Hive JDBC (version 2.3.3)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 2.3.3 by Apache Hive
0: jdbc:hive2://bigdata01:10000>

# 指定用户
[hadoop@bigdata03 bin]$ ./beeline -u jdbc:hive2://bigdata01:10000 hadoop hadoop
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/mnt/opt/hive-2.3.3/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/mnt/opt/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Connecting to jdbc:hive2://bigdata01:10000
Connected to: Apache Hive (version 2.3.3)
Driver: Hive JDBC (version 2.3.3)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 2.3.3 by Apache Hive
0: jdbc:hive2://bigdata01:10000> show tables;
0: jdbc:hive2://bigdata01:10000> !quit
Closing: 0: jdbc:hive2://bigdata01:10000


[hadoop@bigdata03 bin]$ ./beeline --color=true --fastConnect=true
```

#### 九、hive web页面
![hive web](https://github.com/Dongzai1005/learning/blob/master/bigdata/src/main/java/wang/xiaoluobo/hive/images/hive01.png)
