#### 字体

```Bash
# 查看现有字体列表：
fc-list

# 查看当前系统默认字体
more /etc/sysconfig/i18n

# linux安装中文字体
yum -y install fonts-chinese
yum -y install fonts-ISO8859-2

# 编辑/etc/sysconfig/i18n
vi /etc/sysconfig/i18n

LANG="zh_CN.UTF-8"
SUPPORTED="zh_CN.UTF-8:zh_CN:zh"
SYSFONT="latarcyrheb-sun16"
```