# jvm调优工具
> jps
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
$ jstat -class 1056
Loaded  Bytes  Unloaded  Bytes     Time
  3386  6459.8        0     0.0       1.07
  
Loaded:加载class的数量
Bytes：所占用空间大小
Unloaded：未加载数量
Bytes:未加载占用空间
Time：时间

编译统计
$ jstat -compiler 1056
Compiled Failed Invalid   Time   FailedType FailedMethod
    1626      1       0     2.93          1 java/net/URL <init>
Compiled：编译数量。
Failed：失败数量
Invalid：不可用数量
Time：时间
FailedType：失败类型
FailedMethod：失败的方法

垃圾回收统计
$ jstat -gc 1056
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
5120.0 5120.0  0.0   5088.1 33280.0  19947.2   87552.0     2973.9   19456.0 18986.5 2304.0 2142.8      3    0.017   0      0.000    0.017

S0C：survivor0的内存大小
S1C：survivor1的内存大小
S0U：survivor0的内存使用大小
S1U：survivor1的内存使用大小
EC：eden区的大小
EU：eden区的使用大小
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
$ jstat -gccapacity 1056
 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
 43520.0 238592.0  76800.0 5120.0 5120.0  33280.0    87552.0   478208.0    87552.0    87552.0      0.0 1067008.0  19456.0      0.0 1048576.0   2304.0      3     0

NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0C：survivor0的内存大小
S1C：survivor1的内存大小
EC：eden区的大小
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
$ jstat -gcnew 1056
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
5120.0 5120.0    0.0 5088.1  7  15 5120.0  33280.0  19947.2      3    0.017

S0C：survivor0的内存大小
S1C：survivor1的内存大小
S0U：survivor0的内存使用大小
S1U：survivor1的内存使用大小
TT:对象在新生代存活的次数
MTT:对象在新生代存活的最大次数
DSS:期望的幸存区大小
EC：eden区的内存大小
EU：eden区的内存使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间

$ jstat -gcnewcapacity 1056
  NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC
   43520.0   238592.0    76800.0  79360.0   5120.0  79360.0   5120.0   237568.0    33280.0     3     0

NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0CMX：最大survivor0内存大小
S0C：survivor0的内存大小
S1CMX：最大survivor1内存大小
S1C：survivor1的内存大小
ECMX：最大eden区大小
EC：当前eden区大小
YGC：年轻代垃圾回收次数
FGC：老年代回收次数

老年代垃圾回收统计
$ jstat -gcold 1056
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
$ jstat -gcoldcapacity 1056
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
$ jstat -gcmetacapacity 1056
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
$ jstat -gcutil 1056
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
  0.00  99.38  59.94   3.40  97.59  93.00      3    0.017     0    0.000    0.017
S0：survivor0区当前使用比例
S1：survivor1区当前使用比例
E：eden区使用比例
O：老年代使用比例
M：元数据区使用比例
CCS：压缩使用比例
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间


JVM编译方法统计
$ jstat -printcompilation 1056
Compiled  Size  Type Method
    1626    896    1 io/netty/channel/nio/NioEventLoop select

Compiled：最近编译方法的数量
Size：最近编译方法的字节码数量
Type：最近编译方法的编译类型。
Method：方法名标识。
```

> jmap
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

dump: 生成堆转储快照
finalizerinfo: 显示在F-Queue队列等待Finalizer线程执行finalizer方法的对象
heap: 显示Java堆详细信息
histo: 显示堆中对象的统计信息
permstat: to print permanent generation statistics
F: 当-dump没有响应时，强制生成dump快照

1、jmap -histo[:live] <pid>
通过histo选项，打印当前java堆中各个对象的数量、大小。
如果添加了live，只会打印活跃的对象。

2、jmap -dump:[live,]format=b,file=<filename> <pid>
通过-dump选项，把java堆中的对象dump到本地文件，然后使用MAT进行分析。
如果添加了live，只会dump活跃的对象。

3、jmap -heap <pid>(root用户执行)
通过-heap选项，打印java堆的配置情况和使用情况，还有使用的GC算法。

4、jmap -finalizerinfo <pid>(root用户执行)
通过-finalizerinfo选项，打印那些正在等待执行finalize方法的对象。

5、jmap -permstat <pid>
通过-permstat选项，打印java堆永久代的信息，包括class loader相关的信息,和interned Strings的信息。    

example:
$ jmap -dump:live,file=dump.dat 1056

```

> jhat
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
-J<flag> 因为jhat命令实际上会启动一个JVM来执行, 通过 -J 可以在启动JVM时传入一些启动参数. 例如, -J-Xmx512m 则指定运行 jhat 的Java虚拟机使用的最大堆内存为 512 MB. 如果需要使用多个JVM启动参数,则传入多个 -Jxxxxxx.

example:
$ jhat dump.dat

web ui:
http://localhost:7000/
```

> jstack
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


$ printf '%x\n' 1056
1b3b

$ jstack 1056 | grep 1b3b -A 20

$ jstack 1056
2019-01-12 10:40:04
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.172-b11 mixed mode):

"Attach Listener" #46 daemon prio=9 os_prio=31 tid=0x00007fe688ca4000 nid=0xe0b runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"DestroyJavaVM" #45 prio=5 os_prio=31 tid=0x00007fe68a06b000 nid=0x2703 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"http-nio-8000-AsyncTimeout" #43 daemon prio=5 os_prio=31 tid=0x00007fe68a60a800 nid=0x6703 waiting on condition [0x000070000a209000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at org.apache.coyote.AbstractProtocol$AsyncTimeout.run(AbstractProtocol.java:1149)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8000-Acceptor-0" #42 daemon prio=5 os_prio=31 tid=0x00007fe68648d800 nid=0x8e03 runnable [0x000070000a106000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.ServerSocketChannelImpl.accept0(Native Method)
	at sun.nio.ch.ServerSocketChannelImpl.accept(ServerSocketChannelImpl.java:422)
	at sun.nio.ch.ServerSocketChannelImpl.accept(ServerSocketChannelImpl.java:250)
	- locked <0x00000007be06e350> (a java.lang.Object)
	at org.apache.tomcat.util.net.NioEndpoint$Acceptor.run(NioEndpoint.java:482)
	at java.lang.Thread.run(Thread.java:748)

"http-nio-8000-ClientPoller-1" #41 daemon prio=5 os_prio=31 tid=0x00007fe6860bd000 nid=0x6403 runnable [0x000070000a003000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.KQueueArrayWrapper.kevent0(Native Method)
	at sun.nio.ch.KQueueArrayWrapper.poll(KQueueArrayWrapper.java:198)
	at sun.nio.ch.KQueueSelectorImpl.doSelect(KQueueSelectorImpl.java:117)
	at sun.nio.ch.SelectorImpl.lockAndDoSelect(SelectorImpl.java:86)
	- locked <0x0000000795f0d1a0> (a sun.nio.ch.Util$3)
	- locked <0x0000000795f0d190> (a java.util.Collections$UnmodifiableSet)
	- locked <0x0000000795f0d070> (a sun.nio.ch.KQueueSelectorImpl)
	at sun.nio.ch.SelectorImpl.select(SelectorImpl.java:97)
	at org.apache.tomcat.util.net.NioEndpoint$Poller.run(NioEndpoint.java:825)
	at java.lang.Thread.run(Thread.java:748)
```

> jinfo
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

-flag: 输出指定args参数的值
-flags: 不需要args参数，输出所有JVM参数的值
-sysprops: 输出系统属性，等同于System.getProperties()
```

# 常用配置
> 打印GC日志

-XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:<filename>

> -Xmx2048m -Xms2048m -Xmn1g -Xss512k -XX:SurvivorRatio=8 -XX:+UseParallelGC -XX:MaxTenuringThreshold=4 -XX:ParallelGCThreads=43
```text
-Xmx2048m 最大堆内存2G（之前默认是1G）
-Xms2048m 初始堆内存（建议跟-Xmx设置成一样，避免gc后调整）
-Xmn1g  年轻代内存1G
-Xss512k 每个栈大小为512K（设置小点，可以创建更多的线程，设置太小 会栈溢出）
-XX:SurvivorRatio=8  算法(s0+s1)/eden=2:8，即s0=s1=1/10 * 1G=102.4M
-XX:+UseParallelGC 开启并行gc（我们是后端服务，需要大吞吐量）
-XX:MaxTenuringThreshold=4  年轻代对象年龄为4
-XX:ParallelGCThreads  gc线程数，此值最好配置与处理器数目相等
```


# 问题
1. Error attaching to process: sun.jvm.hotspot.debugger.DebuggerException：不允许的操作
> $ echo 0 | sudo tee /proc/sys/kernel/yama/ptrace_scope
