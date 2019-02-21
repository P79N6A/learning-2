# mybatis-generator
> 使用方法
1. mvn install mybatis-generator
2. 依赖 mybatis-generator.jar
3. 修改配置文件
    1. [generatorConfig.properties](../web/src/main/resources/generatorConfig.properties)
    2. [generatorConfig.xml](../web/src/main/resources/generatorConfig.xml)
4. pom 文件配置
    ```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.7</version>
                <configuration>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>wang.xiaoluobo</groupId>
                        <artifactId>mybatis-generator</artifactId>
                        <version>${project.version}</version>
                    </dependency>
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>${mysql.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
    ```
5. 使用 mybatis-generator 插件反向生成代码 
> Maven Projects -> learning -> Modules -> web -> mybatis-generator -> mybatis-generator:generate