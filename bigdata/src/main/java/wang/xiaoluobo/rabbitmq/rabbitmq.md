# rabbitmq version 3.7.12
[rabbitmq-server](https://www.rabbitmq.com/install-rpm.html#downloads)

[erlang](https://github.com/rabbitmq/erlang-rpm/releases)

https://www.cnblogs.com/kevingrace/p/8012792.html
https://qupzhi.com/rabbitmqadmin/
http://soft.dog/2016/04/20/RabbitMQ-cli-rabbitmqadmin/
http://soft.dog/2016/01/13/rabbitmq-monitoring/

## rabbitmq
1. 集群环境
hadoop01,hadoop02,hadoop03

2. 软件下载

    [erlang](https://github.com/rabbitmq/erlang-rpm/releases/download/v21.2.6/erlang-21.2.6-1.el6.x86_64.rpm)

    [rabbitmq-server](https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.12/rabbitmq-server-3.7.12-1.el6.noarch.rpm)

    ```bash
    # 查看系统版本
    $ lsb_release -a
    LSB Version:	:base-4.0-amd64:base-4.0-noarch:core-4.0-amd64:core-4.0-noarch
    Distributor ID:	CentOS
    Description:	CentOS release 6.10 (Final)
    Release:	6.10
    Codename:	Final
    ```

3. 创建 rabbitmq 目录
    ```bash
    $ mkdir -p /mnt/data/rabbitmq/db /mnt/log/rabbitmq/logs
    # 会默认创建一个 rabiitmq 用户，需要把目录授权 other group write 权限
    $ chmod -R 777 /mnt/log/rabbitmq
    $ chmod -R 777 /mnt/data/rabbitmq
    ```

4. 安装 rabbitmq soft
    ```bash
    # 三台机器
    [root@hadoop01 opt]# rpm -ivh --nodeps --force erlang-21.2.6-1.el6.x86_64.rpm
    warning: erlang-21.2.6-1.el6.x86_64.rpm: Header V4 RSA/SHA1 Signature, key ID 6026dfca: NOKEY
    Preparing...                ########################################### [100%]
       1:erlang                 ########################################### [100%]
       
    [root@hadoop01 opt]# rpm -ivh --nodeps --force rabbitmq-server-3.7.12-1.el6.noarch.rpm
    warning: rabbitmq-server-3.7.12-1.el6.noarch.rpm: Header V4 RSA/SHA256 Signature, key ID 6026dfca: NOKEY
    Preparing...                ########################################### [100%]
       1:rabbitmq-server        ########################################### [100%]
    ```

5. 配置 rabbitmq 配置
    ```bash
    # 1. rabbitmq-env.conf
    $ vim /etc/rabbitmq/rabbitmq-env.conf
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
    
    # 2. rabbitmq.conifg
    $ vim /etc/rabbitmq/rabbitmq.conifg
    [
      {rabbit, [{tcp_listeners, [5672]},{vm_memory_high_watermark,0.5},{disk_free_limit, "32GB"},{cluster_partition_handling, autoheal},{cluster_keepalive_interval, 10000},{hipe_compile, true}]}
    ].
    
    [root@hadoop01 ~]# rabbitmq-plugins enable rabbitmq_management
    [root@hadoop02 ~]# rabbitmq-plugins enable rabbitmq_management
    [root@hadoop03 ~]# rabbitmq-plugins enable rabbitmq_management
    ```
    
6. 配置 rabbitmq 集群
    1. 配置 cookie
        ```bash
        # 启动三个 rabbitmq
        [root@hadoop01 opt]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
        [root@hadoop01 opt]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
        [root@hadoop01 opt]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
         
        # 同步三台机器 erlang cookie
        [root@hadoop01 opt]# cat /var/lib/rabbitmq/.erlang.cookie
        RPWIQNFOWEVXUOPNDCKX
        [root@hadoop02 opt]# chmod 600 /var/lib/rabbitmq/.erlang.cookie
        [root@hadoop02 opt]# vim /var/lib/rabbitmq/.erlang.cookie
        [root@hadoop02 opt]# chmod 400 /var/lib/rabbitmq/.erlang.cookie
        [root@hadoop02 opt]# cat /var/lib/rabbitmq/.erlang.cookie
        RPWIQNFOWEVXUOPNDCKX
        
        [root@hadoop03 opt]# chmod 600 /var/lib/rabbitmq/.erlang.cookie
        [root@hadoop03 opt]# vim /var/lib/rabbitmq/.erlang.cookie
        [root@hadoop03 opt]# chmod 400 /var/lib/rabbitmq/.erlang.cookie
        [root@hadoop03 opt]# cat /var/lib/rabbitmq/.erlang.cookie
        RPWIQNFOWEVXUOPNDCKX
    
        # 拷贝 cookie 到当前用户目录下(不拷贝提示 cookie 不一致)
        [root@hadoop01 rabbitmq]# cp .erlang.cookie ~/
        [root@hadoop02 rabbitmq]# cp .erlang.cookie ~/
        [root@hadoop03 rabbitmq]# cp .erlang.cookie ~/
        ```
        
    2. rabbitmq 用户
        ```bash
        # 创建管理用户
        $ rabbitmqctl add_user admin admin
        # 设置用户权限
        $ rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
        # 设置用户角色
        $ rabbitmqctl set_user_tags admin administrator
        # 查看用户列表
        $ rabbitmqctl list_users
        ```
        
    3. 节点 hadoop02 和 hadoop03 加入到 hadoop01
        ```bash
        # hadoop02 加入 hadoop01节点
        [root@hadoop02 rabbitmq]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
        [root@hadoop02 rabbitmq]# rabbitmqctl stop_app
        Stopping rabbit application on node rabbit@hadoop02 ...
        [root@hadoop02 rabbitmq]# rabbitmqctl join_cluster rabbit@hadoop01
        Clustering node rabbit@hadoop02 with rabbit@hadoop01
        
        [root@hadoop02 rabbitmq]# /etc/init.d/rabbitmq-server stop
        Stopping rabbitmq-server: rabbitmq-server.
        [root@hadoop02 rabbitmq]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
        
        
        # hadoop03 加入 hadoop01节点
        [root@hadoop03 rabbitmq]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
        [root@hadoop03 rabbitmq]# rabbitmqctl stop_app
        Stopping rabbit application on node rabbit@hadoop03 ...
        [root@hadoop03 rabbitmq]# rabbitmqctl join_cluster rabbit@hadoop01
        Clustering node rabbit@hadoop03 with rabbit@hadoop01
        [root@hadoop03 rabbitmq]# /etc/init.d/rabbitmq-server stop
        Stopping rabbitmq-server: rabbitmq-server.
        [root@hadoop03 rabbitmq]# /etc/init.d/rabbitmq-server start
        Starting rabbitmq-server: SUCCESS
        rabbitmq-server.
        ```

7. 验证 rabbitmq 集群(任一节点执行即可)
    ```text
    [root@hadoop01 rabbitmq]# rabbitmqctl cluster_status
    Cluster status of node rabbit@hadoop01 ...
    [{nodes,[{disc,['rabbit@hadoop01',
                    'rabbit@hadoop02',
                    'rabbit@hadoop03']}]},
     {running_nodes,['rabbit@hadoop03',
                     'rabbit@hadoop02',
                     'rabbit@hadoop01']},
     {cluster_name,<<"rabbit@hadoop01">>},
     {partitions,[]},
     {alarms,[{'rabbit@hadoop03',[]},
              {'rabbit@hadoop02',[]},
              {'rabbit@hadoop01',[]}]}]
    ```

8. 重启 rabbitmq 集群
    ```text
    # 当集群所有节点都关掉之后，必须在某一个节点上执行rabbitmqctl force_boot，然后再启动该节点。当该节点启动之后，其他节点按正常启动即可。
    [root@hadoop01 rabbitmq]# rabbitmqctl force_boot
    [root@hadoop01 rabbitmq]# /etc/init.d/rabbitmq-server start
    Starting rabbitmq-server: SUCCESS
    rabbitmq-server.
    
    [root@hadoop02 rabbitmq]# /etc/init.d/rabbitmq-server start
    Starting rabbitmq-server: SUCCESS
    rabbitmq-server.
    
    [root@hadoop03 rabbitmq]# /etc/init.d/rabbitmq-server start
    Starting rabbitmq-server: SUCCESS
    rabbitmq-server.
    
    [root@hadoop01 rabbitmq]# rabbitmqctl cluster_status
    Cluster status of node rabbit@hadoop01 ...
    [{nodes,[{disc,['rabbit@hadoop01',
                    'rabbit@hadoop02',
                    'rabbit@hadoop03']}]},
     {running_nodes,['rabbit@hadoop03',
                     'rabbit@hadoop02',
                     'rabbit@hadoop01']},
     {cluster_name,<<"rabbit@hadoop01">>},
     {partitions,[]},
     {alarms,[{'rabbit@hadoop03',[]},
              {'rabbit@hadoop02',[]},
              {'rabbit@hadoop01',[]}]}]
              
              
    [root@hadoop02 rabbitmq]# rabbitmqctl cluster_status
    Cluster status of node rabbit@hadoop02 ...
    [{nodes,[{disc,['rabbit@hadoop01',
                    'rabbit@hadoop02',
                    'rabbit@hadoop03']}]},
     {running_nodes,['rabbit@hadoop03',
                     'rabbit@hadoop01',
                     'rabbit@hadoop02']},
     {cluster_name,<<"rabbit@hadoop01">>},
     {partitions,[]},
     {alarms,[{'rabbit@hadoop03',[]},
              {'rabbit@hadoop01',[]},
              {'rabbit@hadoop02',[]}]}]
              
              
    [root@hadoop03 rabbitmq]# rabbitmqctl cluster_status
    Cluster status of node rabbit@hadoop03 ...
    [{nodes,[{disc,['rabbit@hadoop01',
                    'rabbit@hadoop02',
                    'rabbit@hadoop03']}]},
     {running_nodes,['rabbit@hadoop01',
                     'rabbit@hadoop02',
                     'rabbit@hadoop03']},
     {cluster_name,<<"rabbit@hadoop01">>},
     {partitions,[]},
     {alarms,[{'rabbit@hadoop01',[]},
              {'rabbit@hadoop02',[]},
              {'rabbit@hadoop03',[]}]}]
    ```

## rabbitmqadmin
[root@hadoop01 ~]# wget http://172.16.18.13:15672/cli/rabbitmqadmin
[root@hadoop01 ~]# chmod +x rabbitmqadmin
[root@hadoop01 ~]# mv rabbitmqadmin /usr/sbin/

```text
[root@hadoop01 ~]# rabbitmqadmin -help
Usage
=====
  rabbitmqadmin [options] subcommand

Options
=======
--help, -h              show this help message and exit
--config=CONFIG, -c CONFIG
                        configuration file [default: ~/.rabbitmqadmin.conf]
--node=NODE, -N NODE    node described in the configuration file [default:
                        'default' only if configuration file is specified]
--host=HOST, -H HOST    connect to host HOST [default: localhost]
--port=PORT, -P PORT    connect to port PORT [default: 15672]
--path-prefix=PATH_PREFIX
                        use specific URI path prefix for the RabbitMQ HTTP
                        API. /api and operation path will be appended to it.
                        (default: blank string) [default: ]
--vhost=VHOST, -V VHOST
                        connect to vhost VHOST [default: all vhosts for list,
                        '/' for declare]
--username=USERNAME, -u USERNAME
                        connect using username USERNAME [default: guest]
--password=PASSWORD, -p PASSWORD
                        connect using password PASSWORD [default: guest]
--base-uri=URI, -U URI  connect using a base HTTP API URI. /api and operation
                        path will be appended to it. Path will be ignored.
                        --vhost has to be provided separately.
--quiet, -q             suppress status messages [default: True]
--ssl, -s               connect with ssl [default: False]
--ssl-key-file=SSL_KEY_FILE
                        PEM format key file for SSL
--ssl-cert-file=SSL_CERT_FILE
                        PEM format certificate file for SSL
--ssl-ca-cert-file=SSL_CA_CERT_FILE
                        PEM format CA certificate file for SSL
--ssl-disable-hostname-verification
                        Disables peer hostname verification
--ssl-insecure, -k      Disables all SSL validations like curl's '-k' argument
--request-timeout=REQUEST_TIMEOUT, -t REQUEST_TIMEOUT
                        HTTP request timeout in seconds [default: 120]
--format=FORMAT, -f FORMAT
                        format for listing commands - one of [raw_json, long,
                        pretty_json, kvp, tsv, table, bash] [default: table]
--sort=SORT, -S SORT    sort key for listing queries
--sort-reverse, -R      reverse the sort order
--depth=DEPTH, -d DEPTH
                        maximum depth to recurse for listing tables [default:
                        1]
--bash-completion       Print bash completion script [default: False]
--version               Display version and exit

More Help
=========

For more help use the help subcommand:

  rabbitmqadmin help subcommands  # For a list of available subcommands
  rabbitmqadmin help config       # For help with the configuration file
```


[root@hadoop01 ~]# rabbitmqadmin declare queue name=test durable=true
queue declared
[root@hadoop01 ~]# rabbitmqadmin declare queue name=test durable=true
queue declared
[root@hadoop01 ~]# rabbitmqadmin list queues
+------+----------+
| name | messages |
+------+----------+
| test | 0        |
+------+----------+
[root@hadoop01 ~]# rabbitmqadmin publish routing_key=test payload="hello world"
Message published
[root@hadoop01 ~]# rabbitmqadmin list queues
+------+----------+
| name | messages |
+------+----------+
| test | 1        |
+------+----------+
[root@hadoop01 ~]# rabbitmqadmin get queue=test
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
| routing_key | exchange | message_count |   payload   | payload_bytes | payload_encoding | properties | redelivered |
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
| test        |          | 0             | hello world | 11            | string           |            | False       |
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+

# 消费消息
[root@hadoop01 ~]# rabbitmqadmin get queue=test ackmode=ack_requeue_true
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
| routing_key | exchange | message_count |   payload   | payload_bytes | payload_encoding | properties | redelivered |
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
| test        |          | 0             | hello world | 11            | string           |            | True        |
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
[root@hadoop01 ~]# rabbitmqadmin list queues
+------+----------+
| name | messages |
+------+----------+
| test | 1        |
+------+----------+
[root@hadoop01 ~]# rabbitmqadmin get queue=test ackmode=ack_requeue_false
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
| routing_key | exchange | message_count |   payload   | payload_bytes | payload_encoding | properties | redelivered |
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
| test        |          | 0             | hello world | 11            | string           |            | True        |
+-------------+----------+---------------+-------------+---------------+------------------+------------+-------------+
[root@hadoop01 ~]# rabbitmqadmin list queues
+------+----------+
| name | messages |
+------+----------+
| test | 0        |
+------+----------+


[root@hadoop01 ~]# rabbitmqadmin declare user name=test password=test tags=administrator
user declared
[root@hadoop01 ~]# rabbitmqctl change_password test 123
Changing password for user "test" ...
[root@hadoop01 ~]# rabbitmqctl change_password test test
Changing password for user "test" ...
[root@hadoop01 ~]# rabbitmqctl list_users
Listing users ...
user	tags
admin	[administrator]
guest	[administrator]
test	[administrator]


```text
[root@hadoop01 ~]# rabbitmqadmin help config
Usage
=====
rabbitmqadmin [options] subcommand

Configuration File
==================

  It is possible to specify a configuration file from the command line.
  Hosts can be configured easily in a configuration file and called
  from the command line.

Example
=======

  # rabbitmqadmin.conf.example START

  [host_normal]
  hostname = localhost
  port = 15672
  username = guest
  password = guest
  declare_vhost = / # Used as default for declare / delete only
  vhost = /         # Used as default for declare / delete / list

  [host_ssl]
  hostname = otherhost
  port = 15672
  username = guest
  password = guest
  ssl = True
  ssl_key_file = /path/to/key.pem
  ssl_cert_file = /path/to/cert.pem

  # rabbitmqadmin.conf.example END

Use
===

  rabbitmqadmin -c rabbitmqadmin.conf.example -N host_normal ...
[root@hadoop01 ~]# rabbitmqadmin help subcommands
Usage
=====
  rabbitmqadmin [options] subcommand

  where subcommand is one of:

Display
=======

  list operator_policies [<column>...]
  list users [<column>...]
  list vhost_limits [<column>...]
  list vhosts [<column>...]
  list connections [<column>...]
  list exchanges [<column>...]
  list bindings [<column>...]
  list permissions [<column>...]
  list channels [<column>...]
  list parameters [<column>...]
  list consumers [<column>...]
  list queues [<column>...]
  list policies [<column>...]
  list nodes [<column>...]
  show overview [<column>...]

Object Manipulation
===================

  declare queue name=... [node=... auto_delete=... durable=... arguments=...]
  declare vhost name=... [tracing=...]
  declare operator_policy name=... pattern=... definition=... [priority=... apply-to=...]
  declare vhost_limit name=... value=...
  declare user name=... password=... OR password_hash=... tags=... [hashing_algorithm=...]
  declare exchange name=... type=... [auto_delete=... internal=... durable=... arguments=...]
  declare policy name=... pattern=... definition=... [priority=... apply-to=...]
  declare parameter component=... name=... value=...
  declare permission vhost=... user=... configure=... write=... read=...
  declare binding source=... destination=... [arguments=... routing_key=... destination_type=...]
  delete queue name=...
  delete vhost name=...
  delete operator_policy name=...
  delete vhost_limit name=...
  delete user name=...
  delete exchange name=...
  delete policy name=...
  delete parameter component=... name=...
  delete permission vhost=... user=...
  delete binding source=... destination_type=... destination=... properties_key=...
  close connection name=...
  purge queue name=...

Broker Definitions
==================

  export <file>
  import <file>

Publishing and Consuming
========================

  publish routing_key=... [exchange=... payload=... payload_encoding=... properties=...]
  get queue=... [count=... payload_file=... ackmode=... encoding=...]

  * If payload is not specified on publish, standard input is used

  * If payload_file is not specified on get, the payload will be shown on
    standard output along with the message metadata

  * If payload_file is specified on get, count must not be set
```
  
[root@hadoop01 ~]# rabbitmqadmin list policies
No items
[root@hadoop01 ~]# rabbitmqadmin list nodes
+-----------------+------+----------+
|       name      | type | mem_used |
+-----------------+------+----------+
| rabbit@hadoop01 | disc | 95371264 |
| rabbit@hadoop02 | disc | 91648000 |
| rabbit@hadoop03 | disc | 92979200 |
+-----------------+------+----------+
[root@hadoop01 ~]# rabbitmqadmin show overview
+------------------+-----------------+-----------------------+----------------------+
| rabbitmq_version |  cluster_name   | queue_totals.messages | object_totals.queues |
+------------------+-----------------+-----------------------+----------------------+
| 3.7.12           | rabbit@hadoop01 | 0                     | 1                    |
+------------------+-----------------+-----------------------+----------------------+


web ui http://127.0.0.1:15672


## 安装遇到的问题
1. ERROR: node with name "rabbit" already running on "hadoop02"
> kill 掉 rabbitmq 所有进程，重启 rabbitmq
