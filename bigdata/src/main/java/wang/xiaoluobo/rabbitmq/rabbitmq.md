# rabbitmq version 3.7.12
https://www.rabbitmq.com/install-rpm.html#downloads

erlang rpm
https://github.com/rabbitmq/erlang-rpm/releases

https://www.cnblogs.com/kevingrace/p/8012792.html
http://gavinxyj.com/2017/02/14/rabbitmq-build/

[root@iss-half-ali-f-app-001-18-13 opt]# rpm -ivh --nodeps --force erlang-21.2.6-1.el6.x86_64.rpm
warning: erlang-21.2.6-1.el6.x86_64.rpm: Header V4 RSA/SHA1 Signature, key ID 6026dfca: NOKEY
Preparing...                ########################################### [100%]
   1:erlang                 ########################################### [100%]
   
[root@iss-half-ali-f-app-001-18-13 opt]# rpm -ivh --nodeps --force rabbitmq-server-3.7.12-1.el6.noarch.rpm
warning: rabbitmq-server-3.7.12-1.el6.noarch.rpm: Header V4 RSA/SHA256 Signature, key ID 6026dfca: NOKEY
Preparing...                ########################################### [100%]
   1:rabbitmq-server        ########################################### [100%]



mkdir -p /mnt/data/rabbitmq/db
mkdir -p /mnt/log/rabbitmq/logs
mkdir -p /mnt/opt/rabbitmq
chmod 777 /mnt/opt/rabbitmq


[root@iss-half-ali-f-app-001-18-13 opt]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.

[root@iss-half-ali-f-app-001-18-13 opt]# ll /var/lib/rabbitmq/.erlang.cookie
-r-------- 1 rabbitmq rabbitmq 20 Feb 27 00:00 /var/lib/rabbitmq/.erlang.cookie
[root@iss-half-ali-f-app-001-18-13 opt]# cat /var/lib/rabbitmq/.erlang.cookie
RPWIQNFOWEVXUOPNDCKX[root@iss-half-ali-f-app-001-18-13 opt]# chmod 600 /var/lib/rabbitmq/.erlang.cookie
[root@iss-half-ali-f-app-001-18-13 opt]# vim /var/lib/rabbitmq/.erlang.cookie
[root@iss-half-ali-f-app-001-18-13 opt]# chmod 400 /var/lib/rabbitmq/.erlang.cookie
[root@iss-half-ali-f-app-001-18-13 opt]# cat /var/lib/rabbitmq/.erlang.cookie
RPWIQNFOWEVXUOPNDCKX


[root@iss-half-ali-f-app-001-18-13 opt]# rabbitmqctl stop_app
Stopping rabbit application on node rabbit@iss-half-ali-f-app-001-18-13 ...
[hadoop@iss-half-ali-f-app-001-18-13 opt]$ rabbitmqctl join_cluster --ram rabbit@iss-half-ali-f-app-002-18-14


mkdir -p /mnt/data/rabbitmq/db /mnt/log/rabbitmq/logs
chmod -R 777 /mnt/log/rabbitmq
chmod -R 777 /mnt/data/rabbitmq


vim /etc/rabbitmq/rabbitmq-env.conf
#文件内容
#RABBITMQ_NODE_PORT=    //端口号
#HOSTNAME=
#RABBITMQ_NODENAME=mq
#配置文件的路径
RABBITMQ_CONFIG_FILE=/etc/rabbitmq/rabbitmq.config
#需要使用的MNESIA数据库的路径
RABBITMQ_MNESIA_BASE=/mnt/data/rabbitmq/db
#log的路径    
RABBITMQ_LOG_BASE=/mnt/log/rabbitmq/logs
#RABBITMQ_PLUGINS_DIR=/rabbitmq/plugins    //插件的路径


vim /etc/rabbitmq/rabbitmq.conifg
[
  {rabbit, [{tcp_listeners, [5672]},{vm_memory_high_watermark,0.5},{disk_free_limit, "32GB"},{cluster_partition_handling, autoheal},{cluster_keepalive_interval, 10000},{hipe_compile, true}]}
].


[root@iss-half-ali-f-app-001-18-13 ~]# rabbitmq-plugins enable rabbitmq_management
Enabling plugins on node rabbit@iss-half-ali-f-app-001-18-13:
rabbitmq_management
The following plugins have been configured:
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@iss-half-ali-f-app-001-18-13...
Plugin configuration unchanged.

[root@iss-half-ali-f-app-001-18-13 rabbitmq]# cp .erlang.cookie ~/


http://gavinxyj.com/2017/02/14/rabbitmq-build/

kill 掉 rabbitmq 所有进程，重启 rabbitmq
ERROR: node with name "rabbit" already running on "iss-half-ali-f-app-002-18-14"
ps -ef| grep rabbitmq
ps aux | grep epmd
ps aux | grep erl


hadoop02加入 hadoop01节点
[root@iss-half-ali-f-app-002-18-14 rabbitmq]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.
[root@iss-half-ali-f-app-002-18-14 rabbitmq]# rabbitmqctl stop_app
Stopping rabbit application on node rabbit@iss-half-ali-f-app-002-18-14 ...
[root@iss-half-ali-f-app-002-18-14 rabbitmq]# rabbitmqctl join_cluster rabbit@iss-half-ali-f-app-001-18-13
Clustering node rabbit@iss-half-ali-f-app-002-18-14 with rabbit@iss-half-ali-f-app-001-18-13

[root@iss-half-ali-f-app-002-18-14 rabbitmq]# /etc/init.d/rabbitmq-server stop
Stopping rabbitmq-server: rabbitmq-server.
[root@iss-half-ali-f-app-002-18-14 rabbitmq]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.




[root@iss-half-ali-f-app-003-18-15 rabbitmq]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.
[root@iss-half-ali-f-app-003-18-15 rabbitmq]# rabbitmqctl stop_app
Stopping rabbit application on node rabbit@iss-half-ali-f-app-003-18-15 ...
[root@iss-half-ali-f-app-003-18-15 rabbitmq]# rabbitmqctl join_cluster rabbit@iss-half-ali-f-app-001-18-13
Clustering node rabbit@iss-half-ali-f-app-003-18-15 with rabbit@iss-half-ali-f-app-001-18-13


[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]
[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]




[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]
[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-002-18-14',
                 'rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-002-18-14',[]},
          {'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]
[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-002-18-14',
                 'rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-002-18-14',[]},
          {'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]
[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-003-18-15',
                 'rabbit@iss-half-ali-f-app-002-18-14',
                 'rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-003-18-15',[]},
          {'rabbit@iss-half-ali-f-app-002-18-14',[]},
          {'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]
          
          
当集群所有节点都关掉之后，必须在某一个节点上执行rabbitmqctl force_boot，然后再启动该节点。当该节点启动之后，其他节点按正常启动即可。
[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl force_boot
[root@iss-half-ali-f-app-001-18-13 rabbitmq]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.

[root@iss-half-ali-f-app-002-18-14 rabbitmq]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.

[root@iss-half-ali-f-app-003-18-15 rabbitmq]# /etc/init.d/rabbitmq-server start
Starting rabbitmq-server: SUCCESS
rabbitmq-server.

[root@iss-half-ali-f-app-001-18-13 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-001-18-13 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-003-18-15',
                 'rabbit@iss-half-ali-f-app-002-18-14',
                 'rabbit@iss-half-ali-f-app-001-18-13']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-003-18-15',[]},
          {'rabbit@iss-half-ali-f-app-002-18-14',[]},
          {'rabbit@iss-half-ali-f-app-001-18-13',[]}]}]
          
          
[root@iss-half-ali-f-app-002-18-14 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-002-18-14 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-003-18-15',
                 'rabbit@iss-half-ali-f-app-001-18-13',
                 'rabbit@iss-half-ali-f-app-002-18-14']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-003-18-15',[]},
          {'rabbit@iss-half-ali-f-app-001-18-13',[]},
          {'rabbit@iss-half-ali-f-app-002-18-14',[]}]}]
          
          
          
[root@iss-half-ali-f-app-003-18-15 rabbitmq]# rabbitmqctl cluster_status
Cluster status of node rabbit@iss-half-ali-f-app-003-18-15 ...
[{nodes,[{disc,['rabbit@iss-half-ali-f-app-001-18-13',
                'rabbit@iss-half-ali-f-app-002-18-14',
                'rabbit@iss-half-ali-f-app-003-18-15']}]},
 {running_nodes,['rabbit@iss-half-ali-f-app-001-18-13',
                 'rabbit@iss-half-ali-f-app-002-18-14',
                 'rabbit@iss-half-ali-f-app-003-18-15']},
 {cluster_name,<<"rabbit@iss-half-ali-f-app-001-18-13">>},
 {partitions,[]},
 {alarms,[{'rabbit@iss-half-ali-f-app-001-18-13',[]},
          {'rabbit@iss-half-ali-f-app-002-18-14',[]},
          {'rabbit@iss-half-ali-f-app-003-18-15',[]}]}]
          
          
https://www.cnblogs.com/kevingrace/p/8012792.html

          