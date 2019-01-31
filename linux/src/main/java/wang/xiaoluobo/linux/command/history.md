#### history配置

```bash
# 设置history历史记录数
$ vi /etc/profile
export HISTTIMEFORMAT='%F %T '
export HISTSIZE="50"

# 清除重复的命令
$ vi ~/.bash_profile
export HISTCONTROL=ignoredups

# 清除历史中所有重复的命令
export HISTCONTROL=erasedups

# 不记住特定的命令，输入命令时，前面添加空格才会忽略
export HISTCONTROL=ignorespace
```

```bash
[hadoop@hadoop01 logs]$ history 3
 1027  2019-01-31 14:23:16 hadoop 10.31.140.124 history 3
 1028  2019-01-31 14:23:20 hadoop 10.31.140.124 history 3
 1029  2019-01-31 14:23:37 hadoop 10.31.140.124 history 3
```