### mac常用软件
- Homebrew  
https://www.jianshu.com/p/4e80b42823d5

- [mac shell](https://www.jianshu.com/p/3291de46f3ff)

- protobuf2.5.0  
https://www.jianshu.com/p/ba136a04385c  
protoc --java_out=./examples ./examples/addressbook.proto

- [redis](https://redis.io/download)  
redis desktop manager客户端  
https://download.csdn.net/download/maoni6958/10325040

- p7zip  
brew install p7zip  
解压7z格式文件  
7z e docker.7z  

- [mysql客户端](https://www.sequelpro.com/)  

- [mongodb客户端](https://nosqlbooster.com/)

- Gliffy Diagrams  
chrome流程图插件

- Iterm2 rs sz  
http://zoucz.com/blog/2017/01/23/mac-terminal-rzsz-session/
Item2教程
https://cnbin.github.io/blog/2015/06/20/iterm2-kuai-jie-jian-da-quan/

- 文本工具  
https://atom.io/  
https://www.sublimetext.com/  
https://code.visualstudio.com/download

- scala
https://downloads.lightbend.com/scala/2.12.8/scala-2.12.8.tgz  
```Bash
$ vim .bash_profile
export SCALA_HOME=/Users/dongzai1005/softwares/scala-2.12.8
export PATH=$PATH:$SCALA_HOME/bin
$ source .bash_profile
dongzai1005@localhost:~$ scala -version
Scala code runner version 2.12.8 -- Copyright 2002-2018, LAMP/EPFL and Lightbend, Inc.
```

- mac 清理缓存
1. sudo purge
2. sudo rm -rf /private/var/log/*
3. rm -rf ~/Library/Caches/*
4. rm -rf /private/var/tmp/*
