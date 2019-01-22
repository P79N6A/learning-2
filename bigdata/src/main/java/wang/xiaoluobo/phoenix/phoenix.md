# phoenix version 5.0.0(hbase2.1.2)
### 一、 phoenix config
1. 环境变量
    ```Base
    $ vim .bash_profile
    export PHOENIX_HOME=/mnt/opt/phoenix-5.0.0-HBase-2.0
    export PHOENIX_CLASSPATH=$PHOENIX_HOME
    export PATH=$PATH:$PHOENIX_HOME/bin
    $ source .bash_profile
    
    # 拷贝phoenix到hbase lib目录
    [hadoop@hadoop03 phoenix-5.0.0-HBase-2.0]$ cp phoenix-5.0.0-HBase-2.0-server.jar $HBASE_HOME/lib
    ```

2. python包安装
    ```bash
    [hadoop@hadoop03 opt]$ wget https://files.pythonhosted.org/packages/18/dd/e617cfc3f6210ae183374cd9f6a26b20514bbb5a792af97949c5aacddf0f/argparse-1.4.0.tar.gz
    
    # root用户安装
    [root@hadoop03 argparse-1.4.0]# python setup.py install
    ```

3. 重启hbase
    ```bash
    [hadoop@hadoop01 hbase-2.1.2]$ ./bin/stop-hbase.sh
    [hadoop@hadoop01 hbase-2.1.2]$ ./bin/start-hbase.sh
    ```

4. phoenix
    ```text
    # 连接任一zk节点即可
    [hadoop@hadoop03 phoenix-5.0.0-HBase-2.0]$ ./bin/sqlline.py 172.16.18.13:2181
    
    # column_encoded_bytes=0 解决phoenix映射已存在HBase表，查询不到数据
    0: jdbc:phoenix:172.16.18.13:2181> create table "test"(
    . . . . . . . . . . . . . . . . .>
    . . . . . . . . . . . . . . . . .>     pk VARCHAR PRIMARY KEY
    . . . . . . . . . . . . . . . . .>
    . . . . . . . . . . . . . . . . .>     ,"i"."name" VARCHAR
    . . . . . . . . . . . . . . . . .>
    . . . . . . . . . . . . . . . . .>     ,"i"."age" VARCHAR)
    . . . . . . . . . . . . . . . . .>
    . . . . . . . . . . . . . . . . .>  column_encoded_bytes=0;
    No rows affected (0.756 seconds)


    # hbase
    hbase(main):007:0> put 'test','1','c:name','zhangsan'
    Took 0.0542 seconds
    hbase(main):008:0> put 'test','2','c:name','lisi'
    Took 0.0060 seconds
    hbase(main):009:0> put 'test','1','c:age','22'
    Took 0.0059 seconds
    hbase(main):010:0> put 'test','2','c:age','20'
    Took 0.0060 seconds
    hbase(main):011:0> scan 'test'
    ROW                           COLUMN+CELL
     1                            column=i:age, timestamp=1548139413177, value=15
     1                            column=i:name, timestamp=1548139395942, value=zhangsan
     2                            column=i:age, timestamp=1548139422859, value=2
     2                            column=i:name, timestamp=1548139405592, value=lisi
    2 row(s)
    Took 0.0318 seconds
    
    # phoenix查询
    0: jdbc:phoenix:172.16.18.13:2181> select * from "test";
    +-----+-----------+------+
    | PK  |   name    | age  |
    +-----+-----------+------+
    | 1   | zhangsan  | 22   |
    | 2   | lisi      | 20    |
    +-----+-----------+------+
    2 rows selected (0.016 seconds)
    ```

### 二、 phoenix问题
1. [phoenix映射已存在HBase表，查询不到数据](http://phoenix.apache.org/columnencoding.html)
    ```text
    # 建表时添加 column_encoded_bytes=0
    0: jdbc:phoenix:172.16.18.13:2181> create table "test"(
    . . . . . . . . . . . . . . . . .>     pk VARCHAR PRIMARY KEY
    . . . . . . . . . . . . . . . . .>     ,"i"."name" VARCHAR
    . . . . . . . . . . . . . . . . .>     ,"i"."age" VARCHAR)
    . . . . . . . . . . . . . . . . .>  column_encoded_bytes=0;
    ```
