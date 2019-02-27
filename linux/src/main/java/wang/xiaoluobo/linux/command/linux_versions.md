# linux 查看系统版本信息

```bash
$ lsb_release -a
LSB Version:	:base-4.0-amd64:base-4.0-noarch:core-4.0-amd64:core-4.0-noarch
Distributor ID:	CentOS
Description:	CentOS release 6.10 (Final)
Release:	6.10
Codename:	Final

$ uname -a
Linux hadoop01 2.6.32-754.9.1.el6.x86_64 #1 SMP Thu Dec 6 08:02:15 UTC 2018 x86_64 x86_64 x86_64 GNU/Linux

$ cat /proc/version
Linux version 2.6.32-754.9.1.el6.x86_64 (mockbuild@x86-01.bsys.centos.org) (gcc version 4.4.7 20120313 (Red Hat 4.4.7-23) (GCC) ) #1 SMP Thu Dec 6 08:02:15 UTC 2018
```
