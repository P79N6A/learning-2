# [jdk version 1.8](https://www.oracle.com/technetwork/cn/articles/java/index.html)

## AbstractQueuedSynchronizer(AQS 框架)
AQS 框架是 J.U.C 中实现锁及同步机制的基础，其底层是通过调用 LockSupport#unpark()和 LockSupport#park() 实现线程的阻塞和唤醒。

AbstractQueuedSynchronizer 是一个抽象类，主要是维护了一个int类型的state属性和一个非阻塞、先进先出的线程等待队列；
其中 state 是用 volatile 修饰的，保证线程之间的可见性，队列的入队和出对操作都是无锁操作，基于自旋锁和 CAS 实现；
另外AQS分为两种模式：独占模式和共享模式，ReentrantLock是基于独占模式模式实现的，CountDownLatch、CyclicBarrier等是基于共享模式。

