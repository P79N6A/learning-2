# [hbase](http://hbase.apache.org/book.html)

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

http://blog.pureisle.net/archives/1887.html
http://debugo.com/hbase-shell-cmds/
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

```

