# jvm jdk7
![jvm01.png](./images/jvm01.png)

![jvm02.png](./images/jvm02.png)

> JVM内存结构主要有三大块：堆内存、方法区和栈。
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


### 一、Java堆(Heap)
- 线程共享，整个Java虚拟机只有一个堆，所有的线程都访问同一个堆。而程序计数器、Java虚拟机栈、本地方法栈都是一个线程对应一个。
- 在虚拟机启动时创建。
- 是垃圾回收的主要场所。
- 新生代(Eden区，From Survior和To Survivor)、老年代。

### 二、方法区(Method Area)
- 被虚拟机加载的类信息
- 常量
- 静态变量
- 即时编译器编译后的代码

> 特点
- 线程共享
- 持久代
- 内存回收效率低
- Java虚拟机规范对方法区的要求比较宽松

### 三、程序计数器(Program Counter Register)
- 是一块较小的内存空间。
- 线程私有，每条线程都有自己的程序计数器。
- 生命周期：随着线程的创建而创建，随着线程的结束而销毁。
- 唯一一个不会出现OutOfMemoryError的内存区域。

### 四、JVM栈(JVM Stacks)
描述Java方法运行过程的内存模型。

> 特点
- 局部变量表随着栈帧的创建而创建，它的大小在编译时确定，创建时只需分配事先规定的大小即可。在方法运行过程中，局部变量表的大小不会发生改变。
- Java虚拟机栈会出现两种异常：StackOverFlowError和OutOfMemoryError。
- Java虚拟机栈是线程私有，随着线程创建而创建，随着线程的结束而销毁。

### 五、本地方法栈(Native Method Stacks)
>本地方法栈是为JVM运行Native方法准备的空间，它与Java虚拟机栈实现的功能类似，只不过本地方法栈是描述本地方法运行过程的内存模型。


# jvm jdk8
![jvm03.png](images/jvm03.png)

![jvm04.png](images/jvm04.png)

> 元数据区内存不在JVM中，而是使用的本地内存，默认情况下受操作系统内存限制。调整元数据区内存大小的参数 -XX:MetaspaceSize -XX:MaxMetaspaceSize

> -Xms和-Xmx用于设置堆内存的大小

> -XX:NewSize和-XX:MaxNewSize用于设置年轻代的大小，建议设为整个堆大小的1/3或者1/4，两个值设为一样大。

> -XX:SurvivorRatio用于设置Eden和其中一个Survivor的比值

> -XX:NewRatio=3代表新生代和老年代的比例为1:3

# gc
![jvm05.png](images/jvm05.png)

### 一、GC主要分新生代GC和老年代GC
1. 新生代GC：串行GC、并行GC、并行回收GC
2. 老年代GC：串行GC、并行GC、CMS    
3. G1比较特殊，同时支持新生代和老年代

### 二、吞吐量优先和暂停时间优先，
1. 吞吐量优先  
采用server默认的并行GC(Parallel GC)方式(蓝色区域)
2. 暂停时间优先  
选用并发GC(CMS)方式(黄色区域)，常用场景：互联网、电商类

### 三、常用GC开启方式
```text
1. 暂停时间优先: 并行GC+CMS
开启方式[ -XX:+UseConcMarkSweepGC -XX:+UseParNewGC ]

2. 吞吐量优先: 并行回收GC + 并行GC
开启方式 [ -XX:+UseParallelOldGC ]，server模式默认的配置

3. G1: [ -XX:+UseG1GC ]
适用于服务器端、大内存、多CPU情景的垃圾收集器
G1的目标是在维持高效率回收的同时，提供软实时中断特性
常用场景：hadoop、elasticsearch
```

### 四、CMS
1. CMS(Concurrent Mark Sweep)收集器是一种以获取最短回收停顿时间为目标的收集器
2. CMS垃圾回收步骤
    - 初始标记（CMS initial mark）
    - 并发标记（CMS concurrent mark）
    - 重新标记（CMS remark）
    - 并发清除（CMS concurrent sweep）

3. 优缺点  
   - 优点: 并发收集、低停顿
   - 缺点: 产生大量空间碎片、并发阶段会降低吞吐量

4. 参数控制：
    - -XX:+UseConcMarkSweepGC 使用CMS收集器
    - -XX:+UseCMSCompactAtFullCollection Full GC后，进行一次碎片整理；整理过程是独占的，会引起停顿时间变长
    - -XX:+CMSFullGCsBeforeCompaction 设置进行几次Full GC后，进行一次碎片整理
    - -XX:ParallelCMSThreads 设定CMS的线程数量（一般情况约等于可用CPU数量）

### 五、G1
1. 特点
    - 空间整合
    - 可预测停顿

2. G1垃圾回收步骤
    1. 标记阶段，首先初始标记(Initial-Mark),这个阶段是停顿的(Stop the World Event)，并且会触发一次普通Mintor GC。对应GC log:GC pause (young) (inital-mark)
    2. Root Region Scanning，程序运行过程中会回收survivor区(存活到老年代)，这一过程必须在young GC之前完成。
    3. Concurrent Marking，在整个堆中进行并发标记(和应用程序并发执行)，此过程可能被young GC中断。在并发标记阶段，若发现区域对象中的所有对象都是垃圾，那个这个区域会被立即回收(图中打X)。同时，并发标记过程中，会计算每个区域的对象活性(区域中存活对象的比例)。
    4. Remark, 再标记，会有短暂停顿(STW)。再标记阶段是用来收集 并发标记阶段 产生新的垃圾(并发阶段和应用程序一同运行)；G1中采用了比CMS更快的初始快照算法:snapshot-at-the-beginning (SATB)。
    5. Copy/Clean up，多线程清除失活对象，会有STW。G1将回收区域的存活对象拷贝到新区域，清除Remember Sets，并发清空回收区域并把它返回到空闲区域链表中。
    6. 复制/清除过程后。回收区域的活性对象已经被集中回收到深蓝色和深绿色区域。

### 六、CMS和G1区别
```text
CMS堆->年轻代老年代
Cms标记清理算法

G1 压缩复制算法，不产生碎片
G1堆->多个区->每个区里(年轻代老年代)
G1 时间停顿可设置，相关参数[ -XX:MaxGCPauseMillis=100 -XX:GCPauseIntervalMillis=200 ]
```

# jvm调优工具
### command(jdk1.8)
> JPS
```text
JVM Process Status Tool，显示指定系统内所有的HotSpot虚拟机进程
jps [options] [hostid]

options:
-l : 输出主类全名或jar路径
-q : 只输出LVMID
-m : 输出JVM启动时传递给main()的参数
-v : 输出JVM启动时显示指定的JVM参数

example:
$ jps -ml
1056 org.apache.zookeeper.server.quorum.QuorumPeerMain /Users/dongzai1005/soft/zookeeper-3.4.13/bin/../conf/zoo.cfg
2384 org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode
3588
2277 org.apache.hadoop.hdfs.server.datanode.DataNode
3655 org.jetbrains.idea.maven.server.RemoteMavenServer
2186 org.apache.hadoop.hdfs.server.namenode.NameNode
3884 sun.tools.jps.Jps -ml
```
> jstat
```text
jstat(JVM statistics Monitoring)是用于监视虚拟机运行时状态信息的命令，它可以显示出虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据。

$ jstat -help
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      An option reported by the -options option
  <vmid>        Virtual Machine Identifier. A vmid takes the following form:
                     <lvmid>[@<hostname>[:<port>]]
                Where <lvmid> is the local vm identifier for the target
                Java virtual machine, typically a process id; <hostname> is
                the name of the host running the target Java virtual machine;
                and <port> is the port number for the rmiregistry on the
                target host. See the jvmstat documentation for a more complete
                description of the Virtual Machine Identifier.
  <lines>       Number of samples between header lines.
  <interval>    Sampling interval. The following forms are allowed:
                    <n>["ms"|"s"]
                Where <n> is an integer and the suffix specifies the units as
                milliseconds("ms") or seconds("s"). The default units are "ms".
  <count>       Number of samples to take before terminating.
  -J<flag>      Pass <flag> directly to the runtime system.

$ jstat -options
-class
-compiler
-gc
-gccapacity
-gccause
-gcmetacapacity
-gcnew
-gcnewcapacity
-gcold
-gcoldcapacity
-gcutil
-printcompilation

example:
类加载统计
$ jstat -class 17649
Loaded  Bytes  Unloaded  Bytes     Time
  3386  6459.8        0     0.0       1.07
  
Loaded:加载class的数量
Bytes：所占用空间大小
Unloaded：未加载数量
Bytes:未加载占用空间
Time：时间

编译统计
$ jstat -compiler 17649
Compiled Failed Invalid   Time   FailedType FailedMethod
    1626      1       0     2.93          1 java/net/URL <init>
Compiled：编译数量。
Failed：失败数量
Invalid：不可用数量
Time：时间
FailedType：失败类型
FailedMethod：失败的方法

垃圾回收统计
$ jstat -gc 17649
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
5120.0 5120.0  0.0   5088.1 33280.0  19947.2   87552.0     2973.9   19456.0 18986.5 2304.0 2142.8      3    0.017   0      0.000    0.017

S0C：第一个幸存区的大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
OC：老年代大小
OU：老年代使用大小
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

堆内存统计
$ jstat -gccapacity 17649
 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
 43520.0 238592.0  76800.0 5120.0 5120.0  33280.0    87552.0   478208.0    87552.0    87552.0      0.0 1067008.0  19456.0      0.0 1048576.0   2304.0      3     0

NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0C：第一个幸存区大小
S1C：第二个幸存区的大小
EC：伊甸园区的大小
OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC:当前老年代大小
MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代gc次数
FGC：老年代GC次数

新生代垃圾回收统计
$ jstat -gcnew 17649
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
5120.0 5120.0    0.0 5088.1  7  15 5120.0  33280.0  19947.2      3    0.017

S0C：第一个幸存区大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
TT:对象在新生代存活的次数
MTT:对象在新生代存活的最大次数
DSS:期望的幸存区大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间

$ jstat -gcnewcapacity 17649
  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC
   43520.0   238592.0    76800.0  79360.0   5120.0  79360.0   5120.0   237568.0    33280.0     3     0

NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0CMX：最大幸存1区大小
S0C：当前幸存1区大小
S1CMX：最大幸存2区大小
S1C：当前幸存2区大小
ECMX：最大伊甸园区大小
EC：当前伊甸园区大小
YGC：年轻代垃圾回收次数
FGC：老年代回收次数

老年代垃圾回收统计
$ jstat -gcold 17649
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT
 19456.0  18986.5   2304.0   2142.8     87552.0      2973.9      3     0    0.000    0.017

MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
OC：老年代大小
OU：老年代使用大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

老年代内存统计
$ jstat -gcoldcapacity 17649
   OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT
    87552.0    478208.0     87552.0     87552.0     3     0    0.000    0.017

OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC：老年代大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间

元数据空间统计
$ jstat -gcmetacapacity 17649
   MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT
       0.0  1067008.0    19456.0        0.0  1048576.0     2304.0     3     0    0.000    0.017

MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间


总结垃圾回收统计
$ jstat -gcutil 17649
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
  0.00  99.38  59.94   3.40  97.59  93.00      3    0.017     0    0.000    0.017
S0：幸存1区当前使用比例
S1：幸存2区当前使用比例
E：伊甸园区使用比例
O：老年代使用比例
M：元数据区使用比例
CCS：压缩使用比例
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间


JVM编译方法统计
$ jstat -printcompilation 17649
Compiled  Size  Type Method
    1626    896    1 io/netty/channel/nio/NioEventLoop select

Compiled：最近编译方法的数量
Size：最近编译方法的字节码数量
Type：最近编译方法的编译类型。
Method：方法名标识。
```

```text
jmap(JVM Memory Map)命令用于生成heap dump文件，如果不使用这个命令，还阔以使用-XX:+HeapDumpOnOutOfMemoryError参数来让虚拟机出现OOM的时候·自动生成dump文件。 jmap不仅能生成dump文件，还阔以查询finalize执行队列、Java堆和永久代的详细信息，如当前使用率、当前使用的是哪种收集器等。

$ jmap -help
Usage:
    jmap [option] <pid>
        (to connect to running process)
    jmap [option] <executable <core>
        (to connect to a core file)
    jmap [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    <none>               to print same info as Solaris pmap
    -heap                to print java heap summary
    -histo[:live]        to print histogram of java object heap; if the "live"
                         suboption is specified, only count live objects
    -clstats             to print class loader statistics
    -finalizerinfo       to print information on objects awaiting finalization
    -dump:<dump-options> to dump java heap in hprof binary format
                         dump-options:
                           live         dump only live objects; if not specified,
                                        all objects in the heap are dumped.
                           format=b     binary format
                           file=<file>  dump heap to <file>
                         Example: jmap -dump:live,format=b,file=heap.bin <pid>
    -F                   force. Use with -dump:<dump-options> <pid> or -histo
                         to force a heap dump or histogram when <pid> does not
                         respond. The "live" suboption is not supported
                         in this mode.
    -h | -help           to print this help message
    -J<flag>             to pass <flag> directly to the runtime system
    
dump : 生成堆转储快照
finalizerinfo : 显示在F-Queue队列等待Finalizer线程执行finalizer方法的对象
heap : 显示Java堆详细信息
histo : 显示堆中对象的统计信息
permstat : to print permanent generation statistics
F : 当-dump没有响应时，强制生成dump快照

1、jmap -histo[:live] <pid>
通过histo选项，打印当前java堆中各个对象的数量、大小。
如果添加了live，只会打印活跃的对象。

2、jmap -dump:[live,]format=b,file=<filename> <pid>
通过-dump选项，把java堆中的对象dump到本地文件，然后使用MAT进行分析。
如果添加了live，只会dump活跃的对象。

3、jmap -heap <pid>
通过-heap选项，打印java堆的配置情况和使用情况，还有使用的GC算法。

4、jmap -finalizerinfo <pid>
通过-finalizerinfo选项，打印那些正在等待执行finalize方法的对象。

5、jmap -permstat <pid>
通过-permstat选项，打印java堆永久代的信息，包括class loader相关的信息,和interned Strings的信息。    
```

```text
jhat(JVM Heap Analysis Tool)命令是与jmap搭配使用，用来分析jmap生成的dump，jhat内置了一个微型的HTTP/HTML服务器，生成dump的分析结果后，可以在浏览器中查看。在此要注意，一般不会直接在服务器上进行分析，因为jhat是一个耗时并且耗费硬件资源的过程，一般把服务器生成的dump文件复制到本地或其他机器上进行分析。

$ jhat -h
Usage:  jhat [-stack <bool>] [-refs <bool>] [-port <port>] [-baseline <file>] [-debug <int>] [-version] [-h|-help] <file>

	-J<flag>          Pass <flag> directly to the runtime system. For
			  example, -J-mx512m to use a maximum heap size of 512MB
	-stack false:     Turn off tracking object allocation call stack.
	-refs false:      Turn off tracking of references to objects
	-port <port>:     Set the port for the HTTP server.  Defaults to 7000
	-exclude <file>:  Specify a file that lists data members that should
			  be excluded from the reachableFrom query.
	-baseline <file>: Specify a baseline object dump.  Objects in
			  both heap dumps with the same ID and same class will
			  be marked as not being "new".
	-debug <int>:     Set debug level.
			    0:  No debug output
			    1:  Debug hprof file parsing
			    2:  Debug hprof file parsing, no server
	-version          Report version number
	-h|-help          Print this help and exit
	<file>            The file to read

For a dump file that contains multiple heap dumps,
you may specify which dump in the file
by appending "#<number>" to the file name, i.e. "foo.hprof#3".

All boolean options default to "true"


-stack false|true 关闭对象分配调用栈跟踪(tracking object allocation call stack)。 如果分配位置信息在堆转储中不可用. 则必须将此标志设置为 false. 默认值为 true.>
-refs false|true 关闭对象引用跟踪(tracking of references to objects)。 默认值为 true. 默认情况下, 返回的指针是指向其他特定对象的对象,如反向链接或输入引用(referrers or incoming references), 会统计/计算堆中的所有对象。>
-port port-number 设置 jhat HTTP server 的端口号. 默认值 7000.>
-exclude exclude-file 指定对象查询时需要排除的数据成员列表文件(a file that lists data members that should be excluded from the reachable objects query)。 例如, 如果文件列列出了 java.lang.String.value , 那么当从某个特定对象 Object o 计算可达的对象列表时, 引用路径涉及 java.lang.String.value 的都会被排除。>
-baseline exclude-file 指定一个基准堆转储(baseline heap dump)。 在两个 heap dumps 中有相同 object ID 的对象会被标记为不是新的(marked as not being new). 其他对象被标记为新的(new). 在比较两个不同的堆转储时很有用.>
-debug int 设置 debug 级别. 0 表示不输出调试信息。 值越大则表示输出更详细的 debug 信息.>
-version 启动后只显示版本信息就退出>
-J<flag> 因为 jhat 命令实际上会启动一个JVM来执行, 通过 -J 可以在启动JVM时传入一些启动参数. 例如, -J-Xmx512m 则指定运行 jhat 的Java虚拟机使用的最大堆内存为 512 MB. 如果需要使用多个JVM启动参数,则传入多个 -Jxxxxxx.
```


```text
jstack用于生成java虚拟机当前时刻的线程快照。线程快照是当前java虚拟机内每一条线程正在执行的方法堆栈的集合，生成线程快照的主要目的是定位线程出现长时间停顿的原因，如线程间死锁、死循环、请求外部资源导致的长时间等待等。 线程出现停顿的时候通过jstack来查看各个线程的调用堆栈，就可以知道没有响应的线程到底在后台做什么事情，或者等待什么资源。 如果java程序崩溃生成core文件，jstack工具可以用来获得core文件的java stack和native stack的信息，从而可以轻松地知道java程序是如何崩溃和在程序何处发生问题。另外，jstack工具还可以附属到正在运行的java程序中，看到当时运行的java程序的java stack和native stack的信息, 如果现在运行的java程序呈现hung的状态，jstack是非常有用的。

$ jstack -h
Usage:
    jstack [-l] <pid>
        (to connect to running process)
    jstack -F [-m] [-l] <pid>
        (to connect to a hung process)
    jstack [-m] [-l] <executable> <core>
        (to connect to a core file)
    jstack [-m] [-l] [server_id@]<remote server IP or hostname>
        (to connect to a remote debug server)

Options:
    -F  to force a thread dump. Use when jstack <pid> does not respond (process is hung)
    -m  to print both java and native frames (mixed mode)
    -l  long listing. Prints additional information about locks
    -h or -help to print this help message
    
-F : 当正常输出请求不被响应时，强制输出线程堆栈
-l : 除堆栈外，显示关于锁的附加信息
-m : 如果调用到本地方法的话，可以显示C/C++的堆栈    
```



```text
jinfo(JVM Configuration info)这个命令作用是实时查看和调整虚拟机运行参数。 之前的jps -v口令只能查看到显示指定的参数，如果想要查看未被显示指定的参数的值就要使用jinfo口令

$ jinfo -h
Usage:
    jinfo [option] <pid>
        (to connect to running process)
    jinfo [option] <executable <core>
        (to connect to a core file)
    jinfo [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    -flag <name>         to print the value of the named VM flag
    -flag [+|-]<name>    to enable or disable the named VM flag
    -flag <name>=<value> to set the named VM flag to the given value
    -flags               to print VM flags
    -sysprops            to print Java system properties
    <no option>          to print both of the above
    -h | -help           to print this help message

-flag : 输出指定args参数的值
-flags : 不需要args参数，输出所有JVM参数的值
-sysprops : 输出系统属性，等同于System.getProperties()
```