# kill
```text
dongzai1005@Dongzai1005-3:~$ kill -l
 1) SIGHUP	 2) SIGINT	   3) SIGQUIT	 4) SIGILL
 5) SIGTRAP	 6) SIGABRT	   7) SIGEMT	 8) SIGFPE
 9) SIGKILL	10) SIGBUS	  11) SIGSEGV	12) SIGSYS
13) SIGPIPE	14) SIGALRM	  15) SIGTERM	16) SIGURG
17) SIGSTOP	18) SIGTSTP	  19) SIGCONT	20) SIGCHLD
21) SIGTTIN	22) SIGTTOU	  23) SIGIO	    24) SIGXCPU
25) SIGXFSZ	26) SIGVTALRM 27) SIGPROF	28) SIGWINCH
29) SIGINFO	30) SIGUSR1	  31) SIGUSR2
```

其中前面31个信号为不可靠信号(非实时的，可能会出现信号的丢失)，后面的信号为可靠信号(实时的real_time,对信号排队，不会丢失)。

1) SIGHUP (挂起) 当运行进程的用户注销时通知该进程，使进程终止

2) SIGINT (中断) 当用户按下时,通知前台进程组终止进程

3) SIGQUIT (退出) 用户按下或时通知进程，使进程终止

4) SIGILL (非法指令) 执行了非法指令，如可执行文件本身出现错误、试图执行数据段、堆栈溢出

5) SIGTRAP 由断点指令或其它trap指令产生. 由debugger使用

6) SIGABRT (异常中止) 调用abort函数生成的信号

7) SIGBUS 非法地址, 包括内存地址对齐(alignment)出错. eg: 访问一个四个字长的整数, 但其地址不是4的倍数.

8) SIGFPE (算术异常) 发生致命算术运算错误,包括浮点运算错误、溢出及除数为0.

9) SIGKILL (确认杀死) 当用户通过kill -9命令向进程发送信号时，可靠的终止进程

10) SIGUSR1 用户使用

11) SIGSEGV (段越界) 当进程尝试访问不属于自己的内存空间导致内存错误时，终止进程

12) SIGUSR2 用户使用

13) SIGPIPE 写至无读进程的管道, 或者Socket通信SOCT_STREAM的读进程已经终止，而再写入。

14) SIGALRM (超时) alarm函数使用该信号，时钟定时器超时响应

15) SIGTERM (软中断) 使用不带参数的kill命令时终止进程

17) SIGCHLD (子进程结束) 当子进程终止时通知父进程

18) SIGCONT (暂停进程继续) 让一个停止(stopped)的进程继续执行. 本信号不能被阻塞.

19) SIGSTOP (停止) 作业控制信号,暂停停止(stopped)进程的执行. 本信号不能被阻塞, 处理或忽略.

20) SIGTSTP (暂停/停止) 交互式停止信号, Ctrl-Z 发出这个信号

21) SIGTTIN 当后台作业要从用户终端读数据时, 终端驱动程序产生SIGTTIN信号

22) SIGTTOU 当后台作业要往用户终端写数据时, 终端驱动程序产生SIGTTOU信号

23) SIGURG 有"紧急"数据或网络上带外数据到达socket时产生.

24) SIGXCPU 超过CPU时间资源限制. 这个限制可以由getrlimit/setrlimit来读取/改变。

25) SIGXFSZ 当进程企图扩大文件以至于超过文件大小资源限制。

26) SIGVTALRM 虚拟时钟信号. 类似于SIGALRM, 但是计算的是该进程占用的CPU时间.

27) SIGPROF (梗概时间超时) setitimer(2)函数设置的梗概统计间隔计时器(profiling interval timer)

28) SIGWINCH 窗口大小改变时发出.

29) SIGIO(异步I/O) 文件描述符准备就绪, 可以开始进行输入/输出操作.

30) SIGPWR 电源失效/重启动

31) SIGSYS 非法的系统调用。

程序不可捕获、阻塞或忽略的信号有：SIGKILL，SIGSTOP。
不能恢复至默认动作的信号有：SIGILL，SIGTRAP。
默认会导致进程退出的信号有：SIGALRM，SIGHUP，SIGINT，SIGKILL，SIGPIPE，SIGPOLL，SIGPROF，SIGSYS，SIGTERM，SIGUSR1，SIGUSR2，SIGVTALRM。
默认会导致进程停止的信号有：SIGSTOP，SIGTSTP，SIGTTIN，SIGTTOU。
默认进程忽略的信号有：SIGCHLD，SIGPWR，SIGURG，SIGWINCH。

