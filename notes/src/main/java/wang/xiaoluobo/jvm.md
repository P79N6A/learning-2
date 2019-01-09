# jvm jdk7
>JVM内存结构主要有三大块：堆内存、方法区和栈。
1. 堆内存由年轻代和老年代组成，年轻代内存又被分成三部分，Eden空间、From Survivor空间、To Survivor空间。年轻代默认分配比例8:1:1
2. 方法区存储类信息、常量、静态变量等数据，是线程共享的区域，Non-Heap(非堆)
3. 栈分为java虚拟机栈和本地方法栈，主要用于方法的执行

> 控制参数
- -Xms设置堆的最小空间大小。
- -Xmx设置堆的最大空间大小。
- -XX:NewSize设置新生代最小空间大小。
- -XX:MaxNewSize设置新生代最大空间大小。
- -XX:PermSize设置永久代最小空间大小。
- -XX:MaxPermSize设置永久代最大空间大小。
- -Xss设置每个线程的堆栈大小。
> 老年代空间大小=堆空间大小-年轻代大空间大小


### 一、 Java堆(Heap)
### 二、 方法区(Method Area)
### 三、 程序计数器(Program Counter Register)
### 四、 JVM栈(JVM Stacks)
### 五、 本地方法栈(Native Method Stacks)


# jvm jdk8
