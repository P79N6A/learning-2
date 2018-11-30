```sbtshell
查看SELinux状态：
1. /usr/sbin/sestatus -v      
SELinux status:                 enabled
2. getenforce                 ## 也可以用这个命令检查

关闭SELinux
1、临时关闭(不用重启机器)，设置SELinux成为permissive模式，setenforce 1 设置SELinux 成为enforcing模式
setenforce 0                  
                              
2、修改配置文件需要重启机器
vim /etc/selinux/config
SELINUX=disabled
```
