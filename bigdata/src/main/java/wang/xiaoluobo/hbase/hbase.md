# [hbase](http://hbase.apache.org/book.html)

### 一、hbase单机配置(version2.1.1)
1. hbase环境变量
    ```Bash
    $ vim .bash_profile
    export HBASE_HOME=/Users/wyd/soft/hbase-2.1.1
    export HBASE_CONF_DIR=$HBASE_HOME/conf
    export HBASE_CLASS_PATH=$HBASE_CONF_DIR
    export PATH=$PATH:$HBASE_HOME/bin
    $ source .bash_profile
    ```
2. 启动/关闭 hbase
    1. 启动 hbase
        ```text
        $ ./bin/start-hbase.sh
        SLF4J: Class path contains multiple SLF4J bindings.
        SLF4J: Found binding in [jar:file:/Users/wyd/soft/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
        SLF4J: Found binding in [jar:file:/Users/wyd/soft/hbase-2.1.1/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
        SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
        SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
        running master, logging to /Users/wyd/soft/hbase-2.1.1/logs/hbase-wyd-master-wangyandong.local.out
        SLF4J: Class path contains multiple SLF4J bindings.
        SLF4J: Found binding in [jar:file:/Users/wyd/soft/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
        SLF4J: Found binding in [jar:file:/Users/wyd/soft/hbase-2.1.1/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
        SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
        SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
        ```
     2. 关闭 hbase
        ```bash
        [hadoop@hadoop01 hbase-2.1.2]$ ./bin/stop-hbase.sh
        stopping hbase............
        SLF4J: Class path contains multiple SLF4J bindings.
        SLF4J: Found binding in [jar:file:/mnt/opt/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
        SLF4J: Found binding in [jar:file:/mnt/opt/hbase-2.1.2/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
        SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
        SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
        [hadoop@hadoop01 hbase-2.1.2]$ jps
        26054 NameNode
        887 Jps
        26263 SecondaryNameNode
        26427 ResourceManager
        ```   
3. [web ui](http://127.0.0.1:16010)


### 二、hbase command
- 获取某个命令详细信息
> help 'list'
- 查询服务器状态
> status
- 查询hbase版本
> version
- 查看所有表
> list
- 创建表
> create 'member', 'member_id', 'address', 'info'
- 查看表的描述
> describe 'member'
- 添加一个列族
> alter 'member', 'id'
- 删除一个列族
> alter 'member', {NAME => 'member_id', METHOD => 'delete'}
- 添加数据
> put 'member', 'debugo', 'address:city', 'beijing'
- 查询数据
> get 'member', 'Elvis', 'info:age'
- 删除数据
> delete 'member', 'Elvis', 'info:age'
- 删除指定rowkey数据
> deleteall 'member', 'Elvis'
- 统计表记录数
> count 'member'
- 查询rowkey(Sariel),列族(info)数据
> get 'member', 'Sariel', 'info'
- 查询表所有数据
> scan 'member'
- 扫描整个info列族
> scan 'member', {COLUMN=>'info'}
- 扫描列族info中的birthday列
> scan 'member', {COLUMNS=> 'info:birthday'}
- 除了列（COLUMNS）修饰词外，HBase还支持Limit（限制查询结果行数），STARTROW（ROWKEY起始行。会先根据这个key定位到region，再向后扫描）、STOPROW(结束行)、TIMERANGE（限定时间戳范围）、VERSIONS（版本数）、和FILTER（按条件过滤行）等。比如我们从Sariel这个rowkey开始，找下一个行的最新版本
> scan 'member', {STARTROW => 'Sariel', LIMIT=>1, VERSIONS=>1}
- Filter是一个非常强大的修饰词，可以设定一系列条件来进行过滤
> scan 'member', {FILTER => "(ValueFilter (>=, 'binary:26'))"}
- 引导如何使用表引用的命令
> table_help
- hbase用户详细信息
> whoami
- 禁用表
> disable 'test'
- 启用表
> enable 'test'
- 表是否禁用
> is_disabled 'member'
- 表是否启用
> is_enabled 'test'
- 禁用以t开头的表
> disable_all 't.*' 
- 判断表是否存在
> exists 'test'
- 删除表(删表之前需要将表禁用disabled)
> drop 'emp'
- 删除以t开头的表
> drop_all ‘t.*’
- 截断表
> truncate 'test'


### 三、hbase security
- #### grant
```text
hbase(main):020:0> help 'grant'
Grant users specific rights.
Syntax: grant <user or @group>, <permissions> [, <table> [, <column family> [, <column qualifier>]]]
Syntax: grant <user or @group>, <permissions>, <@namespace>

permissions is either zero or more letters from the set "RWXCA".
READ('R'), WRITE('W'), EXEC('X'), CREATE('C'), ADMIN('A')

Note: Groups and users are granted access in the same way, but groups are prefixed with an '@'
      character. Tables and namespaces are specified the same way, but namespaces are
      prefixed with an '@' character.

For example:

    hbase> grant 'bobsmith', 'RWXCA'
    hbase> grant '@admins', 'RWXCA'
    hbase> grant 'bobsmith', 'RWXCA', '@ns1'
    hbase> grant 'bobsmith', 'RW', 't1', 'f1', 'col1'
    hbase> grant 'bobsmith', 'RW', 'ns1:t1', 'f1', 'col1'
```

- #### revoke
```text
hbase(main):021:0> help 'revoke'
Revoke a user's access rights.
Syntax: revoke <user or @group> [, <table> [, <column family> [, <column qualifier>]]]
Syntax: revoke <user or @group>, <@namespace>

Note: Groups and users access are revoked in the same way, but groups are prefixed with an '@'
      character. Tables and namespaces are specified the same way, but namespaces are
      prefixed with an '@' character.

For example:

    hbase> revoke 'bobsmith'
    hbase> revoke '@admins'
    hbase> revoke 'bobsmith', '@ns1'
    hbase> revoke 'bobsmith', 't1', 'f1', 'col1'
    hbase> revoke 'bobsmith', 'ns1:t1', 'f1', 'col1'
```

- #### user_permission
```text
hbase(main):019:0> help 'user_permission'
Show all permissions for the particular user.
Syntax : user_permission <table>

Note: A namespace must always precede with '@' character.

For example:

    hbase> user_permission
    hbase> user_permission '@ns1'
    hbase> user_permission '@.*'
    hbase> user_permission '@^[a-c].*'
    hbase> user_permission 'table1'
    hbase> user_permission 'namespace1:table1'
    hbase> user_permission '.*'
    hbase> user_permission '^[A-C].*'
```


### 四、Training
```text
hbase(main):053:0> help 'list'
List all user tables in hbase. Optional regular expression parameter could
be used to filter the output. Examples:

  hbase> list
  hbase> list 'abc.*'
  hbase> list 'ns:abc.*'
  hbase> list 'ns:.*'

hbase(main):055:0> status
1 active master, 0 backup masters, 1 servers, 0 dead, 4.0000 average load
Took 0.3400 seconds

hbase(main):056:0> version
2.1.1, rb60a92d6864ef27295027f5961cb46f9162d7637, Fri Oct 26 19:27:03 PDT 2018
Took 0.0006 seconds
hbase(main):057:0> list
TABLE
scores
test
2 row(s)
Took 0.0116 seconds
=> ["scores", "test"]

hbase(main):003:0> create 'member', 'member_id', 'address', 'info'
Created table member
Took 1.1838 seconds
=> Hbase::Table - member

hbase(main):004:0> describe 'member'
Table member is ENABLED
member
COLUMN FAMILIES DESCRIPTION
{NAME => 'address', VERSIONS => '1', EVICT_BLOCKS_ON_CLOSE => 'false', NEW_VERSION_BEHAVIOR => 'false', KEEP_DELETED_CELLS => 'FALSE', CACHE_DATA_ON_WRITE => 'false', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', MIN_VERSION
S => '0', REPLICATION_SCOPE => '0', BLOOMFILTER => 'ROW', CACHE_INDEX_ON_WRITE => 'false', IN_MEMORY => 'false', CACHE_BLOOMS_ON_WRITE => 'false', PREFETCH_BLOCKS_ON_OPEN => 'false', COMPRESSION => 'NONE', BLOCKCACHE => 'true',
 BLOCKSIZE => '65536'}
{NAME => 'info', VERSIONS => '1', EVICT_BLOCKS_ON_CLOSE => 'false', NEW_VERSION_BEHAVIOR => 'false', KEEP_DELETED_CELLS => 'FALSE', CACHE_DATA_ON_WRITE => 'false', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', MIN_VERSIONS =
> '0', REPLICATION_SCOPE => '0', BLOOMFILTER => 'ROW', CACHE_INDEX_ON_WRITE => 'false', IN_MEMORY => 'false', CACHE_BLOOMS_ON_WRITE => 'false', PREFETCH_BLOCKS_ON_OPEN => 'false', COMPRESSION => 'NONE', BLOCKCACHE => 'true', BL
OCKSIZE => '65536'}
{NAME => 'member_id', VERSIONS => '1', EVICT_BLOCKS_ON_CLOSE => 'false', NEW_VERSION_BEHAVIOR => 'false', KEEP_DELETED_CELLS => 'FALSE', CACHE_DATA_ON_WRITE => 'false', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', MIN_VERSI
ONS => '0', REPLICATION_SCOPE => '0', BLOOMFILTER => 'ROW', CACHE_INDEX_ON_WRITE => 'false', IN_MEMORY => 'false', CACHE_BLOOMS_ON_WRITE => 'false', PREFETCH_BLOCKS_ON_OPEN => 'false', COMPRESSION => 'NONE', BLOCKCACHE => 'true
', BLOCKSIZE => '65536'}
3 row(s)
Took 0.1536 seconds

# 添加一个列族
hbase(main):005:0> alter 'member', 'id'
Updating all regions with the new schema...
1/1 regions updated.
Done.
Took 1.7759 seconds

# 删除一个列族
hbase(main):007:0> alter 'member', {NAME => 'member_id', METHOD => 'delete'}
Updating all regions with the new schema...
1/1 regions updated.
Done.
Took 1.8472 seconds

hbase(main):017:0> put 'member', 'debugo','info:age','27'
Took 0.1761 seconds
hbase(main):018:0> put 'member', 'debugo','id','11'
Took 0.0022 seconds
hbase(main):019:0> put 'member', 'debugo','info:birthday','1987-04-04'
Took 0.0068 seconds
hbase(main):020:0> put 'member', 'debugo','info:industry', 'it'
Took 0.0076 seconds
hbase(main):021:0> put 'member', 'debugo','address:city','beijing'
Took 0.0033 seconds
hbase(main):022:0> put 'member', 'debugo','address:country','china'
Took 0.0029 seconds
hbase(main):023:0> put 'member', 'Sariel', 'id', '21'
Took 0.0019 seconds
hbase(main):024:0> put 'member', 'Sariel','info:age', '26'
Took 0.0023 seconds
hbase(main):025:0> put 'member', 'Sariel','info:birthday', '1988-05-09 '
Took 0.0180 seconds
hbase(main):026:0> put 'member', 'Sariel','info:industry', 'it'
Took 0.0023 seconds
hbase(main):027:0> put 'member', 'Sariel','address:city', 'beijing'
Took 0.0025 seconds
hbase(main):028:0> put 'member', 'Sariel','address:country', 'china'
Took 0.0021 seconds
hbase(main):029:0> put 'member', 'Elvis', 'id', '22'
Took 0.0098 seconds
hbase(main):030:0> put 'member', 'Elvis','info:age', '26'
Took 0.0025 seconds
hbase(main):031:0> put 'member', 'Elvis','info:birthday', '1988-09-14 '
Took 0.0020 seconds
hbase(main):032:0> put 'member', 'Elvis','info:industry', 'it'
Took 0.0029 seconds
hbase(main):033:0> put 'member', 'Elvis','address:city', 'beijing'
Took 0.0021 seconds
hbase(main):034:0> put 'member', 'Elvis','address:country', 'china'
Took 0.0019 seconds

hbase(main):035:0> count 'member'
3 row(s)
Took 0.0622 seconds
=> 3

hbase(main):036:0> get 'member', 'Sariel', 'info'
COLUMN                                                    CELL
 info:age                                                 timestamp=1546839804829, value=26
 info:birthday                                            timestamp=1546839808885, value=1988-05-09
 info:industry                                            timestamp=1546839813254, value=it
1 row(s)
Took 0.0491 seconds

hbase(main):037:0> scan 'member', {COLUMN=>'info'}
ROW                                                       COLUMN+CELL
 Elvis                                                    column=info:age, timestamp=1546839830048, value=26
 Elvis                                                    column=info:birthday, timestamp=1546839834567, value=1988-09-14
 Elvis                                                    column=info:industry, timestamp=1546839841696, value=it
 Sariel                                                   column=info:age, timestamp=1546839804829, value=26
 Sariel                                                   column=info:birthday, timestamp=1546839808885, value=1988-05-09
 Sariel                                                   column=info:industry, timestamp=1546839813254, value=it
 debugo                                                   column=info:age, timestamp=1546839772386, value=27
 debugo                                                   column=info:birthday, timestamp=1546839785185, value=1987-04-04
 debugo                                                   column=info:industry, timestamp=1546839789045, value=it
3 row(s)
Took 0.0623 seconds

hbase(main):038:0> scan 'member', {COLUMNS=> 'info:birthday'}
ROW                                                       COLUMN+CELL
 Elvis                                                    column=info:birthday, timestamp=1546839834567, value=1988-09-14
 Sariel                                                   column=info:birthday, timestamp=1546839808885, value=1988-05-09
 debugo                                                   column=info:birthday, timestamp=1546839785185, value=1987-04-04
3 row(s)
Took 0.0228 seconds

hbase(main):039:0> scan 'member', { STARTROW => 'Sariel', LIMIT=>1, VERSIONS=>1}
ROW                                                       COLUMN+CELL
 Sariel                                                   column=address:city, timestamp=1546839817714, value=beijing
 Sariel                                                   column=address:country, timestamp=1546839821811, value=china
 Sariel                                                   column=id:, timestamp=1546839800903, value=21
 Sariel                                                   column=info:age, timestamp=1546839804829, value=26
 Sariel                                                   column=info:birthday, timestamp=1546839808885, value=1988-05-09
 Sariel                                                   column=info:industry, timestamp=1546839813254, value=it
1 row(s)
Took 0.0436 seconds

hbase(main):061:0> is_disabled 'member'
false
Took 0.0921 seconds
=> 1

hbase(main):068:0> is_enabled 'test'
true
Took 0.0136 seconds
=> true

hbase(main):069:0> disable_all 't.*'
test

Disable the above 1 tables (y/n)?
y
1 tables successfully disabled
Took 4.6620 seconds

hbase(main):001:0> exists 'test'
Table test does exist
Took 0.4137 seconds
=> true

```


### 五、[hbase集群部署(version2.1.2)](http://hbase.apache.org/book.html)
##### 1. 集群环境
| host | ip | node | 
| :-------- | :--------------| :------------ |
| bigdata01 | 172.16.18.13   | HMaster       |
| bigdata02 | 172.16.18.14   | HRegionServer |
| bigdata03 | 172.16.18.15   | HRegionServer |

##### 1. 创建用户和用户组(三台机器都需要配置)
```bash
[root@hadoop01 mnt]# sudo visudo
hadoop ALL=(ALL) NOPASSWD: ALL
[root@hadoop01 mnt]# cat /etc/group | grep hadoop
[root@hadoop01 mnt]# cat /etc/passwd | grep hadoop
[root@hadoop01 mnt]# groupadd hadoop
[root@hadoop01 mnt]# useradd -d /home/hadoop -g hadoop -m hadoop

[hadoop@hadoop01 .ssh]$ touch hadoop02.pub hadoop03.pub
[hadoop@hadoop01 .ssh]$ ls
id_rsa  id_rsa.pub  hadoop02.pub  hadoop03.pub
[hadoop@hadoop01 .ssh]$ vim hadoop02.pub
[hadoop@hadoop01 .ssh]$ vim hadoop03.pub
[hadoop@hadoop01 .ssh]$ ls
id_rsa  id_rsa.pub  hadoop02.pub  hadoop03.pub
[hadoop@hadoop01 .ssh]$ cat *.pub > authorized_keys
[hadoop@hadoop01 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAvnSuG9uaUGs228CZ68B4FO1ZH07LtrUEMbKMGMtXB6//IHNHlDvsL1ACrTOAhnruhh9+D50Cano1CEsDUWPuPF8V0JUIS54SnbKRaq3qptgxQZbWITuf58JoCOrQiI8HRA9/hFkagGXcUt0jSKrV7FB4fsDjL85nj+hAnQD7d5LEYC45jRl75zn3+rv2JSvrGEnFLMFnsmI6n+b1vRy7mtoXv2quScdXQNwd3YqmiN2koRKFKdtBnOTwpB5FdHz1Fk9CD6un3I7BL3DaiXqnPoPqLmxq7cM4Boxyypb/pavshbDJJrOIaN7g3WjGKbCWoZe7yNJWLF5ulbAN02LNNw== hadoop@hadoop01
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAsrL3uTX9WvrcyRItmCCgSl+kSSk1qlSuAKxWSQ1H32T78ufihu9o+kXAU1CaY1DkxH4Z4yqG49GmfJeo7gkWhtNalHfU079yVBcg0hpIEUCIW41LR1h5Bqr4GhXsC61sID961plzH5OOC7HrPQqB8W46FMZ30SXLSvL/wuOq140MHoU6wa8ozQMmpm0merLYB9tfTxLsoEAZYi7LUn9xL7yByWKY6VwXw+iNOvTw5RwQNvT2pN3Dhk7SnxczV2HRG4qDIyr2tBNr1CCnt2xoJCyrtb70tAkDbUfmrnpKlloWOXko9d9e7X9T/93yNsIiBugTWa39xvn9xWPG8Z/G+w== hadoop@hadoop02
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAt99d94W99tsNNT0fywCMLJ40zKUN535Vj66Fgy+esUY/07mV0Oo5iUDPe6NK7gVLS6yXyfxnMHsBisheGU68IIUXVg6Sw6qOtX3kMaOmuYtT+tl9WodJ4JHEVOEZ2BHi1I7h6S4K5UX7iEj+DeTnV7KzAKurZkjN5j9vW7NPlM9CQlq2kQElWnGCe/nnJ4w+uZdn7UarkyXrVC0pqGsrLvodX8mWZcMdjvwnfNltsuFgQ66oH2SeBFgOBVOU+BNA8k+slcGKKtxm055za+b0ZSqnL3I71PN6MTwe8alvXY6msh/mYeahITYORYLUwvFgOxZSOX61jWX/H1sGtXSIAw== hadoop@hadoop03

[hadoop@hadoop01 .ssh]$ chmod 600 ~/.ssh/authorized_keys

[hadoop@hadoop01 .ssh]$ ssh hadoop03
```

##### 2. [zk3.4.13集群](../zookeeper/zookeeper.md)

##### 3. [hadoop2.8.4集群](../hadoop/hadoop.md)

##### 4. 环境变量
```bash
$ vim /home/hadoop/.bash_profile
export HBASE_HOME=/mnt/opt/hbase-2.1.2
export HBASE_CONF_DIR=$HBASE_HOME/conf
export HBASE_CLASS_PATH=HADOOP_CONF_DIR
export PATH=$PATH:$HBASE_HOME/bin

export HADOOP_CONF_DIR=/mnt/opt/hadoop-2.8.4/etc/hadoop
export PATH=$PATH:$HADOOP_CONF_DIR/bin
export HADOOP_USER_NAME=hadoop
export HADOOP_HOME=/mnt/opt/hadoop-2.8.4
export PATH=$PATH:$HADOOP_HOME/bin

export HBASE_LIBRARY_PATH=$HBASE_LIBRARY_PATH:$HBASE_HOME/lib/native/Linux-amd64-64/:/usr/local/lib/
$ source /home/hadoop/.bash_profile
```

##### 5. hbase config
1. hbase-site.xml
    ```text
    [hadoop@hadoop01 hbase-2.1.2]$ mkdir -p /mnt/data/hbase
    [hadoop@hadoop01 hbase-2.1.2]$ mkdir -p /mnt/data/hbase/zookeeper
    [hadoop@hadoop01 conf]$ vim hbase-site.xml
    <configuration>
    <!--
      <property>
        <name>hbase.rootdir</name>
        <value>file:///mnt/data/hbase</value>
      </property>
    -->
      <property>
        <name>hbase.rootdir</name>
        <value>hdfs://hadoop01:9000/hbase</value>
      </property>
      <property> 
        <name>hbase.cluster.distributed</name> 
        <value>true</value>
      </property>
      <property>
        <name>hbase.zookeeper.quorum</name>
        <!--<value>172.16.18.13,172.16.18.14,172.16.18.15</value>-->
        <value>hadoop01,hadoop02,hadoop03</value>
      </property>
      <property>
        <name>hbase.zookeeper.property.dataDir</name>
        <value>/mnt/data/hbase/zookeeper</value>
      </property>
      <property>
        <name>zookeeper.session.timeout</name>
        <value>120000</value>
      </property>
      <property>
        <name>hbase.zookeeper.property.tickTime</name>
        <value>6000</value>
      </property>
      <property>
        <name>hbase.coprocessor.abortonerror</name>
        <value>false</value>
      </property>
    </configuration>
    ```

2. regionservers
    ```text
    [hadoop@hadoop01 conf]$ vim regionservers
    hadoop02
    hadoop03
    ```
3. hbase-env.sh
    ```text
    export HBASE_MANAGES_ZK=false
    
    export HBASE_LIBRARY_PATH=$HBASE_LIBRARY_PATH:$HBASE_HOME/lib/native/Linux-amd64-64/:/usr/local/lib/
    ```

##### 6. 启动hbase
```bash
# hadoop01执行
[hadoop@hadoop01 hbase-2.1.2]$ ./bin/start-hbase.sh

# 单独关闭regionserver
[hadoop@hadoop02 hbase-2.1.2]$ ./bin/hbase-daemon.sh stop regionserver
[hadoop@hadoop02 hbase-2.1.2]$ ./bin/hbase-daemon.sh start regionserver
```

##### 7. hbase进程
```bash
[hadoop@hadoop01 hbase-2.1.2]$ jps
17056 Jps
16144 HMaster
26054 NameNode
26263 SecondaryNameNode
26427 ResourceManager
[hadoop@hadoop02 hbase-2.1.2]$ jps
17056 Jps
16144 HMaster
26054 NameNode
26263 SecondaryNameNode
26427 ResourceManager
[hadoop@hadoop03 hbase-2.1.2]$ jps
31553 HRegionServer
17780 NodeManager
17662 DataNode
31934 Jps


[hadoop@hadoop01 hbase-2.1.2]$ ps -ef|grep hbase
hadoop    7494     1  0 Jan18 ?        00:00:00 bash /mnt/opt/hbase-2.1.2/bin/hbase-daemon.sh --config /mnt/opt/hbase-2.1.2/conf foreground_start master
hadoop    7508  7494  0 Jan18 ?        00:12:00 /mnt/jdk8/bin/java -Dproc_master -XX:OnOutOfMemoryError=kill -9 %p -XX:+UseConcMarkSweepGC -Dhbase.log.dir=/mnt/opt/hbase-2.1.2/logs -Dhbase.log.file=hbase-hadoop-master-hadoop01.log -Dhbase.home.dir=/mnt/opt/hbase-2.1.2 -Dhbase.id.str=hadoop -Dhbase.root.logger=INFO,RFA -Dhbase.security.logger=INFO,RFAS org.apache.hadoop.hbase.master.HMaster start

[hadoop@hadoop02 hbase-2.1.2]$ ps -ef|grep hbase
hadoop    6000     1  0 Jan18 ?        00:00:00 bash /mnt/opt/hbase-2.1.2/bin/hbase-daemon.sh --config /mnt/opt/hbase-2.1.2/conf foreground_start regionserver
hadoop    6014  6000  0 Jan18 ?        00:13:09 /usr/java/default/bin/java -Dproc_regionserver -XX:OnOutOfMemoryError=kill -9 %p -XX:+UseConcMarkSweepGC -Dhbase.log.dir=/mnt/opt/hbase-2.1.2/bin/../logs -Dhbase.log.file=hbase-hadoop-regionserver-hadoop02.log -Dhbase.home.dir=/mnt/opt/hbase-2.1.2/bin/.. -Dhbase.id.str=hadoop -Dhbase.root.logger=INFO,RFA -Dhbase.security.logger=INFO,RFAS org.apache.hadoop.hbase.regionserver.HRegionServer start

[hadoop@hadoop03 hbase-2.1.2]$ ps -ef|grep hbase
hadoop    6182     1  0 Jan18 ?        00:00:00 bash /mnt/opt/hbase-2.1.2/bin/hbase-daemon.sh --config /mnt/opt/hbase-2.1.2/conf foreground_start regionserver
hadoop    6196  6182  0 Jan18 ?        00:13:51 /usr/java/default/bin/java -Dproc_regionserver -XX:OnOutOfMemoryError=kill -9 %p -XX:+UseConcMarkSweepGC -Dhbase.log.dir=/mnt/opt/hbase-2.1.2/bin/../logs -Dhbase.log.file=hbase-hadoop-regionserver-hadoop03.log -Dhbase.home.dir=/mnt/opt/hbase-2.1.2/bin/.. -Dhbase.id.str=hadoop -Dhbase.root.logger=INFO,RFA -Dhbase.security.logger=INFO,RFAS org.apache.hadoop.hbase.regionserver.HRegionServer start
```

##### 8. hbase shell
```text
[hadoop@hadoop01 hbase-2.1.2]$ ./bin/hbase shell
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/mnt/opt/hadoop-2.8.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/mnt/opt/hbase-2.1.2/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
HBase Shell
Use "help" to get list of supported commands.
Use "exit" to quit this interactive shell.
For Reference, please visit: http://hbase.apache.org/2.0/book.html#shell
Version 2.1.2, r1dfc418f77801fbfb59a125756891b9100c1fc6d, Sun Dec 30 21:45:09 PST 2018
Took 0.0033 seconds
hbase(main):001:0> version
2.1.2, r1dfc418f77801fbfb59a125756891b9100c1fc6d, Sun Dec 30 21:45:09 PST 2018
Took 0.0006 seconds
hbase(main):002:0> status
1 active master, 0 backup masters, 2 servers, 0 dead, 1.5000 average load
Took 0.6482 seconds
hbase(main):003:0> create 'member', 'member_id', 'address', 'info'
Created table member
Took 0.7969 seconds
=> Hbase::Table - member
hbase(main):004:0> list
TABLE
member
t
2 row(s)
Took 0.0246 seconds
=> ["member", "t"]
```

### 六、hbase单机部署(version1.3.1)
```Bash
[root@bz39 data1]# wget http://apache.claz.org/hbase/1.3.1/hbase-1.3.1-bin.tar.gz
[root@bz39 data1]# tar -zxvf hbase-1.3.1-bin.tar.gz
[root@bz39 data1]# mv hbase-1.3.1/ hbase
[root@bz39 data1]# mkdir -p /data1/hbase/data


[root@bz39 conf]# vi hbase-site.xml
[root@bz39 conf]# vi hbase-env.sh
[root@bz39 conf]# vi hadoop-metrics2-hbase.properties
[root@bz39 conf]# echo 'export PATH=$PATH:/data1/hbase/bin' >> /etc/profile
[root@bz39 conf]# source /etc/profile

[root@bz39 bin]# ./start-hbase.sh
starting master, logging to /data1/hbase/bin/../logs/hbase-root-master-bz39.out
[root@bz39 bin]# ./stop-hbase.sh
stopping hbase....................


[root@bz39 conf]# echo '-A INPUT -p tcp -m tcp --dport 16010 -j ACCEPT' >> /etc/sysconfig/iptables
[root@bz39 conf]# service iptables restart

# HBase Web UI:
# http://114.215.131.39:16010

[root@bz39 bin]# ./hbase shell
hbase(main):001:0> status
1 active master, 0 backup masters, 1 servers, 0 dead, 2.0000 average load

hbase(main):002:0> version
1.3.1, r930b9a55528fe45d8edce7af42fef2d35e77677a, Thu Apr  6 19:36:54 PDT 2017

hbase(main):003:0> help 'list'
List all tables in hbase. Optional regular expression parameter could
be used to filter the output. Examples:

  hbase> list
  hbase> list 'abc.*'
  hbase> list 'ns:abc.*'
  hbase> list 'ns:.*'
hbase(main):004:0> list
TABLE                                                                                     
0 row(s) in 0.0480 seconds

=> []
hbase(main):005:0> create_namespace 'test_ns'
0 row(s) in 0.9110 seconds

hbase(main):006:0> drop_namespace 'test_ns'
0 row(s) in 0.8800 seconds

hbase(main):007:0> create_namespace 'test_ns'
0 row(s) in 0.8670 seconds

hbase(main):008:0> describe_namespace 'test_ns'
DESCRIPTION                                                                               
{NAME => 'test_ns'}                                                                       
1 row(s) in 0.0180 seconds

hbase(main):009:0> list_namespace
NAMESPACE                                                                                 
default                                                                                   
hbase                                                                                     
test_ns                                                                                   
3 row(s) in 0.0250 seconds

hbase(main):010:0> create_namespace 'ns1', {'hbase.namespace.quota.maxtables'=>'5'}
0 row(s) in 0.8660 seconds

hbase(main):006:0> alter_namespace 'ns2', {METHOD => 'set', 'hbase.namespace.quota.maxtables'=>'8'}
0 row(s) in 0.6310 seconds

hbase(main):008:0> list_namespace
NAMESPACE                                                                                 
default                                                                                   
hbase                                                                                     
ns1                                                                                       
ns2                                                                                       
test_ns                                                                                   
5 row(s) in 0.0320 seconds

hbase(main):009:0> list
TABLE                                                                                     
0 row(s) in 0.0140 seconds

=> []
hbase(main):010:0> create 'user','info'
0 row(s) in 1.3010 seconds

=> Hbase::Table - user
hbase(main):011:0> list
TABLE                                                                                     
user                                                                                      
1 row(s) in 0.0070 seconds

=> ["user"]
hbase(main):012:0> drop 'user'

ERROR: Table user is enabled. Disable it first.

Here is some help for this command:
Drop the named table. Table must first be disabled:
  hbase> drop 't1'
  hbase> drop 'ns1:t1'


hbase(main):013:0> disable 'user'
0 row(s) in 2.2570 seconds

hbase(main):014:0> drop 'user'
0 row(s) in 1.2540 seconds

hbase(main):019:0> create 'ns1:user', 'info'
0 row(s) in 1.2310 seconds

=> Hbase::Table - ns1:user
hbase(main):020:0> list
TABLE                                                                                     
ns1:user                                                                                  
1 row(s) in 0.0070 seconds

=> ["ns1:user"]
hbase(main):021:0> list_namespace_tables 'ns1'
TABLE                                                                                     
user                                                                                      
1 row(s) in 0.0340 seconds

hbase(main):023:0> exists 'user'
Table user does not exist                                                                 
0 row(s) in 0.0120 seconds

hbase(main):025:0> is_enabled 'ns1:user'
true                                                                                      
0 row(s) in 0.0130 seconds

hbase(main):034:0> create 'scores','grade', 'course'
0 row(s) in 1.2390 seconds

=> Hbase::Table - scores
hbase(main):035:0> put 'scores','Tom','grade:','5'

hbase(main):036:0> put 'scores','Tom','course:math','97'
0 row(s) in 0.0040 seconds

hbase(main):037:0> put 'scores','Tom','course:art','87'
0 row(s) in 0.0040 seconds

hbase(main):038:0> put 'scores','Jim','grade','4'
0 row(s) in 0.0040 seconds

hbase(main):039:0> put 'scores','Jim','course:','89'
0 row(s) in 0.0040 seconds

hbase(main):040:0> put 'scores','Jim','course:','80'
0 row(s) in 0.0060 seconds

hbase(main):044:0> get 'scores','Jim'
COLUMN                  CELL                                                              
 course:                timestamp=1498189023097, value=80                                 
 grade:                 timestamp=1498189021524, value=4                                  
1 row(s) in 0.0160 seconds

hbase(main):045:0> get 'scores','Jim','grade'
COLUMN                  CELL                                                              
 grade:                 timestamp=1498189021524, value=4                                  
1 row(s) in 0.0080 seconds

hbase(main):049:0> scan 'scores'
ROW                     COLUMN+CELL                                                       
 Jim                    column=course:, timestamp=1498189023097, value=80                 
 Jim                    column=grade:, timestamp=1498189021524, value=4                   
 Tom                    column=course:art, timestamp=1498189021506, value=87              
 Tom                    column=course:math, timestamp=1498189021489, value=97             
 Tom                    column=grade:, timestamp=1498189021457, value=5                   
2 row(s) in 0.0240 seconds

hbase(main):050:0> delete 'scores', 'Jim','grade'
0 row(s) in 0.0230 seconds

hbase(main):052:0> scan 'scores'
ROW                     COLUMN+CELL                                                       
 Jim                    column=course:, timestamp=1498189023097, value=80                 
 Tom                    column=course:art, timestamp=1498189021506, value=87              
 Tom                    column=course:math, timestamp=1498189021489, value=97             
 Tom                    column=grade:, timestamp=1498189021457, value=5                   
2 row(s) in 0.0120 seconds

hbase(main):054:0> get 'scores','Jim'
COLUMN                  CELL                                                              
 course:                timestamp=1498189023097, value=80                                 
1 row(s) in 0.0050 seconds

hbase(main):056:0> count 'scores'
2 row(s) in 0.0150 seconds

=> 2

hbase(main):057:0> truncate 'scores'
Truncating 'scores' table (it may take a while):
 - Disabling table...
 - Truncating table...
0 row(s) in 3.3770 seconds
```

```Bash
$ ./bin/hbase shell
2019-01-06 16:23:01,711 WARN  [main] util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
HBase Shell
Use "help" to get list of supported commands.
Use "exit" to quit this interactive shell.
For Reference, please visit: http://hbase.apache.org/2.0/book.html#shell
Version 2.1.1, rb60a92d6864ef27295027f5961cb46f9162d7637, Fri Oct 26 19:27:03 PDT 2018
Took 0.0050 seconds                                                                                                                                                                                         
hbase(main):001:0> version
2.1.1, rb60a92d6864ef27295027f5961cb46f9162d7637, Fri Oct 26 19:27:03 PDT 2018
Took 0.0008 seconds     

hbase(main):001:0> create 'test','cf'
Created table test
Took 1.1777 seconds
=> Hbase::Table - test

hbase(main):002:0> list 'test'
TABLE
test
1 row(s)
Took 0.1015 seconds
=> ["test"]

hbase(main):003:0> describe 'test'
Table test is ENABLED
test
COLUMN FAMILIES DESCRIPTION
{NAME => 'cf', VERSIONS => '1', EVICT_BLOCKS_ON_CLOSE => 'false', NEW_VERSION_BEHAVIOR => 'false', KEEP_DELETED_CELLS => 'FALSE', CACHE_DATA_ON_WRITE => 'false', DATA_BLOCK_ENCODING => 'NONE', TTL => 'FOREVER', MIN_VERSIONS =>
'0', REPLICATION_SCOPE => '0', BLOOMFILTER => 'ROW', CACHE_INDEX_ON_WRITE => 'false', IN_MEMORY => 'false', CACHE_BLOOMS_ON_WRITE => 'false', PREFETCH_BLOCKS_ON_OPEN => 'false', COMPRESSION => 'NONE', BLOCKCACHE => 'true', BLOC
KSIZE => '65536'}
1 row(s)
Took 0.1974 seconds

hbase(main):004:0> put 'test', 'row1', 'cf:a', 'value1'
Took 0.1779 seconds
hbase(main):005:0> put 'test', 'row2', 'cf:b', 'value2'
Took 0.0123 seconds
hbase(main):006:0> put 'test', 'row3', 'cf:c', 'value3'
Took 0.0124 seconds
hbase(main):007:0> scan 'test'
ROW                                                       COLUMN+CELL
 row1                                                     column=cf:a, timestamp=1546825144794, value=value1
 row2                                                     column=cf:b, timestamp=1546825155049, value=value2
 row3                                                     column=cf:c, timestamp=1546825160959, value=value3
3 row(s)
Took 0.0451 seconds

hbase(main):008:0> get 'test', 'row1'
COLUMN                                                    CELL
 cf:a                                                     timestamp=1546825144794, value=value1
1 row(s)
Took 0.0205 seconds
hbase(main):009:0> disable 'test'
Took 0.5092 seconds
hbase(main):010:0> enable 'test'
Took 0.7813 seconds

hbase(main):011:0> drop 'test'
0 row(s) in 0.1370 seconds

hbase(main):035:0> count 'test'
3 row(s)
Took 0.0079 seconds
=> 3


hbase(main):022:0* create 'scores','grade', 'course'
Created table scores
Took 0.8110 seconds
=> Hbase::Table - scores
hbase(main):023:0> put 'scores','Tom','grade:','5'
Took 0.0232 seconds
hbase(main):024:0> put 'scores','Tom','course:math','97'
Took 0.0082 seconds
hbase(main):025:0> put 'scores','Tom','course:art','87'
Took 0.0114 seconds
hbase(main):026:0> put 'scores','Jim','grade','4'
Took 0.0030 seconds
hbase(main):027:0> put 'scores','Jim','course:','89'
Took 0.0027 seconds
hbase(main):028:0> put 'scores','Jim','course:','80'
Took 0.0048 seconds
hbase(main):029:0> scan 'scores'
ROW                                                       COLUMN+CELL
 Jim                                                      column=course:, timestamp=1546830230460, value=80
 Jim                                                      column=grade:, timestamp=1546830229491, value=4
 Tom                                                      column=course:art, timestamp=1546830229436, value=87
 Tom                                                      column=course:math, timestamp=1546830229394, value=97
 Tom                                                      column=grade:, timestamp=1546830229359, value=5
2 row(s)
Took 0.0141 seconds
hbase(main):030:0> get 'scores','Jim'
COLUMN                                                    CELL
 course:                                                  timestamp=1546830230460, value=80
 grade:                                                   timestamp=1546830229491, value=4
1 row(s)
Took 0.0101 seconds
hbase(main):031:0>
hbase(main):032:0* get 'scores','Jim','grade'
COLUMN                                                    CELL
 grade:                                                   timestamp=1546830229491, value=4
1 row(s)
Took 0.0039 seconds
hbase(main):033:0>
hbase(main):034:0* count 'scores'
2 row(s)
Took 0.1565 seconds
=> 2


hbase(main):046:0* disable 'scores'
Took 0.0068 seconds
hbase(main):047:0> alter 'scores',NAME=>'info'
Updating all regions with the new schema...
All regions updated.
Done.
Took 1.2923 seconds
hbase(main):048:0> enable 'scores'
Took 0.7453 seconds
```


### 七、hbase安装问题处理
1. WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable  

    - [1) 参考hadoop处理](../hadoop/hadoop.md)
    - [2) 安装snappy](http://blog.cheyo.net/197.html)

    ```text
    [hadoop@hadoop01 hbase-2.1.2]$ ./bin/hbase --config ~/conf_hbase org.apache.hadoop.util.NativeLibraryChecker
    2019-01-21 08:36:46,118 WARN  [main] util.NativeCodeLoader (NativeCodeLoader.java:<clinit>(62)) - Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
    Native library checking:
    hadoop:  false
    zlib:    false
    snappy:  false
    lz4:     false
    bzip2:   false
    openssl: false
    2019-01-21 08:36:46,335 INFO  [main] util.ExitUtil (ExitUtil.java:terminate(124)) - Exiting with status 1
    
    # 安装snappy
    [root@hadoop01 opt]# yum -y install snappy snappy-devel
    [root@hadoop01 opt]# cp /usr/lib64/libsnapp* /usr/local/lib
    
    # 配置maven
    [root@hadoop01 opt]# wget http://ftp.kddilabs.jp/infosystems/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
    [root@hadoop01 opt]# vim /etc/profile
    [root@hadoop01 opt]# source /etc/profile
    [root@hadoop01 opt]# echo $MAVEN_HOME
    /mnt/opt/maven-3.6.0
    export MAVEN_HOME=/mnt/opt/maven-3.6.0
    export PATH=$PATH:$MAVEN_HOME/bin

    # 打包
    [root@hadoop01 opt]# wget https://github.com/electrum/hadoop-snappy/archive/master.zip
    [root@hadoop01 hadoop-snappy-master]# mvn clean package
    [hadoop@hadoop01 hadoop-snappy-master]$ cd target/
    [hadoop@hadoop01 target]# tar -zxvf hadoop-snappy-0.0.1-SNAPSHOT.tar.gz
    
    # 拷贝jar包
    [hadoop@hadoop03 target]$ mkdir -p /mnt/opt/hbase-2.1.2/lib/native/Linux-amd64-64
    [hadoop@hadoop01 target]$ cp hadoop-snappy-0.0.1-SNAPSHOT/lib/native/Linux-amd64-64/lib* /mnt/opt/hbase-2.1.2/lib/native/Linux-amd64-64/
    [hadoop@hadoop01 target]$ cp hadoop-snappy-0.0.1-SNAPSHOT/lib/hadoop-snappy-0.0.1-SNAPSHOT.jar /mnt/opt/hbase-2.1.2/lib/
    
    # 配置环境变量
    [hadoop@hadoop03 ~]$ vim .bash_profile
    export HBASE_LIBRARY_PATH=$HBASE_LIBRARY_PATH:$HBASE_HOME/lib/native/Linux-amd64-64/:/usr/local/lib/
    [hadoop@hadoop03 ~]$ source .bash_profile
 
    # 重启hbase
    [hadoop@hadoop01 hbase-2.1.2]$ ./bin/start-hbase.sh
    running master, logging to /mnt/opt/hbase-2.1.2/logs/hbase-hadoop-master-hadoop01.out
    hadoop03: running regionserver, logging to /mnt/opt/hbase-2.1.2/bin/../logs/hbase-hadoop-regionserver-hadoop03.out
    hadoop02: running regionserver, logging to /mnt/opt/hbase-2.1.2/bin/../logs/hbase-hadoop-regionserver-hadoop02.out
    ```

2. Caused by: java.lang.ClassNotFoundException: org.apache.htrace.SamplerBuilder
    ```bash
    # 缺少jar包
    $ cp $HBASE_HOME/lib/client-facing-thirdparty/htrace-core-3.1.0-incubating.jar $HBASE_HOME/lib/
    ```
