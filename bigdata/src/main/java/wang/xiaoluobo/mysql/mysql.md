### mysql5.7
http://ftp.kaist.ac.kr/mysql/Downloads/MySQL-5.7/

##### 1. 查看mysql版本
```sbtshell
[wangyandong@bigdata01 opt]$ lsb_release -a
LSB Version:	:base-4.0-amd64:base-4.0-noarch:core-4.0-amd64:core-4.0-noarch
Distributor ID:	CentOS
Description:	CentOS release 6.10 (Final)
Release:	6.10
Codename:	Final
```

##### 2. 卸载mysql
```sbtshell
[wangyandong@bigdata01 opt]$ sudo su -
[root@bigdata01 opt]# rpm -qa | grep mysql
mysql-libs-5.1.73-8.el6_8.x86_64
mysql-5.1.73-8.el6_8.x86_64
mysql-devel-5.1.73-8.el6_8.x86_64
[root@bigdata01 opt]# rpm -e --nodeps mysql-libs-5.1.73-8.el6_8.x86_64
[root@bigdata01 opt]# rpm -e --nodeps mysql-5.1.73-8.el6_8.x86_64
[root@bigdata01 opt]# rpm -e --nodeps mysql-devel-5.1.73-8.el6_8.x86_64
[root@bigdata01 opt]# rpm -qa | grep mysql
```

##### 3. 查看mysql用户组及密码是否存在
```sbtshell
[root@bigdata01 opt]# cat /etc/group | grep mysql
[root@bigdata01 opt]# cat /etc/passwd | grep mysql
```

##### 4. 添加用户及组
```sbtshell
[root@bigdata01 opt]# groupadd mysql
[root@bigdata01 opt]# useradd -d /home/mysql -g mysql -m mysql -s /sbin/nologin
[root@bigdata01 opt]# chown -R mysql:mysql mysql-5.7.23
[root@bigdata01 mysql-5.7.23]# mkdir -p /mnt/data/mysql/data/
[root@bigdata01 mysql-5.7.23]# chown -R mysql:mysql /mnt/data/mysql
[root@bigdata01 mysql-5.7.23]# mkdir -p /mnt/log/mysql
[root@bigdata01 mnt]# chown -R mysql:mysql /mnt/log/mysql
```

##### 5. 初始化mysql
```sbtshell
[mysql@bigdata01 mysql-5.7.23]$ ./bin/mysqld --initialize --user=mysql --basedir=/mnt/data/mysql/ --datadir=/mnt/data/mysql/data/
[mysql@bigdata01 mysql-5.7.23]$ tail -f /mnt/log/mysql/mysqld.log
2018-09-10T07:23:51.110288Z 0 [Warning] TIMESTAMP with implicit DEFAULT value is deprecated. Please use --explicit_defaults_for_timestamp server option (see documentation for more details).
2018-09-10T07:23:51.110403Z 0 [ERROR] Can't find error-message file '/mnt/data/mysql/share/errmsg.sys'. Check error-message file location and 'lc-messages-dir' configuration directive.
2018-09-10T07:23:51.898570Z 0 [Warning] InnoDB: New log files created, LSN=45790
2018-09-10T07:23:52.036246Z 0 [Warning] InnoDB: Creating foreign key constraint system tables.
2018-09-10T07:23:52.107793Z 0 [Warning] No existing UUID has been found, so we assume that this is the first time that this server has been started. Generating a new UUID: 77cc6610-b4ca-11e8-ab73-00163e2e5da7.
2018-09-10T07:23:52.112647Z 0 [Warning] Gtid table is not ready to be used. Table 'mysql.gtid_executed' cannot be opened.
2018-09-10T07:23:52.113568Z 1 [Note] A temporary password is generated for root@localhost: _d?S*8voweo#
```

##### 6. mysql安全
```sbtshell
[root@bigdata01 mysql-5.7.23]# ./bin/mysql_ssl_rsa_setup --datadir=/mnt/data/mysql/data/
Generating a 2048 bit RSA private key
.................................+++
..................+++
writing new private key to 'ca-key.pem'
-----
Generating a 2048 bit RSA private key
.....................................................+++
..................+++
writing new private key to 'server-key.pem'
-----
Generating a 2048 bit RSA private key
.............................................+++
.......................................................................................................................................................................................................+++
writing new private key to 'client-key.pem'
-----
```

##### 7. 添加mysql开机启动
```sbtshell
[root@bigdata01 mysql-5.7.23]# cp ./support-files/mysql.server /etc/init.d/mysql
[root@bigdata01 mysql-5.7.23]# chmod +x /etc/init.d/mysql
[root@bigdata01 mysql-5.7.23]# chkconfig --add mysql
[root@bigdata01 mysql-5.7.23]# chkconfig --list mysql
mysql          	0:off	1:off	2:on	3:on	4:on	5:on	6:off
```

##### 8. mysql配置
```sbtshell
[root@bigdata01 mysql-5.7.23]# vim /etc/init.d/mysql
basedir=/mnt/opt/mysql-5.7.23
datadir=/mnt/data/mysql/data

[root@bigdata01 mysql-5.7.23]# vi /etc/my.cnf
[mysqld]
character_set_server=utf8
init_connect='SET NAMES utf8'
basedir=/mnt/opt/mysql-5.7.23
datadir=/mnt/data/mysql/data
socket=/tmp/mysql.sock
lower_case_table_names = 1
log-error=/mnt/log/mysql/mysqld.log
pid-file=/mnt/opt/mysql-5.7.23/mysqld.pid
```

##### 9. 修改mysql密码
```sbtshell
# 查看mysql默认密码
[root@bigdata01 mysql]# tail -f /mnt/log/mysql/mysqld.log
2018-09-10T07:23:52.113568Z 1 [Note] A temporary password is generated for root@localhost: _d?S*8voweo#

# mysql用户启动mysql服务
[mysql@bigdata01 mysql-5.7.23]$ service mysql restart
[mysql@bigdata01 mysql-5.7.23]$ mysql -uroot -p
Enter password:
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.23

Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> set password=password('bigdata01_mysql');
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'bigdata01_mysql' WITH GRANT OPTION;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.00 sec)
```
