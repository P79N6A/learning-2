# nginx
```Bash
$ cd /data1
$ tar -zxvf nginx-1.10.0.tar.gz 

# 安装pcre开发包 
$ yum -y install pcre-devel 
$ yum -y install gcc gcc-c++ ncurses-devel perl 
$ yum -y install make gcc gcc-c++ ncurses-devel
$ yum -y install zlib zlib-devel 
$ yum -y install openssl openssl--devel 

$ cd /data1/nginx-1.10.0
$ ./configure --prefix=/data1/nginx
$ make
$ make install

# nginx优化配置缓存目录
$ mkdir -p /data1/nginx/ngx_fcgi_tmp
$ mkdir -p /data1/nginx/ngx_fcgi_cache
$ mkdir -p /data1/nginx/cache

# 启动服务
$ cd /data1/nginx/sbin/
$ ./nginx  -c /data1/nginx/conf/nginx.conf 
# 停止服务 
$ ./nginx -s stop 

# 查看端口占用情况 
$ netstat -tunlp

# nginx配置开机启动
$ /data1/nginx/sbin/nginx -c /data1/nginx/conf/nginx.conf
```