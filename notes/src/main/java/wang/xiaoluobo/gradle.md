## [gradle5.0](http://services.gradle.org/distributions/gradle-5.0-bin.zip)

```sbtshell
$ unzip -q gradle-5.0-bin.zip

$ vim .bash_profile
export GRADLE_HOME=/Users/wyd/soft/gradle-5.0
export PATH=$PATH:$GRADLE_HOME
$ source .bash_profile

$ gradle -v

Welcome to Gradle 5.0!

Here are the highlights of this release:
 - Kotlin DSL 1.0
 - Task timeouts
 - Dependency alignment aka BOM support
 - Interactive `gradle init`

For more details see https://docs.gradle.org/5.0/release-notes.html


------------------------------------------------------------
Gradle 5.0
------------------------------------------------------------

Build time:   2018-11-26 11:48:43 UTC
Revision:     7fc6e5abf2fc5fe0824aec8a0f5462664dbcd987

Kotlin DSL:   1.0.4
Kotlin:       1.3.10
Groovy:       2.5.4
Ant:          Apache Ant(TM) version 1.9.13 compiled on July 10 2018
JVM:          1.8.0_172 (Oracle Corporation 25.172-b11)
OS:           Mac OS X 10.14 x86_64
```