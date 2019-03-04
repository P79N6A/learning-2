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

## redis 集群部署(3主3从)
yum -y install cpp binutils glibc glibc-kernheaders glibc-common glibc-devel

tcl
https://sourceforge.net/projects/tcl/files/Tcl/8.6.9/tk8.6.9.1-src.tar.gz/download


[root@iss-half-ali-f-app-001-18-13 unix]# ./configure
[root@iss-half-ali-f-app-001-18-13 unix]# make && make install


[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# make
[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# cd src/
[root@iss-half-ali-f-app-001-18-13 src]# make test
[root@iss-half-ali-f-app-001-18-13 src]# make install
[root@iss-half-ali-f-app-001-18-13 cluster]# mkdir -p 6001 6002
[root@iss-half-ali-f-app-002-18-14 cluster]# mkdir -p 6003 6004
[root@iss-half-ali-f-app-003-18-15 cluster]# mkdir -p 6005 6006
[root@iss-half-ali-f-app-001-18-13 6001]# mkdir -p data logs conf
[root@iss-half-ali-f-app-001-18-13 cluster]# mkdir -p /mnt/log/redis/6001 /mnt/log/redis/6002
[root@iss-half-ali-f-app-001-18-13 cluster]# mkdir -p /mnt/data/redis/6001 /mnt/data/redis/6002

[root@iss-half-ali-f-app-002-18-14 6003]# mkdir -p data logs conf
[root@iss-half-ali-f-app-002-18-14 cluster]# mkdir -p /mnt/log/redis/6001 /mnt/log/redis/6002
[root@iss-half-ali-f-app-002-18-14 cluster]# mkdir -p /mnt/data/redis/6001 /mnt/data/redis/6002


[root@iss-half-ali-f-app-003-18-15 6006]# mkdir -p data logs conf
[root@iss-half-ali-f-app-003-18-15 cluster]# mkdir -p /mnt/log/redis/6001 /mnt/log/redis/6002
[root@iss-half-ali-f-app-003-18-15 cluster]# mkdir -p /mnt/data/redis/6001 /mnt/data/redis/6002


[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# yum -y install ruby ruby-devel rubygems rpm-build



port 6001
daemonize yes
pidfile /var/run/redis_6001.pid
bind 172.16.18.13
logfile /mnt/log/redis/6001/redis.log
dir /mnt/data/redis/6001
appendonly yes
cluster-enabled yes
cluster-config-file redis.conf
cluster-node-timeout 15000

https://cache.ruby-lang.org/pub/ruby/2.6/ruby-2.6.1.tar.gz
[root@iss-half-ali-f-app-001-18-13 opt]# cd ruby-2.6.1/
[root@iss-half-ali-f-app-001-18-13 ruby-2.6.1]# ./configure --prefix=/usr/local/ruby
[root@iss-half-ali-f-app-001-18-13 ruby-2.6.1]# make && make install
[root@iss-half-ali-f-app-001-18-13 ruby-2.6.1]# vim /etc/profile
export PATH=/usr/local/ruby/bin:$PATH
[root@iss-half-ali-f-app-001-18-13 ruby-2.6.1]# source /etc/profile


https://rubygems.org/rubygems/rubygems-3.0.2.tgz
[root@iss-half-ali-f-app-001-18-13 opt]# cd rubygems-3.0.2/
[root@iss-half-ali-f-app-001-18-13 rubygems-3.0.2]# ruby setup.rb
[root@iss-half-ali-f-app-001-18-13 rubygems-3.0.2]# gem -v
3.0.2


[root@iss-half-ali-f-app-001-18-13 src]# gem install redis
[root@iss-half-ali-f-app-001-18-13 src]# nohup ./redis-server ../cluster/6001/redis.conf &
[root@iss-half-ali-f-app-001-18-13 src]# nohup ./redis-server ../cluster/6002/redis.conf &

[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 6001
172.16.18.13:6001> set a a
(error) CLUSTERDOWN Hash slot not served
172.16.18.13:6001> quit
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 6002
172.16.18.13:6002> set a a
(error) CLUSTERDOWN Hash slot not served
172.16.18.13:6002> quit


[root@iss-half-ali-f-app-002-18-14 src]# ./redis-cli -h 172.16.18.14 -p 6001
172.16.18.14:6001> set a a
(error) CLUSTERDOWN Hash slot not served
172.16.18.14:6001> quit
[root@iss-half-ali-f-app-002-18-14 src]# ./redis-cli -h 172.16.18.14 -p 6002
172.16.18.14:6002> set a a
(error) CLUSTERDOWN Hash slot not served
172.16.18.14:6002> quit


[root@iss-half-ali-f-app-003-18-15 src]# ./redis-cli -h 172.16.18.15 -p 6001
172.16.18.15:6001> set a a
(error) CLUSTERDOWN Hash slot not served
172.16.18.15:6001> quit
[root@iss-half-ali-f-app-003-18-15 src]# ./redis-cli -h 172.16.18.15 -p 6002
172.16.18.15:6002> set a a
(error) CLUSTERDOWN Hash slot not served
172.16.18.15:6002> quit



[root@iss-half-ali-f-app-001-18-13 src]# ./redis-trib.rb create --replicas 1 172.16.18.13:6001 172.16.18.13:6002 172.16.18.14:6001 172.16.18.14:6002 172.16.18.15:6001 172.16.18.15:6002
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




[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -c -h 172.16.18.13 -p 6001
172.16.18.13:6001> set a a
-> Redirected to slot [15495] located at 172.16.18.15:6001
OK
172.16.18.15:6001> get a
"a"

[root@iss-half-ali-f-app-003-18-15 src]# ./redis-cli -c -h 172.16.18.13 -p 6001
172.16.18.13:6001> set a b
-> Redirected to slot [15495] located at 172.16.18.15:6001
OK
172.16.18.15:6001> get a
"b"


http://weizijun.cn/2016/01/08/redis%20cluster%E7%AE%A1%E7%90%86%E5%B7%A5%E5%85%B7redis-trib-rb%E8%AF%A6%E8%A7%A3/
[root@iss-half-ali-f-app-001-18-13 src]# ruby redis-trib.rb help
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



## 哨兵部署
https://www.cnblogs.com/kevingrace/p/9004460.html
https://www.cnblogs.com/linuxbug/p/5131504.html


172.16.18.13    master
172.16.18.14    slave
172.16.18.15    slave


[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# mkdir sentinel
[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# mkdir -p /mnt/log/redis/26379
[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# mkdir -p /mnt/log/redis/6379
[root@iss-half-ali-f-app-001-18-13 redis-4.0.11]# mkdir -p /mnt/data/redis/6379

启动 master 节点
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.13:6379> select 0
OK
172.16.18.13:6379> set a a
OK


[root@iss-half-ali-f-app-002-18-14 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-002-18-14 src]# ./redis-cli -h 172.16.18.14 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.14:6379> select 0
OK
172.16.18.14:6379> get a
"a"


[root@iss-half-ali-f-app-003-18-15 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-003-18-15 src]# ./redis-cli -h 172.16.18.15 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.15:6379> select 0
OK
172.16.18.15:6379> get a
"a"


sentinel 哨兵
[root@iss-half-ali-f-app-001-18-13 src]# cat /mnt/log/redis/26379/sentinel.log
2716:X 04 Mar 13:37:35.107 # Sentinel ID is 48143c8d87bf8b35bafd038bfb70d3ef115d3073

[root@iss-half-ali-f-app-002-18-14 src]# cat /mnt/log/redis/26379/sentinel.log
20876:X 04 Mar 13:37:35.107 # Sentinel ID is d5069af427115cfc03e92188c64d97ee16ff80e6

[root@iss-half-ali-f-app-003-18-15 src]# cat /mnt/log/redis/26379/sentinel.log
18426:X 04 Mar 13:37:35.107 # Sentinel ID is 6064c2bad7ac00d30e21339487350403f0aab890


[root@iss-half-ali-f-app-001-18-13 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-sentinel ../sentinel/sentinel.conf
[root@iss-half-ali-f-app-002-18-14 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-002-18-14 src]# ./redis-sentinel ../sentinel/sentinel.conf
[root@iss-half-ali-f-app-003-18-15 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-003-18-15 src]# ./redis-sentinel ../sentinel/sentinel.conf

172.16.18.13:26379> sentinel get-master-addr-by-name redis-master
1) "172.16.18.13"
2) "6379"


172.16.18.14:26379> sentinel get-master-addr-by-name redis-master
1) "172.16.18.13"
2) "6379"

172.16.18.15:26379> sentinel get-master-addr-by-name redis-master
1) "172.16.18.13"
2) "6379"


[root@iss-half-ali-f-app-001-18-13 src]# ./redis-server ../sentinel/redis.conf




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



切 172.16.18.14 为主节点
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.14 -p 6379 -a 123456 slaveof NO ONE
[root@iss-half-ali-f-app-002-18-14 src]# ./redis-cli -h 172.16.18.14 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.14:6379> slaveof NO ONE
OK
172.16.18.14:6379> set a c
OK
172.16.18.14:6379> get a
"c"
172.16.18.14:6379> slaveof 172.16.18.13 6379
OK

172.16.18.13 宕机状态下，14切换13为主后，set a c 这条数据丢了
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-server ../sentinel/redis.conf
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.13:6379>
172.16.18.13:6379>
172.16.18.13:6379> get a
"b"


[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 6379 -a 123456 info replication


[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 26379 info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redis-master,status=ok,address=172.16.18.13:6379,slaves=2,sentinels=1
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 26379 info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redis-master,status=ok,address=172.16.18.13:6379,slaves=2,sentinels=3

success logs
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 26379
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

172.16.18.13:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redis-master,status=ok,address=172.16.18.15:6379,slaves=2,sentinels=3


1)：首次启动时，必须先启动Master
2)：Sentinel 只在 server 端做主从切换，app端要自己开发(例如Jedis库的SentinelJedis，能够监控Sentinel的状态)
3)：若Master已经被判定为下线，Sentinel已经选择了新的Master，也已经将old Master改成Slave，但是还没有将其改成new Master。若此时重启old Master，则Redis集群将处于无Master状态，此时只能手动修改配置文件，然后重新启动集群



172.16.18.14:6379> info
# Server
redis_version:4.0.11
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:7954e3b157354d16
redis_mode:standalone
os:Linux 2.6.32-754.9.1.el6.x86_64 x86_64
arch_bits:64
multiplexing_api:epoll
atomicvar_api:sync-builtin
gcc_version:4.4.7
process_id:21158
run_id:bae84ae8a3d25c25119621f818468a5da8801881
tcp_port:6379
uptime_in_seconds:5419
uptime_in_days:0
hz:10
lru_clock:8181506
executable:/mnt/opt/redis-4.0.11/src/./redis-server
config_file:/mnt/opt/redis-4.0.11/sentinel/redis.conf

# Clients
connected_clients:8
client_longest_output_list:0
client_biggest_input_buf:0
blocked_clients:0

# Memory
used_memory:2044728
used_memory_human:1.95M
used_memory_rss:12247040
used_memory_rss_human:11.68M
used_memory_peak:2186344
used_memory_peak_human:2.09M
used_memory_peak_perc:93.52%
used_memory_overhead:2002996
used_memory_startup:786680
used_memory_dataset:41732
used_memory_dataset_perc:3.32%
total_system_memory:8389660672
total_system_memory_human:7.81G
used_memory_lua:37888
used_memory_lua_human:37.00K
maxmemory:0
maxmemory_human:0B
maxmemory_policy:noeviction
mem_fragmentation_ratio:5.99
mem_allocator:jemalloc-4.0.3
active_defrag_running:0
lazyfree_pending_objects:0

# Persistence
loading:0
rdb_changes_since_last_save:0
rdb_bgsave_in_progress:0
rdb_last_save_time:1551684002
rdb_last_bgsave_status:ok
rdb_last_bgsave_time_sec:0
rdb_current_bgsave_time_sec:-1
rdb_last_cow_size:6471680
aof_enabled:1
aof_rewrite_in_progress:0
aof_rewrite_scheduled:0
aof_last_rewrite_time_sec:0
aof_current_rewrite_time_sec:-1
aof_last_bgrewrite_status:ok
aof_last_write_status:ok
aof_last_cow_size:6393856
aof_current_size:50
aof_base_size:50
aof_pending_rewrite:0
aof_buffer_length:0
aof_rewrite_buffer_length:0
aof_pending_bio_fsync:0
aof_delayed_fsync:0

# Stats
total_connections_received:494
total_commands_processed:7619
instantaneous_ops_per_sec:7
total_net_input_bytes:616827
total_net_output_bytes:3900338
instantaneous_input_kbps:0.49
instantaneous_output_kbps:7.21
rejected_connections:0
sync_full:0
sync_partial_ok:0
sync_partial_err:0
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
evicted_keys:0
keyspace_hits:5
keyspace_misses:0
pubsub_channels:1
pubsub_patterns:0
latest_fork_usec:1202
migrate_cached_sockets:0
slave_expires_tracked_keys:0
active_defrag_hits:0
active_defrag_misses:0
active_defrag_key_hits:0
active_defrag_key_misses:0

# Replication
role:slave
master_host:172.16.18.15
master_port:6379
master_link_status:up
master_last_io_seconds_ago:1
master_sync_in_progress:0
slave_repl_offset:164641
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:9e86f0ce6f3f8a2e1ed266cef9d639a7e81293e0
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:164641
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:100684
repl_backlog_histlen:63958

# CPU
used_cpu_sys:3.56
used_cpu_user:2.72
used_cpu_sys_children:0.01
used_cpu_user_children:0.00

# Cluster
cluster_enabled:0

# Keyspace
db0:keys=1,expires=0,avg_ttl=0



[root@iss-half-ali-f-app-003-18-15 src]# ./redis-cli -h 172.16.18.15 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.15:6379> info
# Server
redis_version:4.0.11
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:6b498f92ce08f35b
redis_mode:standalone
os:Linux 2.6.32-754.9.1.el6.x86_64 x86_64
arch_bits:64
multiplexing_api:epoll
atomicvar_api:sync-builtin
gcc_version:4.4.7
process_id:18706
run_id:a3e54ad34e437e3af238fb5899020d398d7a676a
tcp_port:6379
uptime_in_seconds:5463
uptime_in_days:0
hz:10
lru_clock:8181556
executable:/mnt/opt/redis-4.0.11/src/./redis-server
config_file:/mnt/opt/redis-4.0.11/sentinel/redis.conf

# Clients
connected_clients:7
client_longest_output_list:0
client_biggest_input_buf:0
blocked_clients:0

# Memory
used_memory:2106232
used_memory_human:2.01M
used_memory_rss:11288576
used_memory_rss_human:10.77M
used_memory_peak:2206904
used_memory_peak_human:2.10M
used_memory_peak_perc:95.44%
used_memory_overhead:2052626
used_memory_startup:786680
used_memory_dataset:53606
used_memory_dataset_perc:4.06%
total_system_memory:8389660672
total_system_memory_human:7.81G
used_memory_lua:37888
used_memory_lua_human:37.00K
maxmemory:0
maxmemory_human:0B
maxmemory_policy:noeviction
mem_fragmentation_ratio:5.36
mem_allocator:jemalloc-4.0.3
active_defrag_running:0
lazyfree_pending_objects:0

# Persistence
loading:0
rdb_changes_since_last_save:0
rdb_bgsave_in_progress:0
rdb_last_save_time:1551685085
rdb_last_bgsave_status:ok
rdb_last_bgsave_time_sec:0
rdb_current_bgsave_time_sec:-1
rdb_last_cow_size:6455296
aof_enabled:1
aof_rewrite_in_progress:0
aof_rewrite_scheduled:0
aof_last_rewrite_time_sec:0
aof_current_rewrite_time_sec:-1
aof_last_bgrewrite_status:ok
aof_last_write_status:ok
aof_last_cow_size:6397952
aof_current_size:50
aof_base_size:50
aof_pending_rewrite:0
aof_buffer_length:0
aof_rewrite_buffer_length:0
aof_pending_bio_fsync:0
aof_delayed_fsync:0

# Stats
total_connections_received:497
total_commands_processed:8329
instantaneous_ops_per_sec:6
total_net_input_bytes:597084
total_net_output_bytes:4753026
instantaneous_input_kbps:0.35
instantaneous_output_kbps:1.34
rejected_connections:0
sync_full:2
sync_partial_ok:0
sync_partial_err:2
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
evicted_keys:0
keyspace_hits:3
keyspace_misses:0
pubsub_channels:1
pubsub_patterns:0
latest_fork_usec:1143
migrate_cached_sockets:0
slave_expires_tracked_keys:0
active_defrag_hits:0
active_defrag_misses:0
active_defrag_key_hits:0
active_defrag_key_misses:0

# Replication
role:master
connected_slaves:2
slave0:ip=172.16.18.14,port=6379,state=online,offset=175150,lag=1
slave1:ip=172.16.18.13,port=6379,state=online,offset=175150,lag=1
master_replid:9e86f0ce6f3f8a2e1ed266cef9d639a7e81293e0
master_replid2:b318cd330edc63ab3fd6e8952b167029678c7543
master_repl_offset:175436
second_repl_offset:100232
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:167
repl_backlog_histlen:175270

# CPU
used_cpu_sys:3.29
used_cpu_user:2.38
used_cpu_sys_children:0.01
used_cpu_user_children:0.00

# Cluster
cluster_enabled:0

# Keyspace
db0:keys=1,expires=0,avg_ttl=0






[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.13:6379> info
# Server
redis_version:4.0.11
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:d7e01b9378e246c
redis_mode:standalone
os:Linux 2.6.32-754.9.1.el6.x86_64 x86_64
arch_bits:64
multiplexing_api:epoll
atomicvar_api:sync-builtin
gcc_version:4.4.7
process_id:3514
run_id:7ccfe199ae71e339e951413a204f05fef62039fc
tcp_port:6379
uptime_in_seconds:1380
uptime_in_days:0
hz:10
lru_clock:8181593
executable:/mnt/opt/redis-4.0.11/src/./redis-server
config_file:/mnt/opt/redis-4.0.11/sentinel/redis.conf

# Clients
connected_clients:8
client_longest_output_list:0
client_biggest_input_buf:0
blocked_clients:0

# Memory
used_memory:2044784
used_memory_human:1.95M
used_memory_rss:9887744
used_memory_rss_human:9.43M
used_memory_peak:2165896
used_memory_peak_human:2.07M
used_memory_peak_perc:94.41%
used_memory_overhead:2002980
used_memory_startup:786664
used_memory_dataset:41804
used_memory_dataset_perc:3.32%
total_system_memory:8389660672
total_system_memory_human:7.81G
used_memory_lua:37888
used_memory_lua_human:37.00K
maxmemory:0
maxmemory_human:0B
maxmemory_policy:noeviction
mem_fragmentation_ratio:4.84
mem_allocator:jemalloc-4.0.3
active_defrag_running:0
lazyfree_pending_objects:0

# Persistence
loading:0
rdb_changes_since_last_save:0
rdb_bgsave_in_progress:0
rdb_last_save_time:1551684086
rdb_last_bgsave_status:ok
rdb_last_bgsave_time_sec:0
rdb_current_bgsave_time_sec:-1
rdb_last_cow_size:6471680
aof_enabled:1
aof_rewrite_in_progress:0
aof_rewrite_scheduled:0
aof_last_rewrite_time_sec:0
aof_current_rewrite_time_sec:-1
aof_last_bgrewrite_status:ok
aof_last_write_status:ok
aof_last_cow_size:10600448
aof_current_size:50
aof_base_size:50
aof_pending_rewrite:0
aof_buffer_length:0
aof_rewrite_buffer_length:0
aof_pending_bio_fsync:0
aof_delayed_fsync:0

# Stats
total_connections_received:19
total_commands_processed:6641
instantaneous_ops_per_sec:6
total_net_input_bytes:376952
total_net_output_bytes:1751984
instantaneous_input_kbps:0.56
instantaneous_output_kbps:1.64
rejected_connections:0
sync_full:2
sync_partial_ok:0
sync_partial_err:2
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
evicted_keys:0
keyspace_hits:3
keyspace_misses:0
pubsub_channels:1
pubsub_patterns:0
latest_fork_usec:1141
migrate_cached_sockets:0
slave_expires_tracked_keys:0
active_defrag_hits:0
active_defrag_misses:0
active_defrag_key_hits:0
active_defrag_key_misses:0

# Replication
role:slave
master_host:172.16.18.15
master_port:6379
master_link_status:up
master_last_io_seconds_ago:1
master_sync_in_progress:0
slave_repl_offset:183200
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:9e86f0ce6f3f8a2e1ed266cef9d639a7e81293e0
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:183200
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:102580
repl_backlog_histlen:80621

# CPU
used_cpu_sys:1.12
used_cpu_user:0.86
used_cpu_sys_children:0.01
used_cpu_user_children:0.00

# Cluster
cluster_enabled:0

# Keyspace
db0:keys=1,expires=0,avg_ttl=0


# 模拟 master 故障
[root@iss-half-ali-f-app-003-18-15 src]# !ps
ps -ef|grep redis
root     17918     1  0 10:01 ?        00:00:27 ./redis-server 172.16.18.15:6001 [cluster]
root     17927     1  0 10:02 ?        00:00:24 ./redis-server 172.16.18.15:6002 [cluster]
root     18706     1  0 14:12 ?        00:00:05 ./redis-server 172.16.18.15:6379
root     19128     1  0 15:34 ?        00:00:02 ./redis-sentinel 172.16.18.15:26379 [sentinel]
root     19151 18320  0 15:45 pts/0    00:00:00 grep redis
[root@iss-half-ali-f-app-003-18-15 src]# kill -QUIT 18706
[root@iss-half-ali-f-app-003-18-15 src]# kill -3 18706
[root@iss-half-ali-f-app-001-18-13 src]# ./redis-cli -h 172.16.18.13 -p 26379
172.16.18.13:26379> sentinel get-master-addr-by-name redis-master
1) "172.16.18.13"
2) "6379"

[root@iss-half-ali-f-app-003-18-15 src]# ./redis-cli -h 172.16.18.15 -p 6379 -a 123456
Warning: Using a password with '-a' option on the command line interface may not be safe.
172.16.18.15:6379> info
# Server
redis_version:4.0.11
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:6b498f92ce08f35b
redis_mode:standalone
os:Linux 2.6.32-754.9.1.el6.x86_64 x86_64
arch_bits:64
multiplexing_api:epoll
atomicvar_api:sync-builtin
gcc_version:4.4.7
process_id:19158
run_id:d720f4da8e212fc2c7ae2be044d2a06610985ebe
tcp_port:6379
uptime_in_seconds:14
uptime_in_days:0
hz:10
lru_clock:8181777
executable:/mnt/opt/redis-4.0.11/src/./redis-server
config_file:/mnt/opt/redis-4.0.11/sentinel/redis.conf

# Clients
connected_clients:8
client_longest_output_list:0
client_biggest_input_buf:0
blocked_clients:0

# Memory
used_memory:2044728
used_memory_human:1.95M
used_memory_rss:10027008
used_memory_rss_human:9.56M
used_memory_peak:2125328
used_memory_peak_human:2.03M
used_memory_peak_perc:96.21%
used_memory_overhead:2002980
used_memory_startup:786664
used_memory_dataset:41748
used_memory_dataset_perc:3.32%
total_system_memory:8389660672
total_system_memory_human:7.81G
used_memory_lua:37888
used_memory_lua_human:37.00K
maxmemory:0
maxmemory_human:0B
maxmemory_policy:noeviction
mem_fragmentation_ratio:4.90
mem_allocator:jemalloc-4.0.3
active_defrag_running:0
lazyfree_pending_objects:0

# Persistence
loading:0
rdb_changes_since_last_save:1
rdb_bgsave_in_progress:0
rdb_last_save_time:1551685635
rdb_last_bgsave_status:ok
rdb_last_bgsave_time_sec:-1
rdb_current_bgsave_time_sec:-1
rdb_last_cow_size:0
aof_enabled:1
aof_rewrite_in_progress:0
aof_rewrite_scheduled:0
aof_last_rewrite_time_sec:0
aof_current_rewrite_time_sec:-1
aof_last_bgrewrite_status:ok
aof_last_write_status:ok
aof_last_cow_size:8499200
aof_current_size:50
aof_base_size:50
aof_pending_rewrite:0
aof_buffer_length:0
aof_rewrite_buffer_length:0
aof_pending_bio_fsync:0
aof_delayed_fsync:0

# Stats
total_connections_received:9
total_commands_processed:103
instantaneous_ops_per_sec:8
total_net_input_bytes:5644
total_net_output_bytes:45842
instantaneous_input_kbps:0.58
instantaneous_output_kbps:1.65
rejected_connections:0
sync_full:0
sync_partial_ok:0
sync_partial_err:0
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
evicted_keys:0
keyspace_hits:0
keyspace_misses:0
pubsub_channels:1
pubsub_patterns:0
latest_fork_usec:905
migrate_cached_sockets:0
slave_expires_tracked_keys:0
active_defrag_hits:0
active_defrag_misses:0
active_defrag_key_hits:0
active_defrag_key_misses:0

# Replication
role:slave
master_host:172.16.18.13
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_repl_offset:222408
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:68ae725e730d0130b0eb178cc63f997c16fddbb3
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:222408
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:221657
repl_backlog_histlen:752

# CPU
used_cpu_sys:0.02
used_cpu_user:0.01
used_cpu_sys_children:0.00
used_cpu_user_children:0.00

# Cluster
cluster_enabled:0

# Keyspace
db0:keys=1,expires=0,avg_ttl=0
