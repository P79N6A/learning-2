# mysql version 5.7
## 一、MyISAM和InnoDB区别
### 1. MyISAM存储引擎的特点
- 表级锁、不支持事务和全文索引，适合一些CMS内容管理系统作为后台数据库使用，但是使用大并发、重负荷生产系统上，表锁结构的特性就显得力不从心

- 每个MyISAM在磁盘上存储成三个文件：
    - 第一个文件的名字以表的名字开始，扩展名指出文件类型，.frm文件存储表定义。 
    - 第二个文件是数据文件，其扩展名为.MYD(MYData) 
    - 第三个文件是索引文件，其扩展名是.MYI(MYIndex)

- MyISAM适合： 
    - 做很多count 的计算； 
    - 插入不频繁，查询非常频繁，如果执行大量的SELECT，MyISAM是更好的选择； 
    - 没有事务

### 2. InnoDB存储引擎的特点
- 行级锁、事务安全(ACID兼容)支持外键。InnoDB存储引擎提供了具有提交、回滚和崩溃恢复能力的事务安全存储引擎。InnoDB是为处理巨大量时拥有最大性能而设计的。它的CPU效率可能是任何其他基于磁盘的关系数据库引擎所不能匹敌的

- 基于磁盘的资源是InnoDB表空间数据文件和它的日志文件，InnoDB 表的 大小只受限于操作系统文件的大小，一般为2GB

- InnoDB适合： 
    - 可靠性要求比较高，或者要求事务； 
    - 表更新和查询都相当的频繁，并且表锁定的机会比较大的情况指定数据引擎的创建； 
    - 如果你的数据执行大量的INSERT或UPDATE，出于性能方面的考虑，应该使用InnoDB表； 
    - DELETE FROM table时，InnoDB不会重新建立表，而是一行一行的 删除； 
    - LOAD TABLE FROM MASTER操作对InnoDB是不起作用的，解决方法是首先把InnoDB表改成MyISAM表，导入数据后再改成InnoDB表，但是对于使用的额外的InnoDB特性(例如外键)的表不适用。
    
## 二、InnoDB支持的四种事务隔离级别名称    
- read uncommited: 读到未提交数据
- read committed: 脏读，不可重复读
- repeatable read: 可重复读
- serializable: 串行事务

## 三、Mysql中有哪几种锁?
- MyISAM支持表锁，InnoDB支持表锁和行锁，默认为行锁
- 表级锁: 开销小，加锁快，不会出现死锁。锁定粒度大，发生锁冲突的概率最高，并发量最低
- 行级锁: 开销大，加锁慢，会出现死锁。锁力度小，发生锁冲突的概率小，并发度最高


## 四、mysql command
```text
mysql> show processlist;
+----+------+-----------------+------+---------+------+----------+------------------+
| Id | User | Host            | db   | Command | Time | State    | Info             |
+----+------+-----------------+------+---------+------+----------+------------------+
|  5 | root | localhost:57841 | test | Sleep   |   21 |          | NULL             |
|  6 | root | localhost:57842 | NULL | Sleep   |   11 |          | NULL             |
|  7 | root | localhost:57886 | NULL | Sleep   |   68 |          | NULL             |
|  9 | root | localhost       | NULL | Query   |    0 | starting | show processlist |
+----+------+-----------------+------+---------+------+----------+------------------+
4 rows in set (0.00 sec)











```