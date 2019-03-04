# redis version 4.0.9

## 单点部署
```Bash
$ wget http://download.redis.io/releases/redis-4.0.9.tar.gz
$ tar xzf redis-4.0.9.tar.gz
$ cd redis-4.0.9
$ make

# 启动redis
$ src/redis-server

$ src/redis-cli
redis> set foo bar
OK
redis> get foo
"bar"
```

## redis 集群部署(3master 3slave)
### 1. 集群环境
```text
hadoop01   172.16.18.13    master 6001     slave 6002
hadoop02   172.16.18.14    master 6001     slave 6002
hadoop03   172.16.18.15    master 6001     slave 6002
```

### 2. redis 依赖
1. yum -y install cpp binutils glibc glibc-kernheaders glibc-common glibc-devel
2. yum -y install ruby ruby-devel rubygems rpm-build
3. [tcl](https://sourceforge.net/projects/tcl/files/latest/download)
    ```bash
    [root@hadoop01 tcl8.6.9]# cd unix
    [root@hadoop01 unix]# ./configure
    [root@hadoop01 unix]# make && make install
    ```
4. [ruby](https://cache.ruby-lang.org/pub/ruby/2.6/ruby-2.6.1.tar.gz)
    ```bash
    [root@hadoop01 opt]# cd ruby-2.6.1/
    [root@hadoop01 ruby-2.6.1]# ./configure --prefix=/usr/local/ruby
    [root@hadoop01 ruby-2.6.1]# make && make install
    [root@hadoop01 ruby-2.6.1]# vim /etc/profile
    export PATH=/usr/local/ruby/bin:$PATH
    [root@hadoop01 ruby-2.6.1]# source /etc/profile
    ```
5. [rubygems](https://rubygems.org/rubygems/rubygems-3.0.2.tgz)
    ```bash
    [root@hadoop01 opt]# cd rubygems-3.0.2/
    [root@hadoop01 rubygems-3.0.2]# ruby setup.rb
    [root@hadoop01 rubygems-3.0.2]# gem -v
    3.0.2
    [root@hadoop01 src]# gem install redis
    ```

### 3. redis 集群部署(三个节点执行)
1. redis 配置
    ```text
    [root@hadoop01 redis-4.0.11]# mkdir -p cluster/6001 cluster/6002 /mnt/data/redis/6001 /mnt/data/redis/6002 /mnt/log/redis/6001 /mnt/log/redis/6002
    [root@hadoop01 cluster]# tree
    .
    ├── 6001
    │   └── redis.conf
    └── 6002
        └── redis.conf
    
    # 需要修改的属性名称：
    port 6001
    daemonize yes
    pidfile /var/run/redis_6001.pid
    bind 172.16.18.13
    logfile /mnt/log/redis/6001/redis.log
    dir /mnt/data/redis/6001
    appendonly yes
    cluster-enabled yes
    cluster-config-file nodes-6001.conf
    cluster-node-timeout 15000
    ```

2. 编译 redis
    ```bash
    [root@hadoop01 redis-4.0.11]# make
    [root@hadoop01 redis-4.0.11]# cd src/
    [root@hadoop01 src]# make test
    [root@hadoop01 src]# make install
    ```

3. 启动 redis cluster
    ```text
    # 先启动 master 节点，再启动 slave 节点
    [root@hadoop01 src]# nohup ./redis-server ../cluster/6001/redis.conf &
    [root@hadoop01 src]# nohup ./redis-server ../cluster/6002/redis.conf &
    
 
    # 没有启动 cluster，无法分配 slot
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 6001
    172.16.18.13:6001> set a a
    (error) CLUSTERDOWN Hash slot not served
    172.16.18.13:6001> quit
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 6002
    172.16.18.13:6002> set a a
    (error) CLUSTERDOWN Hash slot not served
    172.16.18.13:6002> quit
 
 
    # 启动 redis cluster
    [root@hadoop01 src]# ./redis-trib.rb create --replicas 1 172.16.18.13:6001 172.16.18.13:6002 172.16.18.14:6001 172.16.18.14:6002 172.16.18.15:6001 172.16.18.15:6002
    >>> Creating cluster
    >>> Performing hash slots allocation on 6 nodes...
    Using 3 masters:
    172.16.18.13:6001
    172.16.18.14:6001
    172.16.18.15:6001
    Adding replica 172.16.18.14:6002 to 172.16.18.13:6001
    Adding replica 172.16.18.15:6002 to 172.16.18.14:6001
    Adding replica 172.16.18.13:6002 to 172.16.18.15:6001
    M: 64576c35515ffa550a4e577b64bd0e2263ac0c0b 172.16.18.13:6001
       slots:0-5460 (5461 slots) master
    S: 05a1cd45ab7a06f5ee47c09b91efdb57aacde096 172.16.18.13:6002
       replicates cab84231a2284e849d901f0f2bc1c5d529c01a9a
    M: 3ed10ec788309966ed400fada16ce104733e705d 172.16.18.14:6001
       slots:5461-10922 (5462 slots) master
    S: d35960e731bcac17c6e16681aff050afe2f564bb 172.16.18.14:6002
       replicates 64576c35515ffa550a4e577b64bd0e2263ac0c0b
    M: cab84231a2284e849d901f0f2bc1c5d529c01a9a 172.16.18.15:6001
       slots:10923-16383 (5461 slots) master
    S: ea47903c96592d595214d1d2c27503dbea5dd5f9 172.16.18.15:6002
       replicates 3ed10ec788309966ed400fada16ce104733e705d
    Can I set the above configuration? (type 'yes' to accept): yes
    >>> Nodes configuration updated
    >>> Assign a different config epoch to each node
    >>> Sending CLUSTER MEET messages to join the cluster
    Waiting for the cluster to join...
    >>> Performing Cluster Check (using node 172.16.18.13:6001)
    M: 64576c35515ffa550a4e577b64bd0e2263ac0c0b 172.16.18.13:6001
       slots:0-5460 (5461 slots) master
       1 additional replica(s)
    S: d35960e731bcac17c6e16681aff050afe2f564bb 172.16.18.14:6002
       slots: (0 slots) slave
       replicates 64576c35515ffa550a4e577b64bd0e2263ac0c0b
    M: 3ed10ec788309966ed400fada16ce104733e705d 172.16.18.14:6001
       slots:5461-10922 (5462 slots) master
       1 additional replica(s)
    S: ea47903c96592d595214d1d2c27503dbea5dd5f9 172.16.18.15:6002
       slots: (0 slots) slave
       replicates 3ed10ec788309966ed400fada16ce104733e705d
    M: cab84231a2284e849d901f0f2bc1c5d529c01a9a 172.16.18.15:6001
       slots:10923-16383 (5461 slots) master
       1 additional replica(s)
    S: 05a1cd45ab7a06f5ee47c09b91efdb57aacde096 172.16.18.13:6002
       slots: (0 slots) slave
       replicates cab84231a2284e849d901f0f2bc1c5d529c01a9a
    [OK] All nodes agree about slots configuration.
    >>> Check for open slots...
    >>> Check slots coverage...
    [OK] All 16384 slots covered.

    
    # 验证 redis cluster
    [root@hadoop01 src]# ./redis-cli -c -h 172.16.18.13 -p 6001
    172.16.18.13:6001> set a a
    -> Redirected to slot [15495] located at 172.16.18.15:6001
    OK
    
    [root@hadoop03 src]# ./redis-cli -c -h 172.16.18.15 -p 6001
    172.16.18.15:6001> get a
    "a"
    ```

4. redis-trib command
    ```text
    # http://weizijun.cn/2016/01/08/redis%20cluster%E7%AE%A1%E7%90%86%E5%B7%A5%E5%85%B7redis-trib-rb%E8%AF%A6%E8%A7%A3/
    [root@hadoop01 src]# ruby redis-trib.rb help
    Usage: redis-trib <command> <options> <arguments ...>
    
      create          host1:port1 ... hostN:portN
                      --replicas <arg>
      check           host:port
      info            host:port
      fix             host:port
                      --timeout <arg>
      reshard         host:port
                      --from <arg>
                      --to <arg>
                      --slots <arg>
                      --yes
                      --timeout <arg>
                      --pipeline <arg>
      rebalance       host:port
                      --weight <arg>
                      --auto-weights
                      --use-empty-masters
                      --timeout <arg>
                      --simulate
                      --pipeline <arg>
                      --threshold <arg>
      add-node        new_host:new_port existing_host:existing_port
                      --slave
                      --master-id <arg>
      del-node        host:port node_id
      set-timeout     host:port milliseconds
      call            host:port command arg arg .. arg
      import          host:port
                      --from <arg>
                      --copy
                      --replace
      help            (show this help)
    
    For check, fix, reshard, del-node, set-timeout you can specify the host and port of any working node in the cluster.
    ```


## 哨兵部署(1master 2slave 3sentinel)
1. redis sentinel 环境(redis 安装参考 redis cluster)
```text
hadoop01   172.16.18.13    master 6379     sentinel 26379
hadoop02   172.16.18.14    slave  6379     sentinel 26379
hadoop03   172.16.18.15    slave  6379     sentinel 26379
```

2. redis 配置
    ```text
    [root@hadoop01 redis-4.0.11]# mkdir -p sentinel /mnt/log/redis/6379 /mnt/data/redis/6379 /mnt/log/redis/26379
    [root@hadoop01 sentinel]# tree
    .
    ├── redis.conf
    └── sentinel.conf
    ```

3. 启动 master-slave 节点
    ```text
    # 先启动 master 节点再启动 slave 节点
    [root@hadoop01 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 6379 -a 123456
    Warning: Using a password with '-a' option on the command line interface may not be safe.
    172.16.18.13:6379> select 0
    OK
    172.16.18.13:6379> set a a
    OK
    
    [root@hadoop02 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop02 src]# ./redis-cli -h 172.16.18.14 -p 6379 -a 123456
    Warning: Using a password with '-a' option on the command line interface may not be safe.
    172.16.18.14:6379> get a
    "a"
 
    [root@hadoop03 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop03 src]# ./redis-cli -h 172.16.18.15 -p 6379 -a 123456
    Warning: Using a password with '-a' option on the command line interface may not be safe.
    172.16.18.15:6379> get a
    "a"
    
 
    # master-slave 环境切换测试
    # 切换 hadoop02 为主，并将 hadoop01 停机
    [root@hadoop02 src]# ./redis-cli -h 172.16.18.14 -p 6379 -a 123456
    Warning: Using a password with '-a' option on the command line interface may not be safe.
    172.16.18.14:6379> slaveof NO ONE
    OK
    172.16.18.14:6379> set a c
    OK
    172.16.18.14:6379> get a
    "c"
    172.16.18.14:6379> slaveof 172.16.18.13 6379
    OK
    
    # hadoop01 宕机状态下，hadoop02 切换 hadoop01 为主后，set a c 这条数据丢了
    [root@hadoop01 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 6379 -a 123456
    Warning: Using a password with '-a' option on the command line interface may not be safe.
    172.16.18.13:6379> get a
    "b"
    ```

4. 启动 sentinel(三个节点执行)
    ```text
    [root@hadoop03 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop03 src]# ./redis-cli -h 172.16.18.15 -p 6379 -a 123456
    Warning: Using a password with '-a' option on the command line interface may not be safe.
    172.16.18.15:6379> select 0
    OK
    172.16.18.15:6379> get a
    "a"
    
    [root@hadoop01 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop01 src]# ./redis-sentinel ../sentinel/sentinel.conf
    [root@hadoop02 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop02 src]# ./redis-sentinel ../sentinel/sentinel.conf
    [root@hadoop03 src]# ./redis-server ../sentinel/redis.conf
    [root@hadoop03 src]# ./redis-sentinel ../sentinel/sentinel.conf
    ```

5. 查看 redis 哨兵节点信息
    ```text
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 26379
    172.16.18.13:26379> sentinel masters
    1)  1) "name"
        2) "redis-master"
        3) "ip"
        4) "172.16.18.13"
        5) "port"
        6) "6379"
        7) "runid"
        8) "7ccfe199ae71e339e951413a204f05fef62039fc"
        9) "flags"
       10) "master"
       11) "link-pending-commands"
       12) "0"
       13) "link-refcount"
       14) "1"
       15) "last-ping-sent"
       16) "0"
       17) "last-ok-ping-reply"
       18) "826"
       19) "last-ping-reply"
       20) "826"
       21) "down-after-milliseconds"
       22) "1500"
       23) "info-refresh"
       24) "6843"
       25) "role-reported"
       26) "master"
       27) "role-reported-time"
       28) "2002916"
       29) "config-epoch"
       30) "0"
       31) "num-slaves"
       32) "2"
       33) "num-other-sentinels"
       34) "2"
       35) "quorum"
       36) "2"
       37) "failover-timeout"
       38) "10000"
       39) "parallel-syncs"
       40) "1"
    172.16.18.13:26379> sentinel slaves redis-master
    1)  1) "name"
        2) "172.16.18.15:6379"
        3) "ip"
        4) "172.16.18.15"
        5) "port"
        6) "6379"
        7) "runid"
        8) "a3e54ad34e437e3af238fb5899020d398d7a676a"
        9) "flags"
       10) "slave"
       11) "link-pending-commands"
       12) "0"
       13) "link-refcount"
       14) "1"
       15) "last-ping-sent"
       16) "0"
       17) "last-ok-ping-reply"
       18) "500"
       19) "last-ping-reply"
       20) "500"
       21) "down-after-milliseconds"
       22) "1500"
       23) "info-refresh"
       24) "3431"
       25) "role-reported"
       26) "slave"
       27) "role-reported-time"
       28) "2020215"
       29) "master-link-down-time"
       30) "0"
       31) "master-link-status"
       32) "ok"
       33) "master-host"
       34) "172.16.18.13"
       35) "master-port"
       36) "6379"
       37) "slave-priority"
       38) "100"
       39) "slave-repl-offset"
       40) "78069"
    2)  1) "name"
        2) "172.16.18.14:6379"
        3) "ip"
        4) "172.16.18.14"
        5) "port"
        6) "6379"
        7) "runid"
        8) "bae84ae8a3d25c25119621f818468a5da8801881"
        9) "flags"
       10) "slave"
       11) "link-pending-commands"
       12) "0"
       13) "link-refcount"
       14) "1"
       15) "last-ping-sent"
       16) "0"
       17) "last-ok-ping-reply"
       18) "500"
       19) "last-ping-reply"
       20) "500"
       21) "down-after-milliseconds"
       22) "1500"
       23) "info-refresh"
       24) "4089"
       25) "role-reported"
       26) "slave"
       27) "role-reported-time"
       28) "936296"
       29) "master-link-down-time"
       30) "0"
       31) "master-link-status"
       32) "ok"
       33) "master-host"
       34) "172.16.18.13"
       35) "master-port"
       36) "6379"
       37) "slave-priority"
       38) "100"
       39) "slave-repl-offset"
       40) "77926"
       
    # master-slave 节点切换测试
    172.16.18.13:26379> sentinel get-master-addr-by-name redis-master
    1) "172.16.18.13"
    2) "6379"
    172.16.18.13:26379> sentinel failover redis-master
    OK
    172.16.18.13:26379> sentinel get-master-addr-by-name redis-master
    1) "172.16.18.15"
    2) "6379"
    
    
    172.16.18.13:26379> sentinel slaves redis-master
    1)  1) "name"
        2) "172.16.18.13:6379"
        3) "ip"
        4) "172.16.18.13"
        5) "port"
        6) "6379"
        7) "runid"
        8) "7ccfe199ae71e339e951413a204f05fef62039fc"
        9) "flags"
       10) "slave"
       11) "link-pending-commands"
       12) "0"
       13) "link-refcount"
       14) "1"
       15) "last-ping-sent"
       16) "0"
       17) "last-ok-ping-reply"
       18) "769"
       19) "last-ping-reply"
       20) "769"
       21) "down-after-milliseconds"
       22) "1500"
       23) "info-refresh"
       24) "4999"
       25) "role-reported"
       26) "slave"
       27) "role-reported-time"
       28) "45161"
       29) "master-link-down-time"
       30) "0"
       31) "master-link-status"
       32) "ok"
       33) "master-host"
       34) "172.16.18.15"
       35) "master-port"
       36) "6379"
       37) "slave-priority"
       38) "100"
       39) "slave-repl-offset"
       40) "111524"
    2)  1) "name"
        2) "172.16.18.14:6379"
        3) "ip"
        4) "172.16.18.14"
        5) "port"
        6) "6379"
        7) "runid"
        8) "bae84ae8a3d25c25119621f818468a5da8801881"
        9) "flags"
       10) "slave"
       11) "link-pending-commands"
       12) "0"
       13) "link-refcount"
       14) "1"
       15) "last-ping-sent"
       16) "0"
       17) "last-ok-ping-reply"
       18) "267"
       19) "last-ping-reply"
       20) "267"
       21) "down-after-milliseconds"
       22) "1500"
       23) "info-refresh"
       24) "4999"
       25) "role-reported"
       26) "slave"
       27) "role-reported-time"
       28) "55305"
       29) "master-link-down-time"
       30) "0"
       31) "master-link-status"
       32) "ok"
       33) "master-host"
       34) "172.16.18.15"
       35) "master-port"
       36) "6379"
       37) "slave-priority"
       38) "100"
       39) "slave-repl-offset"
       40) "111524"
    
    # hadoop02 和 hadoop03 redis sentinel 启动前
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 26379 info sentinel
    # Sentinel
    sentinel_masters:1
    sentinel_tilt:0
    sentinel_running_scripts:0
    sentinel_scripts_queue_length:0
    sentinel_simulate_failure_flags:0
    master0:name=redis-master,status=ok,address=172.16.18.13:6379,slaves=2,sentinels=1
    
    # hadoop02 和 hadoop03 redis sentinel 启动后
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 26379 info sentinel
    # Sentinel
    sentinel_masters:1
    sentinel_tilt:0
    sentinel_running_scripts:0
    sentinel_scripts_queue_length:0
    sentinel_simulate_failure_flags:0
    master0:name=redis-master,status=ok,address=172.16.18.13:6379,slaves=2,sentinels=3
    
    
    172.16.18.13:26379> info sentinel
    # Sentinel
    sentinel_masters:1
    sentinel_tilt:0
    sentinel_running_scripts:0
    sentinel_scripts_queue_length:0
    sentinel_simulate_failure_flags:0
    master0:name=redis-master,status=ok,address=172.16.18.15:6379,slaves=2,sentinels=3
    
    172.16.18.15:6379> info replication
    # Replication
    role:slave
    master_host:172.16.18.13
    master_port:6379
    master_link_status:down
    master_last_io_seconds_ago:-1
    master_sync_in_progress:0
    slave_repl_offset:20049
    master_link_down_since_seconds:295
    slave_priority:100
    slave_read_only:1
    connected_slaves:0
    master_replid:4175e22073495f3a6819310dc1e94e0055bf5c4e
    master_replid2:0000000000000000000000000000000000000000
    master_repl_offset:20049
    second_repl_offset:-1
    repl_backlog_active:1
    repl_backlog_size:1048576
    repl_backlog_first_byte_offset:1
    repl_backlog_histlen:20049
    172.16.18.15:6379>
    172.16.18.15:6379>
    172.16.18.15:6379>
    172.16.18.15:6379>
    172.16.18.15:6379> info replication
    # Replication
    role:slave
    master_host:172.16.18.13
    master_port:6379
    master_link_status:down
    master_last_io_seconds_ago:-1
    master_sync_in_progress:0
    slave_repl_offset:20049
    master_link_down_since_seconds:319
    slave_priority:100
    slave_read_only:1
    connected_slaves:0
    master_replid:4175e22073495f3a6819310dc1e94e0055bf5c4e
    master_replid2:0000000000000000000000000000000000000000
    master_repl_offset:20049
    second_repl_offset:-1
    repl_backlog_active:1
    repl_backlog_size:1048576
    repl_backlog_first_byte_offset:1
    repl_backlog_histlen:20049
    
    
    172.16.18.14:6379> info
    172.16.18.15:6379> info
    ```

6. 模拟 master 故障
    ```text
    # 当前 hadoop03 为 master 节点
    [root@hadoop03 src]# !ps
    ps -ef|grep redis
    root     17918     1  0 10:01 ?        00:00:27 ./redis-server 172.16.18.15:6001 [cluster]
    root     17927     1  0 10:02 ?        00:00:24 ./redis-server 172.16.18.15:6002 [cluster]
    root     18706     1  0 14:12 ?        00:00:05 ./redis-server 172.16.18.15:6379
    root     19128     1  0 15:34 ?        00:00:02 ./redis-sentinel 172.16.18.15:26379 [sentinel]
    root     19151 18320  0 15:45 pts/0    00:00:00 grep redis
    # kill -9 sentinel 会将其标为 odown， sentinel 不为切换 master 节点(kill -3/-QUIT 可以切换)
    [root@hadoop03 src]# kill -9 18706
    [root@hadoop03 src]# kill -QUIT 18706
    [root@hadoop03 src]# kill -3 18706
    # sentinel 将 master 切为 hadoop01 节点
    [root@hadoop01 src]# ./redis-cli -h 172.16.18.13 -p 26379
    172.16.18.13:26379> sentinel get-master-addr-by-name redis-master
    1) "172.16.18.13"
    2) "6379"
    ```
