# [phoenix version 5.0.0(hbase2.1.2)](http://phoenix.apache.org/installation.html)
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
    . . . . . . . . . . . . . . . . .>     pk VARCHAR PRIMARY KEY
    . . . . . . . . . . . . . . . . .>     ,"i"."name" VARCHAR
    . . . . . . . . . . . . . . . . .>     ,"i"."age" VARCHAR)
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

### 三、 phoenix command
1. command
   ```text
    0: jdbc:phoenix:172.16.18.13:2181> help
    !all                Execute the specified SQL against all the current
                        connections
    !autocommit         Set autocommit mode on or off
    !batch              Start or execute a batch of statements
    !brief              Set verbose mode off
    !call               Execute a callable statement
    !close              Close the current connection to the database
    !closeall           Close all current open connections
    !columns            List all the columns for the specified table
    !commit             Commit the current transaction (if autocommit is off)
    !connect            Open a new connection to the database.
    !dbinfo             Give metadata information about the database
    !describe           Describe a table
    !dropall            Drop all tables in the current database
    !exportedkeys       List all the exported keys for the specified table
    !go                 Select the current connection
    !help               Print a summary of command usage
    !history            Display the command history
    !importedkeys       List all the imported keys for the specified table
    !indexes            List all the indexes for the specified table
    !isolation          Set the transaction isolation for this connection
    !list               List the current connections
    !manual             Display the SQLLine manual
    !metadata           Obtain metadata information
    !nativesql          Show the native SQL for the specified statement
    !outputformat       Set the output format for displaying results
                        (table,vertical,csv,tsv,xmlattrs,xmlelements)
    !primarykeys        List all the primary keys for the specified table
    !procedures         List all the procedures
    !properties         Connect to the database specified in the properties file(s)
    !quit               Exits the program
    !reconnect          Reconnect to the database
    !record             Record all output to the specified file
    !rehash             Fetch table and column names for command completion
    !rollback           Roll back the current transaction (if autocommit is off)
    !run                Run a script from the specified file
    !save               Save the current variabes and aliases
    !scan               Scan for installed JDBC drivers
    !script             Start saving a script to a file
    !set                Set a sqlline variable
    
    Variable        Value      Description
    =============== ========== ================================
    autoCommit      true/false Enable/disable automatic
                               transaction commit
    autoSave        true/false Automatically save preferences
    color           true/false Control whether color is used
                               for display
    fastConnect     true/false Skip building table/column list
                               for tab-completion
    force           true/false Continue running script even
                               after errors
    headerInterval  integer    The interval between which
                               headers are displayed
    historyFile     path       File in which to save command
                               history. Default is
                               $HOME/.sqlline/history (UNIX,
                               Linux, Mac OS),
                               $HOME/sqlline/history (Windows)
    incremental     true/false Do not receive all rows from
                               server before printing the first
                               row. Uses fewer resources,
                               especially for long-running
                               queries, but column widths may
                               be incorrect.
    isolation       LEVEL      Set transaction isolation level
    maxColumnWidth  integer    The maximum width to use when
                               displaying columns
    maxHeight       integer    The maximum height of the
                               terminal
    maxWidth        integer    The maximum width of the
                               terminal
    numberFormat    pattern    Format numbers using
                               DecimalFormat pattern
    outputFormat    table/vertical/csv/tsv Format mode for
                               result display
    propertiesFile  path       File from which SqlLine reads
                               properties on startup; default is
                               $HOME/.sqlline/sqlline.properties
                               (UNIX, Linux, Mac OS),
                               $HOME/sqlline/sqlline.properties
                               (Windows)
    rowLimit        integer    Maximum number of rows returned
                               from a query; zero means no
                               limit
    showElapsedTime true/false Display execution time when
                               verbose
    showHeader      true/false Show column names in query
                               results
    showNestedErrs  true/false Display nested errors
    showWarnings    true/false Display connection warnings
    silent          true/false Be more silent
    timeout         integer    Query timeout in seconds; less
                               than zero means no timeout
    trimScripts     true/false Remove trailing spaces from
                               lines read from script files
    verbose         true/false Show verbose error messages and
                               debug info
    !sql                Execute a SQL command
    !tables             List all the tables in the database
    !typeinfo           Display the type map for the current connection
    !verbose            Set verbose mode on
    
    Comments, bug reports, and patches go to ???
    ```

### 四、 [SQuirrel客户端](http://squirrel-sql.sourceforge.net/)





