# rabbitmq version 3.7.12
[rabbitmq-server](https://www.rabbitmq.com/install-rpm.html#downloads)

[erlang](https://github.com/rabbitmq/erlang-rpm/releases)


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
    
    2. 节点 hadoop02 和 hadoop03 加入到 hadoop01
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
    
## 安装遇到的问题
1. ERROR: node with name "rabbit" already running on "hadoop02"
> kill 掉 rabbitmq 所有进程，重启 rabbitmq
