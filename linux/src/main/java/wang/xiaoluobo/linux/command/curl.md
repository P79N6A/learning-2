# [curl](http://man.linuxde.net/curl)
## curl 介绍
在Linux中curl是一个利用URL规则在命令行下工作的文件传输工具，可以说是一款很强大的http命令行工具。它支持文件的上传和下载，是综合传输工具，但按传统，习惯称url为下载工具。

> 常用参数
```text
-A/--user-agent <string>          设置用户代理发送给服务器
-b/--cookie <name=string/file>    cookie字符串或文件读取位置
-c/--cookie-jar <file>            操作结束后把cookie写入到这个文件中
-C/--continue-at <offset>         断点续转
-D/--dump-header <file>           把header信息写入到该文件中
-e/--referer                      来源网址
-f/--fail                         连接失败时不显示http错误
-o/--output                       把输出写到该文件中
-O/--remote-name                  把输出写到该文件中，保留远程文件的文件名
-r/--range <range>                检索来自HTTP/1.1或FTP服务器字节范围
-s/--silent                       静音模式。不输出任何东西
-T/--upload-file <file>           上传文件
-u/--user <user[:password]>       设置服务器的用户和密码
-w/--write-out [format]           什么输出完成后
-x/--proxy <host[:port]>          在给定的端口上使用HTTP代理
-#/--progress-bar                 进度条显示当前的传送状态
```


## command action
```text
[root@hadoop01 ~]$ curl http://127.0.0.1:3000
"Welcome to The Athens Proxy"


[root@hadoop01 ~]$ curl http://127.0.0.1:3000 >> index.html
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0    29    0    29    0     0  20265      0 --:--:-- --:--:-- --:--:-- 29000


[root@hadoop01 ~]$ curl -o index1.html  http://127.0.0.1:3000
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0    29    0    29    0     0  21706      0 --:--:-- --:--:-- --:--:-- 29000

# 测试网页返回结果
[root@hadoop01 ~]$ curl -o /dev/null -s -w %{http_code} http://127.0.0.1:3000
200
```
