# spring version 5.1.2.RELEASE
## spring架构
- Core Container
    - Core
    - Beans
    - Context
    - Expression Language
- Data Access/Integration
    - JDBC
    - ORM
    - OXM
    - JMS
    - Transaction
- Web
    - Web
    - Servlet
    - Portlet
- AOP
    - Aspects
    - Instrumentation
- Test
    - JUnit
    - TestNG

## spring启动过程
核心方法：ClassPathXmlApplicationContext中refresh方法

# spring boot version 2.1.0.RELEASE
## spring boot启动过程
SpringApplication的run方法

## spring boot 创建 tomcat server
1. [TomcatReactiveWebServerFactory](./boot/web/embedded/tomcat/TomcatReactiveWebServerFactory.java#getWebServer())
2. [TomcatServletWebServerFactory](./boot/web/embedded/tomcat/TomcatServletWebServerFactory.java#getWebServer())