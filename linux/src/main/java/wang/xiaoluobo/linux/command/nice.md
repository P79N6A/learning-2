# nice
## nice介绍
nice命令以更改过的优先序来执行程序，如果未指定程序，则会印出目前的排程优先序，内定的adjustment为10，范围为-20(最高优先序)到19(最低优先序)

> nice [-n adjustment] [-adjustment] [--adjustment=adjustment] [--help] [--version] [command [arg...]]

```text
参数说明：
    -n adjustment, -adjustment, --adjustment=adjustment 皆为将该原有优先序的增加 adjustment
    --help 显示求助讯息
    --version 显示版本资讯
```

> 注意：优先序 (priority) 为操作系统用来决定 CPU 分配的参数，Linux 使用『回合制(round-robin)』的演算法来做 CPU 排程，优先序越高，所可能获得的 CPU时间就越多。


## command action
```bash
# 默认值为0
[hadoop@hadoop01 ~]$ nice
0

# 后台运行
$ vi &

# 设置默认优先级
$ nice vi &

# 设置优先级为19
$ nice -n 19 vi &

# 设置优先级为 -20
$ nice -n -20 vi &

# 将ls的优先序加1并执行
$ nice -n 1 ls
```
