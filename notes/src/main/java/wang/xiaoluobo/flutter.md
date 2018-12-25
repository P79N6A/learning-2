# [flutter release 1.0](https://flutter.io/docs/get-started/install/macos)
# Flutter开发环境搭建
1. 安装Dart SDK   
可以参考：http://www.dartlang.cc/docs/tutorials/get-started/  
运行 ：dart --version   
查看提示版本 ：Dart VM version: 2.0.0

2. 安装Flutter SDK  
可以参考：https://flutterchina.club/setup-macos/  
安装后运行 flutter doctor 
按照提示安装对应插件

3. 开发工具安装Dart和Flutter插件  
IntelliJ IDEA Ultimate, version 2017.1 或更高版本.

4. 配置环境变量  
export PATH=$PATH:/Users/wyd/soft/dart-sdk/bin
export PATH=$PATH:$HOME/.pub-cache/bin
export PATH=$PATH:/Users/wyd/soft/flutter/bin
export PUB_HOSTED_URL=https://pub.flutter-io.cn
export FLUTTER_STORAGE_BASE_URL=https://storage.flutter-io.cn

export ANDROID_HOME=/Users/wyd/soft/sdk
export PATH=${PATH}:${ANDROID_HOME}/tools
export PATH=${PATH}:${ANDROID_HOME}/platform-tools

