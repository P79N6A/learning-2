/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.common.Time;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.server.ZooKeeperThread;
import org.apache.zookeeper.server.quorum.QuorumCnxManager.Message;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.QuorumPeer.ServerState;
import org.apache.zookeeper.server.util.ZxidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of leader election using TCP. It uses an object of the class
 * QuorumCnxManager to manage connections. Otherwise, the algorithm is push-based
 * as with the other UDP implementations.
 * <p>
 * There are a few parameters that can be tuned to change its behavior. First,
 * finalizeWait determines the amount of time to wait until deciding upon a leader.
 * This is part of the leader election algorithm.
 * <p>
 * 使用TCP实现领导者选举。它使用类QuorumCnxManager来管理连接。 否则，该算法是基于推送的与其他UDP实现一样。
 * <p>
 * 可以调整一些参数来改变其行为。 第一，finalizeWait确定等待决定领导者的时间。这是领导者选举算法的一部分。
 * <p>
 * zk fast leader election(优化Paxos算法)
 * <p>
 * zk版本3.4.13
 */
public class FastLeaderElection implements Election {

    private static final Logger LOG = LoggerFactory.getLogger(FastLeaderElection.class);

    /**
     * Determine how much time a process has to wait
     * once it believes that it has reached the end of
     * leader election.
     * <p>
     * 确定进程必须等待的时间，一旦它认为已经到了就结束领导人选举。
     * <p>
     * zk默认选举等待时间为200ms
     */
    final static int finalizeWait = 200;


    /**
     * Upper bound on the amount of time between two consecutive
     * notification checks. This impacts the amount of time to get
     * the system up again after long partitions. Currently 60 seconds.
     * <p>
     * 两个连续之间的时间量的上限通知检查。 这会影响获得的时间长分区后系统再次启动。 目前60秒。
     * <p>
     * zk默认60s
     */
    final static int maxNotificationInterval = 60000;

    /**
     * Connection manager. Fast leader election uses TCP for
     * communication between peers, and QuorumCnxManager manages
     * such connections.
     * <p>
     * 连接管理者。快速领导者选举使用TCP对zk Server之间的通信，QuorumCnxManager管理这种连接。
     */
    QuorumCnxManager manager;


    /**
     * Notifications are messages that let other peers know that
     * a given peer has changed its vote, either because it has
     * joined leader election or because it learned of another
     * peer with higher zxid or same zxid and higher server id
     * <p>
     * 通知是允许其他zk Server知道的消息一个特定的同伴已经改变了投票，
     * 因为它已经改变了加入领导人选举或因为它了解了另一个具有更高zxid或相同zxid和更高服务器ID的对等体
     */
    static public class Notification {
        /**
         * Format version, introduced in 3.4.6
         */
        public final static int CURRENTVERSION = 0x1;

        /**
         * 版本号
         */
        int version;

        /**
         * Proposed leader
         * <p>
         * 推荐的leader
         */
        long leader;

        /**
         * zxid of the proposed leader
         * <p>
         * zk选举时使用的id，此id集群中每次选举唯一并且自增
         * zk选举时，会比较此值，同票时，zxid较大者胜出
         */
        long zxid;

        /**
         * Epoch
         * 选举周期
         * zk fast paxos优先判断epoch，然后再比较zxid，最后再比较server id
         */
        long electionEpoch;

        /**
         * current state of sender
         * <p>
         * zk Server当前状态
         * <p>
         * zk Server一共有四种状态
         * LOOKING, FOLLOWING, LEADING, OBSERVING
         * <p>
         * LOOKING---启动默认为looking状态
         * FOLLOWING-参与选举，但是不是leader的节点为following状态
         * LEADING---leader状态
         * OBSERVING-不参与选举的为observing
         */
        ServerState state;

        /**
         * Address of sender
         * <p>
         * 发送者的地址
         */
        long sid;

        /**
         * epoch of the proposed leader
         * 推荐leader周期
         */
        long peerEpoch;

        @Override
        public String toString() {
            return Long.toHexString(version) + " (message format version), "
                    + leader + " (n.leader), 0x"
                    + Long.toHexString(zxid) + " (n.zxid), 0x"
                    + Long.toHexString(electionEpoch) + " (n.round), " + state
                    + " (n.state), " + sid + " (n.sid), 0x"
                    + Long.toHexString(peerEpoch) + " (n.peerEpoch) ";
        }
    }

    /**
     * zk Server之间通信消息构建
     *
     * @param state
     * @param leader
     * @param zxid
     * @param electionEpoch
     * @param epoch
     * @return
     */
    static ByteBuffer buildMsg(int state,
                               long leader,
                               long zxid,
                               long electionEpoch,
                               long epoch) {
        byte requestBytes[] = new byte[40];
        ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);

        /**
         * Building notification packet to send
         *
         * 构建待发送通知包(消息包)
         */
        requestBuffer.clear();
        requestBuffer.putInt(state);
        requestBuffer.putLong(leader);
        requestBuffer.putLong(zxid);
        requestBuffer.putLong(electionEpoch);
        requestBuffer.putLong(epoch);
        requestBuffer.putInt(Notification.CURRENTVERSION);

        return requestBuffer;
    }

    /**
     * Messages that a peer wants to send to other peers.
     * These messages can be both Notifications and Acks
     * of reception of notification.
     * <p>
     * zk Server想要发送给其他zk Server的消息。这些消息可以是通知和Acks接收通知。
     */
    static public class ToSend {
        static enum mType {crequest, challenge, notification, ack}

        ToSend(mType type,
               long leader,
               long zxid,
               long electionEpoch,
               ServerState state,
               long sid,
               long peerEpoch) {

            this.leader = leader;
            this.zxid = zxid;
            this.electionEpoch = electionEpoch;
            this.state = state;
            this.sid = sid;
            this.peerEpoch = peerEpoch;
        }

        /**
         * Proposed leader in the case of notification
         */
        long leader;

        /*
         * id contains the tag for acks, and zxid for notifications
         */
        long zxid;

        /*
         * Epoch
         */
        long electionEpoch;

        /*
         * Current state;
         */
        ServerState state;

        /*
         * Address of recipient
         */
        long sid;

        /*
         * Leader epoch
         */
        long peerEpoch;
    }

    /**
     * 发送消息阻塞队列
     */
    LinkedBlockingQueue<ToSend> sendqueue;

    /**
     * 接收消息阻塞队列
     */
    LinkedBlockingQueue<Notification> recvqueue;

    /**
     * Multi-threaded implementation of message handler. Messenger
     * implements two sub-classes: WorkReceiver and  WorkSender. The
     * functionality of each is obvious from the name. Each of these
     * spawns a new thread.
     * <p>
     * 消息处理程序的多线程实现。消息的两个子类实现：WorkReceiver和WorkSender。
     * 从名称中就可以看出每个子类的功能。其中每一个子类都会产生一个新线程。
     */
    protected class Messenger {

        /**
         * Receives messages from instance of QuorumCnxManager on
         * method run(), and processes such messages.
         * <p>
         * 从QuorumCnxManager实例接收消息方法run()，并处理此类消息。
         */
        class WorkerReceiver extends ZooKeeperThread {
            volatile boolean stop;
            QuorumCnxManager manager;

            WorkerReceiver(QuorumCnxManager manager) {
                // 线程添加名字标识
                super("WorkerReceiver");
                this.stop = false;
                this.manager = manager;
            }

            public void run() {

                Message response;
                // while死循环，等待处理数据
                while (!stop) {
                    // Sleeps on receive
                    try {
                        // 每3秒拉取一次数据
                        response = manager.pollRecvQueue(3000, TimeUnit.MILLISECONDS);
                        if (response == null) continue;

                        /**
                         * If it is from an observer, respond right away.
                         * Note that the following predicate assumes that
                         * if a server is not a follower, then it must be
                         * an observer. If we ever have any other type of
                         * learner in the future, we'll have to change the
                         * way we check for observers.
                         *
                         * 如果请求来自观察者，则立即回复。(观察者不参与投票，故直接影响)
                         * 如果服务器不是关注者，那么它必是一个观察者。
                         * 如果我们有任何其他类型的未来的学习者，我们将不得不改变我们检查观察者的方式。
                         */
                        if (!validVoter(response.sid)) {
                            // 接收到是zk当前投票信息，则将当前节点投票结果发送给请求节点(即response.sid，加入发送队列)
                            Vote current = self.getCurrentVote();
                            ToSend notmsg = new ToSend(ToSend.mType.notification,
                                    current.getId(),
                                    current.getZxid(),
                                    logicalclock.get(),
                                    self.getPeerState(),
                                    response.sid,
                                    current.getPeerEpoch());

                            sendqueue.offer(notmsg);
                        } else {
                            // Receive new message
                            // 接收到的新投票信息
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Receive new notification message. My id = "
                                        + self.getId());
                            }

                            /*
                             * We check for 28 bytes for backward compatibility
                             *
                             * 为了向后兼容，检查应答信息是否大于等于28个字节
                             */
                            if (response.buffer.capacity() < 28) {
                                LOG.error("Got a short response: "
                                        + response.buffer.capacity());
                                continue;
                            }

                            boolean backCompatibility = (response.buffer.capacity() == 28);
                            response.buffer.clear();

                            // Instantiate Notification and set its attributes
                            // 实例化通知并设置其属性
                            Notification n = new Notification();

                            // State of peer that sent this message
                            // 发送此消息的对等状态
                            ServerState ackstate = ServerState.LOOKING;
                            // 4字节
                            switch (response.buffer.getInt()) {
                                case 0:
                                    ackstate = ServerState.LOOKING;
                                    break;
                                case 1:
                                    ackstate = ServerState.FOLLOWING;
                                    break;
                                case 2:
                                    ackstate = ServerState.LEADING;
                                    break;
                                case 3:
                                    ackstate = ServerState.OBSERVING;
                                    break;
                                default:
                                    continue;
                            }

                            // 8字节
                            n.leader = response.buffer.getLong();
                            // 8字节
                            n.zxid = response.buffer.getLong();
                            // 8字节
                            n.electionEpoch = response.buffer.getLong();
                            n.state = ackstate;
                            n.sid = response.sid;
                            // 应答消息大于28字节
                            if (!backCompatibility) {
                                // 8字节
                                n.peerEpoch = response.buffer.getLong();
                            } else {
                                // 应答消息等于28字节
                                if (LOG.isInfoEnabled()) {
                                    LOG.info("Backward compatibility mode, server id=" + n.sid);
                                }
                                n.peerEpoch = ZxidUtils.getEpochFromZxid(n.zxid);
                            }

                            /**
                             * Version added in 3.4.6
                             *
                             * 版本号
                             *
                             * 4字节
                             */
                            n.version = (response.buffer.remaining() >= 4) ?
                                    response.buffer.getInt() : 0x0;

                            /**
                             * Print notification info
                             *
                             * debug模式打印通知信息
                             */
                            if (LOG.isInfoEnabled()) {
                                printNotification(n);
                            }

                            /**
                             * If this server is looking, then send proposed leader
                             *
                             * 如果当前server是looking状态，则发送投票推荐leader
                             */
                            if (self.getPeerState() == ServerState.LOOKING) {
                                recvqueue.offer(n);

                                /**
                                 * Send a notification back if the peer that sent this
                                 * message is also looking and its logical clock is
                                 * lagging behind.
                                 *
                                 * 如果发送此消息的对等方也在寻找并且其逻辑时钟滞后，则发回通知
                                 */
                                if ((ackstate == ServerState.LOOKING)
                                        && (n.electionEpoch < logicalclock.get())) {
                                    Vote v = getVote();
                                    ToSend notmsg = new ToSend(ToSend.mType.notification,
                                            v.getId(),
                                            v.getZxid(),
                                            logicalclock.get(),
                                            self.getPeerState(),
                                            response.sid,
                                            v.getPeerEpoch());
                                    sendqueue.offer(notmsg);
                                }
                            } else {
                                /*
                                 * If this server is not looking, but the one that sent the ack
                                 * is looking, then send back what it believes to be the leader.
                                 *
                                 * 如果当前zk Server不是looking状态，请求者是looking状态，则发送让其认为当前zk Server是leader的信息
                                 */
                                Vote current = self.getCurrentVote();
                                if (ackstate == ServerState.LOOKING) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Sending new notification. My id =  " +
                                                self.getId() + " recipient=" +
                                                response.sid + " zxid=0x" +
                                                Long.toHexString(current.getZxid()) +
                                                " leader=" + current.getId());
                                    }

                                    ToSend notmsg;
                                    // 版本号大于0
                                    if (n.version > 0x0) {
                                        notmsg = new ToSend(
                                                ToSend.mType.notification,
                                                current.getId(),
                                                current.getZxid(),
                                                current.getElectionEpoch(),
                                                self.getPeerState(),
                                                response.sid,
                                                current.getPeerEpoch());

                                    } else {
                                        Vote bcVote = self.getBCVote();
                                        notmsg = new ToSend(
                                                ToSend.mType.notification,
                                                bcVote.getId(),
                                                bcVote.getZxid(),
                                                bcVote.getElectionEpoch(),
                                                self.getPeerState(),
                                                response.sid,
                                                bcVote.getPeerEpoch());
                                    }
                                    sendqueue.offer(notmsg);
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted Exception while waiting for new message" +
                                e.toString());
                    }
                }
                LOG.info("WorkerReceiver is down");
            }
        }


        /**
         * This worker simply dequeues a message to send and
         * and queues it on the manager's queue.
         * <p>
         * worker发送者线程只是将要发送的消息出列并将其排队到管理器的队列中
         */
        class WorkerSender extends ZooKeeperThread {
            volatile boolean stop;
            QuorumCnxManager manager;

            WorkerSender(QuorumCnxManager manager) {
                super("WorkerSender");
                this.stop = false;
                this.manager = manager;
            }

            public void run() {
                while (!stop) {
                    try {
                        ToSend m = sendqueue.poll(3000, TimeUnit.MILLISECONDS);
                        if (m == null) continue;

                        process(m);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                LOG.info("WorkerSender is down");
            }

            /**
             * Called by run() once there is a new message to send.
             * <p>
             * 一旦有新消息要发送，则调用run方法
             *
             * @param m message to send
             */
            void process(ToSend m) {
                ByteBuffer requestBuffer = buildMsg(m.state.ordinal(),
                        m.leader,
                        m.zxid,
                        m.electionEpoch,
                        m.peerEpoch);
                manager.toSend(m.sid, requestBuffer);
            }
        }


        WorkerSender ws;
        WorkerReceiver wr;

        /**
         * Constructor of class Messenger.
         * <p>
         * 消息类构造方法
         * 启动两个守护线程，一个是发送消息线程，一个接收消息线程
         *
         * @param manager Connection manager
         */
        Messenger(QuorumCnxManager manager) {

            this.ws = new WorkerSender(manager);

            Thread t = new Thread(this.ws,
                    "WorkerSender[myid=" + self.getId() + "]");
            t.setDaemon(true);
            t.start();

            this.wr = new WorkerReceiver(manager);

            t = new Thread(this.wr,
                    "WorkerReceiver[myid=" + self.getId() + "]");
            t.setDaemon(true);
            t.start();
        }

        /**
         * Stops instances of WorkerSender and WorkerReceiver
         */
        void halt() {
            this.ws.stop = true;
            this.wr.stop = true;
        }

    }

    QuorumPeer self;
    Messenger messenger;

    /**
     * zk逻辑时钟
     */
    AtomicLong logicalclock = new AtomicLong(); /* Election instance */

    /**
     * 推荐的leader
     */
    long proposedLeader;

    /**
     * 推荐leader的zxid
     */
    long proposedZxid;

    /**
     * 推荐leader的epoch
     */
    long proposedEpoch;


    /**
     * Returns the current vlue of the logical clock counter
     * <p>
     * 获取当前的逻辑时钟
     */
    public long getLogicalClock() {
        return logicalclock.get();
    }

    /**
     * Constructor of FastLeaderElection. It takes two parameters, one
     * is the QuorumPeer object that instantiated this object, and the other
     * is the connection manager. Such an object should be created only once
     * by each peer during an instance of the ZooKeeper service.
     * <p>
     * zk fast paxos构造方法
     * <p>
     * fast leader election构造方法，每个zk server只能创建一次该对象
     *
     * @param self    QuorumPeer that created this object
     *                QuorumPeer类负责管理仲裁协议。zk server可以有三种状态
     *                1.领导选举  - 每个zk server将选出一名领导者(提议自己作为最初的领导者)
     *                2.follower - zk server将与leader同步并复制所有事务
     *                3.Leader   - zk server将处理请求并将其转发给follower
     *                <p>
     *                大多数follower必须先记录该请求才能被接受。
     *                <p>
     *                该类设置一个数据报套接字，用来响应给当前leader的投票结果(zxid, leader_id, leader_zxid)。zxid是必须应答的参数
     * @param manager Connection manager
     *                此类使用TCP实现leader选举的连接管理器。它为每对服务器维护一个连接。棘手的部分是保证每对服务器只有一个连接正常运行并且可以通过网络进行通信。
     *                如果两个服务器尝试同时启动连接，则连接管理器使用非常简单的打破平局机制来根据双方的IP地址决定丢弃哪个连接。
     *                对于每个zk server，管理器维护要发送的消息队列。如果与任何特定对等体的连接断开，则发送方线程将该消息放回列表中。
     *                由于当前实现使用队列来维护要发送给另一个zk server的消息，因此我们将消息添加到队列的尾部，从而更改消息的顺序。
     *                虽然这不是领导者选举的问题，但在巩固同伴沟通时可能会出现问题。不过，这是有待验证的。
     */
    public FastLeaderElection(QuorumPeer self, QuorumCnxManager manager) {
        this.stop = false;
        this.manager = manager;
        starter(self, manager);
    }

    /**
     * This method is invoked by the constructor. Because it is a
     * part of the starting procedure of the object that must be on
     * any constructor of this class, it is probably best to keep as
     * a separate method. As we have a single constructor currently,
     * it is not strictly necessary to have it separate.
     * <p>
     * 构造方法调用此方法。因为它必须在此类的任何构造函数上的对象的启动过程的一部分，所以最好保持作为单独的方法。
     * 由于我们目前只有一个构造方法，因此并不一定要将它分开。
     *
     * @param self    QuorumPeer that created this object
     * @param manager Connection manager
     */
    private void starter(QuorumPeer self, QuorumCnxManager manager) {
        this.self = self;
        proposedLeader = -1;
        proposedZxid = -1;

        sendqueue = new LinkedBlockingQueue<>();
        recvqueue = new LinkedBlockingQueue<>();
        this.messenger = new Messenger(manager);
    }

    private void leaveInstance(Vote v) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("About to leave FLE instance: leader="
                    + v.getId() + ", zxid=0x" +
                    Long.toHexString(v.getZxid()) + ", my id=" + self.getId()
                    + ", my state=" + self.getPeerState());
        }
        recvqueue.clear();
    }

    public QuorumCnxManager getCnxManager() {
        return manager;
    }

    volatile boolean stop;

    public void shutdown() {
        stop = true;
        LOG.debug("Shutting down connection manager");
        manager.halt();
        LOG.debug("Shutting down messenger");
        messenger.halt();
        LOG.debug("FLE is down");
    }


    /**
     * Send notifications to all peers upon a change in our vote
     * <p>
     * 当我们的投票发生变化时向所有zk server发送通知
     */
    private void sendNotifications() {
        /**
         * self.getVotingView()
         *
         * 获取当前zk server投票结果，返回hashmap
         */
        for (QuorumServer server : self.getVotingView().values()) {
            long sid = server.id;

            ToSend notmsg = new ToSend(ToSend.mType.notification,
                    proposedLeader,
                    proposedZxid,
                    logicalclock.get(),
                    ServerState.LOOKING,
                    sid,
                    proposedEpoch);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending Notification: " + proposedLeader + " (n.leader), 0x" +
                        Long.toHexString(proposedZxid) + " (n.zxid), 0x" + Long.toHexString(logicalclock.get()) +
                        " (n.round), " + sid + " (recipient), " + self.getId() +
                        " (myid), 0x" + Long.toHexString(proposedEpoch) + " (n.peerEpoch)");
            }
            sendqueue.offer(notmsg);
        }
    }


    /**
     * 打印通知消息
     *
     * @param n
     */
    private void printNotification(Notification n) {
        LOG.info("Notification: " + n.toString()
                + self.getPeerState() + " (my state)");
    }

    /**
     * Check if a pair (server id, zxid) succeeds our
     * current vote.
     * <p>
     * 检查一对(服务器ID，zxid)是否能够成功进行当前投票。
     *
     * @param id   Server identifier
     * @param zxid Last zxid observed by the issuer of this vote
     */
    protected boolean totalOrderPredicate(long newId, long newZxid, long newEpoch, long curId, long curZxid, long curEpoch) {
        LOG.debug("id: " + newId + ", proposed id: " + curId + ", zxid: 0x" +
                Long.toHexString(newZxid) + ", proposed zxid: 0x" + Long.toHexString(curZxid));
        if (self.getQuorumVerifier().getWeight(newId) == 0) {
            return false;
        }

        /**
         * We return true if one of the following three cases hold:
         * 1- New epoch is higher
         * 2- New epoch is the same as current epoch, but new zxid is higher
         * 3- New epoch is the same as current epoch, new zxid is the same
         *  as current zxid, but server id is higher.
         *
         * 如果以下三种情况之一成立，则返回true：
         * 1-新投票周期更高
         * 2-新投票周期与当前投票周期相同，但新的zxid更高
         * 3-新投票周期与当前投票周期相同，新zxid与当前zxid相同，但服务器ID更高。
         */
        return ((newEpoch > curEpoch) ||
                ((newEpoch == curEpoch) &&
                        ((newZxid > curZxid) || ((newZxid == curZxid) && (newId > curId)))));
    }

    /**
     * Termination predicate. Given a set of votes, determines if
     * have sufficient to declare the end of the election round.
     *
     * 给定一组投票，确定是否足以宣布选举结束。
     *
     * @param votes 投票信息
     * @param vote  当前投票
     */
    protected boolean termPredicate(
            HashMap<Long, Vote> votes,
            Vote vote) {

        HashSet<Long> set = new HashSet<>();

        /**
         * First make the views consistent. Sometimes peers will have
         * different zxids for a server depending on timing.
         *
         * 首先保证观点保持一致。 有时，zk server将根据时间对服务器使用不同的zxids。
         */
        for (Map.Entry<Long, Vote> entry : votes.entrySet()) {
            if (vote.equals(entry.getValue())) {
                set.add(entry.getKey());
            }
        }

        return self.getQuorumVerifier().containsQuorum(set);
    }

    /**
     * In the case there is a leader elected, and a quorum supporting
     * this leader, we have to check if the leader has voted and acked
     * that it is leading. We need this check to avoid that peers keep
     * electing over and over a peer that has crashed and it is no
     * longer leading.
     *
     * 如果选出一个leader，并且大多数zk server支持这个leader，我们必须检查leader是否已投票并确认其领先。
     * 我们需要这个检查来避免对zk server一直在选择已经崩溃并且不再领先的zk server。
     *
     * @param votes         set of votes
     * @param leader        leader id
     * @param electionEpoch epoch id
     */
    protected boolean checkLeader(
            HashMap<Long, Vote> votes,
            long leader,
            long electionEpoch) {

        boolean predicate = true;

        /**
         * If everyone else thinks I'm the leader, I must be the leader.
         * The other two checks are just for the case in which I'm not the
         * leader. If I'm not the leader and I haven't received a message
         * from leader stating that it is leading, then predicate is false.
         *
         * 如果其他人都认为我是leader，我必须是leader。
         * 另外两个检查仅适用于我不是领导者的情况。
         * 如果我不是leader并且我没有收到leader的消息，表明它正在领先，那么就返回我不是leader
         */
        if (leader != self.getId()) {
            if (votes.get(leader) == null) predicate = false;
            else if (votes.get(leader).getState() != ServerState.LEADING) predicate = false;
        } else if (logicalclock.get() != electionEpoch) {
            predicate = false;
        }

        return predicate;
    }

    /**
     * This predicate checks that a leader has been elected. It doesn't
     * make a lot of sense without context (check lookForLeader) and it
     * has been separated for testing purposes.
     *
     * 该谓词检查领导者是否已经当选。没有上下文(检查lookForLeader)并没有很多意义，它已被分开用于测试目的。
     *
     * @param recv map of received votes
     * @param ooe  map containing out of election votes (LEADING or FOLLOWING)
     * @param n    Notification
     * @return
     */
    protected boolean ooePredicate(HashMap<Long, Vote> recv,
                                   HashMap<Long, Vote> ooe,
                                   Notification n) {

        return (termPredicate(recv, new Vote(n.version,
                n.leader,
                n.zxid,
                n.electionEpoch,
                n.peerEpoch,
                n.state))
                && checkLeader(ooe, n.leader, n.electionEpoch));

    }

    /**
     * 更新提议
     * @param leader
     * @param zxid
     * @param epoch
     */
    synchronized void updateProposal(long leader, long zxid, long epoch) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating proposal: " + leader + " (newleader), 0x"
                    + Long.toHexString(zxid) + " (newzxid), " + proposedLeader
                    + " (oldleader), 0x" + Long.toHexString(proposedZxid) + " (oldzxid)");
        }
        proposedLeader = leader;
        proposedZxid = zxid;
        proposedEpoch = epoch;
    }

    /**
     * 获取投票信息
     * @return
     */
    synchronized Vote getVote() {
        return new Vote(proposedLeader, proposedZxid, proposedEpoch);
    }

    /**
     * A learning state can be either FOLLOWING or OBSERVING.
     * This method simply decides which one depending on the
     * role of the server.
     *
     * learning状态可以是FOLLOWING或OBSERVING。这种方法只是根据zk server的角色决定哪一个
     *
     * @return ServerState
     */
    private ServerState learningState() {
        /**
         * LearnerType枚举包括PARTICIPANT和OBSERVER
         * PARTICIPANT: 投票参与者，包括leader和follower
         * OBSERVER: observer
         */
        if (self.getLearnerType() == LearnerType.PARTICIPANT) {
            LOG.debug("I'm a participant: " + self.getId());
            return ServerState.FOLLOWING;
        } else {
            LOG.debug("I'm an observer: " + self.getId());
            return ServerState.OBSERVING;
        }
    }

    /**
     * Returns the initial vote value of server identifier.
     *
     * 返回zk server标识符的初始投票值
     *
     * @return long
     */
    private long getInitId() {
        // 如果是投票参与者，则返回自身ID，否则返回long的最小值，即0
        if (self.getLearnerType() == LearnerType.PARTICIPANT)
            return self.getId();
        else return Long.MIN_VALUE;
    }

    /**
     * Returns initial last logged zxid.
     *
     * 返回上次记录的最初zxid
     *
     * @return long
     */
    private long getInitLastLoggedZxid() {
        // 如果是投票参与者，则返回zxid，否则返回long的最小值，即0
        if (self.getLearnerType() == LearnerType.PARTICIPANT)
            return self.getLastLoggedZxid();
        else return Long.MIN_VALUE;
    }

    /**
     * Returns the initial vote value of the peer epoch.
     *
     * 返回zk server投票周期的初始投票值
     *
     * @return long
     */
    private long getPeerEpoch() {
        if (self.getLearnerType() == LearnerType.PARTICIPANT)
            try {
                return self.getCurrentEpoch();
            } catch (IOException e) {
                RuntimeException re = new RuntimeException(e.getMessage());
                re.setStackTrace(e.getStackTrace());
                throw re;
            }
        else return Long.MIN_VALUE;
    }

    /**
     * Starts a new round of leader election. Whenever our QuorumPeer
     * changes its state to LOOKING, this method is invoked, and it
     * sends notifications to all other peers.
     *
     * 开始新一轮leader选举。每当我们的QuorumPeer将其状态更改为LOOKING时，都会调用此方法，并向所有其他zk server发送通知。
     */
    public Vote lookForLeader() throws InterruptedException {
        try {
            /**
             * MBeanRegistry类提供了一个统一的接口，用于使用平台MBean服务器注册/取消注册zookeeper MBean。
             * 它构建了MBean的层次结构，其中每个MBean由类似文件系统的路径表示。
             * 最终，此层次结构将作为虚拟数据树存储在zookeeper数据树实例中
             */
            // 创建LeaderElectionBean，并注册bean到MBeanRegistry
            self.jmxLeaderElectionBean = new LeaderElectionBean();
            MBeanRegistry.getInstance().register(
                    self.jmxLeaderElectionBean, self.jmxLocalPeerBean);
        } catch (Exception e) {
            LOG.warn("Failed to register with JMX", e);
            self.jmxLeaderElectionBean = null;
        }

        // leader选举开始时间
        if (self.start_fle == 0) {
            self.start_fle = Time.currentElapsedTime();
        }

        try {
            HashMap<Long, Vote> recvset = new HashMap<>();

            HashMap<Long, Vote> outofelection = new HashMap<>();

            int notTimeout = finalizeWait;

            // 修改逻辑时钟并更新提议
            synchronized (this) {
                logicalclock.incrementAndGet();
                updateProposal(getInitId(), getInitLastLoggedZxid(), getPeerEpoch());
            }

            LOG.info("New election. My id =  " + self.getId() + ", proposed zxid=0x" + Long.toHexString(proposedZxid));

            // 发送通知
            sendNotifications();

            /*
             * Loop in which we exchange notifications until we find a leader
             */
            while ((self.getPeerState() == ServerState.LOOKING) &&
                    (!stop)) {
                /*
                 * Remove next notification from queue, times out after 2 times
                 * the termination time
                 */
                Notification n = recvqueue.poll(notTimeout,
                        TimeUnit.MILLISECONDS);

                /*
                 * Sends more notifications if haven't received enough.
                 * Otherwise processes new notification.
                 */
                if (n == null) {
                    if (manager.haveDelivered()) {
                        sendNotifications();
                    } else {
                        manager.connectAll();
                    }

                    /*
                     * Exponential backoff
                     */
                    int tmpTimeOut = notTimeout * 2;
                    notTimeout = (tmpTimeOut < maxNotificationInterval ?
                            tmpTimeOut : maxNotificationInterval);
                    LOG.info("Notification time out: " + notTimeout);
                } else if (validVoter(n.sid) && validVoter(n.leader)) {
                    /*
                     * Only proceed if the vote comes from a replica in the
                     * voting view for a replica in the voting view.
                     */
                    switch (n.state) {
                        case LOOKING:
                            // If notification > current, replace and send messages out
                            if (n.electionEpoch > logicalclock.get()) {
                                logicalclock.set(n.electionEpoch);
                                recvset.clear();
                                if (totalOrderPredicate(n.leader, n.zxid, n.peerEpoch,
                                        getInitId(), getInitLastLoggedZxid(), getPeerEpoch())) {
                                    updateProposal(n.leader, n.zxid, n.peerEpoch);
                                } else {
                                    updateProposal(getInitId(),
                                            getInitLastLoggedZxid(),
                                            getPeerEpoch());
                                }
                                sendNotifications();
                            } else if (n.electionEpoch < logicalclock.get()) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Notification election epoch is smaller than logicalclock. n.electionEpoch = 0x"
                                            + Long.toHexString(n.electionEpoch)
                                            + ", logicalclock=0x" + Long.toHexString(logicalclock.get()));
                                }
                                break;
                            } else if (totalOrderPredicate(n.leader, n.zxid, n.peerEpoch,
                                    proposedLeader, proposedZxid, proposedEpoch)) {
                                updateProposal(n.leader, n.zxid, n.peerEpoch);
                                sendNotifications();
                            }

                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Adding vote: from=" + n.sid +
                                        ", proposed leader=" + n.leader +
                                        ", proposed zxid=0x" + Long.toHexString(n.zxid) +
                                        ", proposed election epoch=0x" + Long.toHexString(n.electionEpoch));
                            }

                            recvset.put(n.sid, new Vote(n.leader, n.zxid, n.electionEpoch, n.peerEpoch));

                            if (termPredicate(recvset,
                                    new Vote(proposedLeader, proposedZxid,
                                            logicalclock.get(), proposedEpoch))) {

                                // Verify if there is any change in the proposed leader
                                while ((n = recvqueue.poll(finalizeWait,
                                        TimeUnit.MILLISECONDS)) != null) {
                                    if (totalOrderPredicate(n.leader, n.zxid, n.peerEpoch,
                                            proposedLeader, proposedZxid, proposedEpoch)) {
                                        recvqueue.put(n);
                                        break;
                                    }
                                }

                                /*
                                 * This predicate is true once we don't read any new
                                 * relevant message from the reception queue
                                 */
                                if (n == null) {
                                    self.setPeerState((proposedLeader == self.getId()) ?
                                            ServerState.LEADING : learningState());

                                    Vote endVote = new Vote(proposedLeader,
                                            proposedZxid,
                                            logicalclock.get(),
                                            proposedEpoch);
                                    leaveInstance(endVote);
                                    return endVote;
                                }
                            }
                            break;
                        case OBSERVING:
                            LOG.debug("Notification from observer: " + n.sid);
                            break;
                        case FOLLOWING:
                        case LEADING:
                            /*
                             * Consider all notifications from the same epoch
                             * together.
                             */
                            if (n.electionEpoch == logicalclock.get()) {
                                recvset.put(n.sid, new Vote(n.leader,
                                        n.zxid,
                                        n.electionEpoch,
                                        n.peerEpoch));

                                if (ooePredicate(recvset, outofelection, n)) {
                                    self.setPeerState((n.leader == self.getId()) ?
                                            ServerState.LEADING : learningState());

                                    Vote endVote = new Vote(n.leader,
                                            n.zxid,
                                            n.electionEpoch,
                                            n.peerEpoch);
                                    leaveInstance(endVote);
                                    return endVote;
                                }
                            }

                            /*
                             * Before joining an established ensemble, verify
                             * a majority is following the same leader.
                             */
                            outofelection.put(n.sid, new Vote(n.version,
                                    n.leader,
                                    n.zxid,
                                    n.electionEpoch,
                                    n.peerEpoch,
                                    n.state));

                            if (ooePredicate(outofelection, outofelection, n)) {
                                synchronized (this) {
                                    logicalclock.set(n.electionEpoch);
                                    self.setPeerState((n.leader == self.getId()) ?
                                            ServerState.LEADING : learningState());
                                }
                                Vote endVote = new Vote(n.leader,
                                        n.zxid,
                                        n.electionEpoch,
                                        n.peerEpoch);
                                leaveInstance(endVote);
                                return endVote;
                            }
                            break;
                        default:
                            LOG.warn("Notification state unrecognized: {} (n.state), {} (n.sid)",
                                    n.state, n.sid);
                            break;
                    }
                } else {
                    if (!validVoter(n.leader)) {
                        LOG.warn("Ignoring notification for non-cluster member sid {} from sid {}", n.leader, n.sid);
                    }
                    if (!validVoter(n.sid)) {
                        LOG.warn("Ignoring notification for sid {} from non-quorum member sid {}", n.leader, n.sid);
                    }
                }
            }
            return null;
        } finally {
            // 选举完成后，销毁jmxLeaderElectionBean
            try {
                if (self.jmxLeaderElectionBean != null) {
                    MBeanRegistry.getInstance().unregister(
                            self.jmxLeaderElectionBean);
                }
            } catch (Exception e) {
                LOG.warn("Failed to unregister with JMX", e);
            }
            self.jmxLeaderElectionBean = null;
            LOG.debug("Number of connection processing threads: {}",
                    manager.getConnectionThreadCount());
        }
    }

    /**
     * Check if a given sid is represented in either the current or
     * the next voting view
     * <p>
     * 检查给定的sid是否在当前或下一个投票周期中
     *
     * @param sid Server identifier
     * @return boolean
     */
    private boolean validVoter(long sid) {
        return self.getVotingView().containsKey(sid);
    }
}
