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




