# tomcat version 9.0.12

## Tomcat
### 1. shell 调用关系
> $ start_up.sh -> catalina.sh
    
    ```bash
    # start_up.sh
    # $@ 获取参数列表
    exec "$PRGDIR"/"$EXECUTABLE" start "$@"
    
    # catalina.sh
    eval \{ $_NOHUP "\"$_RUNJAVA\"" "\"$LOGGING_CONFIG\"" $LOGGING_MANAGER $JAVA_OPTS $CATALINA_OPTS \
          -D$ENDORSED_PROP="\"$JAVA_ENDORSED_DIRS\"" \
          -classpath "\"$CLASSPATH\"" \
          -Dcatalina.base="\"$CATALINA_BASE\"" \
          -Dcatalina.home="\"$CATALINA_HOME\"" \
          -Djava.io.tmpdir="\"$CATALINA_TMPDIR\"" \
          org.apache.catalina.startup.Bootstrap "$@" start \
          2\>\&1 \&\& echo \$! \>\"$catalina_pid_file\" \; \} $catalina_out_command "&"
    ```
    
### 2. java 源码
1. Bootstrap -> static 代码块(tomcat 运行环境) ->  初始化 Catalina -> 设置 Catalina#setParentClassLoader() 
2. 调用 Catalina.load(String args[]) -> 加载 tomcat 配置文件并解析初始化 -> Bootstrap#start() -> 初始化 StandardServer#startInternal()
    > StandardServer#start() -> globalNamingResources#start() -> StandardService#start() -> executors#start()
     -> listener#start() -> Connector#startInternal() -> NioEndpoint#startInternal()
    
3. Catalina#start() -> 添加钩子回调 -> tomcat 启动完成


## Tomcat 模块
### Connector
#### connector
1. Http Connector：解析HTTP请求，又分为BIO Http Connector和NIO Http Connector，即阻塞IO Connector和非阻塞IO Connector。本文主要分析NIO Http Connector的实现过程。
2. AJP：基于AJP协议，用于Tomcat与HTTP服务器通信定制的协议，能提供较高的通信速度和效率。如与Apache服务器集成时，采用这个协议。
3. APR HTTP Connector：用C实现，通过JNI调用的。主要提升对静态资源（如HTML、图片、CSS、JS等）的访问性能。

#### [tomcat nio thread](../tomcat/util/net/NioEndpoint.java)
> NIO tailored thread pool, providing the following services
1. Socket acceptor thread(Acceptor)
2. Socket poller thread(NioEndpoint.Poller)
3. Worker threads pool(NioEndpoint.SocketProcessor)

### Container
1. Engine: 整个Catalina servlet引擎的表示，最有可能包含一个或多个主机或上下文实现的子容器，或其他自定义组。
2. Host: 表示包含多个上下文的虚拟主机。
3. Context: 表示单个ServletContext，它通常包含一个或多个支持的servlet的Wrappers。
4. Wrapper: 表示单个servlet定义(如果servlet本身实现SingleThreadModel，则可以支持多个servlet实例)。

> Connector -> StandardEngine -> ContainerBase -> StandardEngineValue -> Request -> StandardHost -> StandardHostValue  

### JNDI(Java naming and directory interface(Java命名和目录接口))
1. JNDI 目的是为了解藕，为了开发更加容易维护，容易扩展，容易部署的应用。 
2. JNDI 是一个sun提出的一个 j2ee 规范，具体的实现是各个 j2ee 容器提供商，sun 要求 j2ee 容器必须有 JNDI 功能。 
3. JNDI 在 j2ee 系统中的角色是交换机，是J2EE组件在运行时间接地查找其他组件、资源或服务的通用机制。 
4. JNDI 是通过资源的名字来查找的，资源的名字在整个 j2ee 应用/容器中是唯一的。 




