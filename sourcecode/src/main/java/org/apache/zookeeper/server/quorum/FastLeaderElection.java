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
         * zk选举时的事务id
         * zk选举时，会比较此值，同票时，zxid较大者胜出
         */
        long zxid;

        /**
         * Epoch
         * <p>
         * 当前zk选举周期，判断是否是同一次选举，选举一次就会自增1
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
         * 发送者的server id
         */
        long sid;

        /**
         * epoch of the proposed leader
         * 推荐leader epochId
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
        // 5个参数合计40字节
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
     * zk Server想要发送给其他zk Server的消息体。这些消息可以是通知和Acks接收通知。
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
         * <p>
         * 通知的提议leader
         */
        long leader;

        /**
         * id contains the tag for acks, and zxid for notifications
         * <p>
         * id包含acks的标记，zxid包含通知
         * <p>
         * zk选举的事务ID
         */
        long zxid;

        /**
         * Epoch
         * <p>
         * epochId
         */
        long electionEpoch;

        /**
         * Current state;
         * <p>
         * 当前zk server状态
         */
        ServerState state;

        /**
         * Address of recipient
         * <p>
         * 接收zk server sid
         */
        long sid;

        /**
         * Leader epoch
         * <p>
         * leader epochId
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
     * 消息处理器的多线程实现。消息的两个子类实现：{@link Messenger.WorkReceiver}和{@link Messenger.WorkSender}
     * 从名称中就可以看出每个子类的功能。其中每一个子类都会产生一个新线程。
     */
    protected class Messenger {

        /**
         * Receives messages from instance of QuorumCnxManager on
         * method run(), and processes such messages.
         * <p>
         * 从{@link QuorumCnxManager}实例接收消息方法run()，并处理此类消息。
         * <p>
         * {@link ZooKeeperThread} zk线程，简单重写Thread的构造方法
         */
        class WorkerReceiver extends ZooKeeperThread {
            // 线程是否停止标识
            volatile boolean stop;

            // zk server leader选举管理器，每对zk server只保留一个TCP连接
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
                        // 3秒超时，死循环拉取数据
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
                         * 如果请求来自observer，则立即回复。(observer不参与投票，故直接响应)
                         * 如果服务器不是follower，那么它必是一个observer
                         * 如果我们有任何其他类型的未来的learner，我们将不得不改变我们检查observer的方式。
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

                            /**
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
                                /**
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
         * worker发送者线程只是将要发送的消息出列，然后将添加到管理器的队列中
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

                /**
                 * 通过{@link QuorumCnxManager}发送数据
                 */
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
     * <p>
     * 代表zk选举轮次，
     * 如果当前logicalclock比接收到其他zk server的logicalclock小，表示当前logicalclock失效(例如网络原因未收到投票信息等)，
     * 即当前选举信息全部作废，需要同步最新logicalclock
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
     * @param manager Connection manager
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

    /**
     * 停止选举
     */
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
        // 权重为0，则不参与投票
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
         * 以下三种情况，返回true：
         * 1-epoch比当前epoch大
         * 2-epoch与当前epoch相同，zxid比当前zxid大
         * 3-epoch与当前epoch相同，zxid与当前zxid相同，但serverId比当前serverId大
         */
        return ((newEpoch > curEpoch) ||
                ((newEpoch == curEpoch) &&
                        ((newZxid > curZxid) || ((newZxid == curZxid) && (newId > curId)))));
    }

    /**
     * Termination predicate. Given a set of votes, determines if
     * have sufficient to declare the end of the election round.
     * <p>
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
         *
         * 统计当前vote信息，将投该zk server的id放入set集合
         */
        for (Map.Entry<Long, Vote> entry : votes.entrySet()) {
            if (vote.equals(entry.getValue())) {
                set.add(entry.getKey());
            }
        }

        // 统计投票信息是否过半
        return self.getQuorumVerifier().containsQuorum(set);
    }

    /**
     * In the case there is a leader elected, and a quorum supporting
     * this leader, we have to check if the leader has voted and acked
     * that it is leading. We need this check to avoid that peers keep
     * electing over and over a peer that has crashed and it is no
     * longer leading.
     * <p>
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
         * 如果其他人都认为当前zk server是leader，则当前zk server就是leader
         * 另外两个检查仅适用于当前zk server不是leader的情况
         * 如果当前zk server不是leader并且我没有收到leader的消息，表明它正在领先，那么就返回我不是leader
         */
        if (leader != self.getId()) {
            if (votes.get(leader) == null) {
                // 投票信息中不存在leader信息
                predicate = false;
            } else if (votes.get(leader).getState() != ServerState.LEADING) {
                // zk server非leader状态
                predicate = false;
            }
        } else if (logicalclock.get() != electionEpoch) {
            // 逻辑时钟不一致
            predicate = false;
        }

        return predicate;
    }

    /**
     * This predicate checks that a leader has been elected. It doesn't
     * make a lot of sense without context (check lookForLeader) and it
     * has been separated for testing purposes.
     * <p>
     * 检查leader是否已经当选。没有上下文(检查lookForLeader)并没有很多意义，它已被分开用于测试目的。
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
     * 更新投票
     *
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
     *
     * @return
     */
    synchronized Vote getVote() {
        return new Vote(proposedLeader, proposedZxid, proposedEpoch);
    }

    /**
     * A learning state can be either FOLLOWING or OBSERVING.
     * This method simply decides which one depending on the
     * role of the server.
     * <p>
     * learning状态可以是FOLLOWING或OBSERVING。这种方法只是根据zk server的角色决定哪一个
     *
     * @return ServerState
     */
    private ServerState learningState() {
        /**
         * LearnerType枚举包括PARTICIPANT和OBSERVER
         * PARTICIPANT: 选举参与者，包括leader和follower
         * OBSERVER: observer server主要用于扩展和客户端连接
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
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
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

            // 初始值为200ms
            int notTimeout = finalizeWait;

            // 修改逻辑时钟并更新投票
            synchronized (this) {
                logicalclock.incrementAndGet();
                updateProposal(getInitId(), getInitLastLoggedZxid(), getPeerEpoch());
            }

            LOG.info("New election. My id =  " + self.getId() + ", proposed zxid=0x" + Long.toHexString(proposedZxid));

            // 发送通知
            sendNotifications();

            /**
             * Loop in which we exchange notifications until we find a leader
             *
             * 直到找到leader前，zk server之间循环交换通知
             */
            while ((self.getPeerState() == ServerState.LOOKING) &&
                    (!stop)) {
                /**
                 * Remove next notification from queue, times out after 2 times
                 * the termination time
                 *
                 * 从队列中删除下一个通知，在终止时间的2倍后超时
                 */
                Notification n = recvqueue.poll(notTimeout,
                        TimeUnit.MILLISECONDS);

                /**
                 * Sends more notifications if haven't received enough.
                 * Otherwise processes new notification.
                 *
                 * 如果接收不到足够的通知(即收到信息，无法选举出leader)，则发送更多通知。否则处理新通知
                 */
                if (n == null) {
                    if (manager.haveDelivered()) {
                        sendNotifications();
                    } else {
                        manager.connectAll();
                    }

                    /**
                     * Exponential backoff
                     *
                     * 指数退避
                     * 调整拉取数据超时时间
                     */
                    int tmpTimeOut = notTimeout * 2;
                    notTimeout = (tmpTimeOut < maxNotificationInterval ?
                            tmpTimeOut : maxNotificationInterval);
                    LOG.info("Notification time out: " + notTimeout);
                } else if (validVoter(n.sid) && validVoter(n.leader)) {
                    /**
                     * Only proceed if the vote comes from a replica in the
                     * voting view for a replica in the voting view.
                     */
                    switch (n.state) {
                        case LOOKING:
                            // If notification > current, replace and send messages out
                            // 当选举epoch比逻辑时钟值大时，说明逻辑时钟已失效，需要重置逻辑时钟，更新投票，群发自己的投票信息
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
                                // 投票周期比当前logicalclock小，不需要处理，因为请求的投票周期没有任何意义
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Notification election epoch is smaller than logicalclock. n.electionEpoch = 0x"
                                            + Long.toHexString(n.electionEpoch)
                                            + ", logicalclock=0x" + Long.toHexString(logicalclock.get()));
                                }
                                break;
                            } else if (totalOrderPredicate(n.leader, n.zxid, n.peerEpoch,
                                    proposedLeader, proposedZxid, proposedEpoch)) {
                                // 选举周期相同
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

                            // 判断是否终止投票
                            if (termPredicate(recvset,
                                    new Vote(proposedLeader, proposedZxid,
                                            logicalclock.get(), proposedEpoch))) {

                                // Verify if there is any change in the proposed leader
                                // 验证提议的leader是否有任何变化
                                while ((n = recvqueue.poll(finalizeWait,
                                        TimeUnit.MILLISECONDS)) != null) {
                                    if (totalOrderPredicate(n.leader, n.zxid, n.peerEpoch,
                                            proposedLeader, proposedZxid, proposedEpoch)) {
                                        recvqueue.put(n);
                                        break;
                                    }
                                }

                                /**
                                 * This predicate is true once we don't read any new
                                 * relevant message from the reception queue
                                 *
                                 * 一旦我们没有从接收队列中读取任何新的相关消息，该谓词就成立了
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
                            // observer没有投票权利，无需处理
                            LOG.debug("Notification from observer: " + n.sid);
                            break;
                        case FOLLOWING:
                        case LEADING:
                            /**
                             * Consider all notifications from the same epoch
                             * together.
                             *
                             * 同时考虑来自同一投票周期的所有通知
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

                            /**
                             * Before joining an established ensemble, verify
                             * a majority is following the same leader.
                             *
                             * 在加入已建立的团体之前，确认大多数人都在跟随同一个leader
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
