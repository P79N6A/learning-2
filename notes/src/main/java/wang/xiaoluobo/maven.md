### maven

- [versions plugin](https://www.mojohaus.org/versions-maven-plugin/)
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>2.5</version>
</plugin>
```
```text
使用方法：
mvn versions:set -DnewVersion=0.0.1
mvn versions:commit
mvn versions:rollback
```



