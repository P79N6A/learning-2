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

import org.apache.zookeeper.server.ZooKeeperThread;
import org.apache.zookeeper.server.quorum.auth.QuorumAuthLearner;
import org.apache.zookeeper.server.quorum.auth.QuorumAuthServer;
import org.apache.zookeeper.server.quorum.auth.SaslQuorumAuthLearner;
import org.apache.zookeeper.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class implements a connection manager for leader election using TCP. It
 * maintains one connection for every pair of servers. The tricky part is to
 * guarantee that there is exactly one connection for every pair of servers that
 * are operating correctly and that can communicate over the network.
 * <p>
 * If two servers try to start a connection concurrently, then the connection
 * manager uses a very simple tie-breaking mechanism to decide which connection
 * to drop based on the IP addressed of the two parties.
 * <p>
 * For every peer, the manager maintains a queue of messages to send. If the
 * connection to any particular peer drops, then the sender thread puts the
 * message back on the list. As this implementation currently uses a queue
 * implementation to maintain messages to send to another peer, we add the
 * message to the tail of the queue, thus changing the order of messages.
 * Although this is not a problem for the leader election, it could be a problem
 * when consolidating peer communication. This is to be verified, though.
 * <p>
 * 此类使用TCP实现leader选举的连接管理器。它为每对服务器维护一个连接。棘手的部分是保证每对服务器只有一个连接正常运行并且可以通过网络进行通信。
 * 如果两个服务器尝试同时启动连接，则连接管理器使用非常简单的打破平局机制来根据双方的IP地址决定丢弃哪个连接。
 * 对于每个zk server，管理器维护要发送的消息队列。如果与任何特定对等体的连接断开，则发送方线程将该消息放回列表中。
 * 由于当前实现使用队列来维护要发送给另一个zk server的消息，因此我们将消息添加到队列的尾部，从而更改消息的顺序。
 * 虽然这不是领导者选举的问题，但在巩固同伴沟通时可能会出现问题。不过，这是有待验证的。
 * <p>
 * zk版本3.4.13
 */
public class QuorumCnxManager {
    private static final Logger LOG = LoggerFactory.getLogger(QuorumCnxManager.class);

    /**
     * Maximum capacity of thread queues
     * <p>
     * 线程队列最大容量
     */
    static final int RECV_CAPACITY = 100;

    // Initialized to 1 to prevent sending
    // stale notifications to peers
    static final int SEND_CAPACITY = 1;

    static final int PACKETMAXSIZE = 1024 * 512;

    /*
     * Max buffer size to be read from the network.
     */
    static public final int maxBuffer = 2048;

    /*
     * Negative counter for observer server ids.
     */

    private AtomicLong observerCounter = new AtomicLong(-1);

    /*
     * Connection time out value in milliseconds
     */

    private int cnxTO = 5000;

    /*
     * Local IP address
     */
    final long mySid;
    final int socketTimeout;
    final Map<Long, QuorumPeer.QuorumServer> view;
    final boolean tcpKeepAlive = Boolean.getBoolean("zookeeper.tcpKeepAlive");
    final boolean listenOnAllIPs;
    private ThreadPoolExecutor connectionExecutor;
    private final Set<Long> inprogressConnections = Collections
            .synchronizedSet(new HashSet<Long>());
    private QuorumAuthServer authServer;
    private QuorumAuthLearner authLearner;
    private boolean quorumSaslAuthEnabled;
    /*
     * Counter to count connection processing threads.
     */
    private AtomicInteger connectionThreadCnt = new AtomicInteger(0);

    /*
     * Mapping from Peer to Thread number
     */
    final ConcurrentHashMap<Long, SendWorker> senderWorkerMap;
    final ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>> queueSendMap;
    final ConcurrentHashMap<Long, ByteBuffer> lastMessageSent;

    /*
     * Reception queue
     */
    public final ArrayBlockingQueue<Message> recvQueue;
    /*
     * Object to synchronize access to recvQueue
     */
    private final Object recvQLock = new Object();

    /*
     * Shutdown flag
     */

    volatile boolean shutdown = false;

    /*
     * Listener thread
     */
    public final Listener listener;

    /*
     * Counter to count worker threads
     */
    private AtomicInteger threadCnt = new AtomicInteger(0);

    static public class Message {

        Message(ByteBuffer buffer, long sid) {
            this.buffer = buffer;
            this.sid = sid;
        }

        ByteBuffer buffer;
        long sid;
    }

    public QuorumCnxManager(final long mySid,
                            Map<Long, QuorumPeer.QuorumServer> view,
                            QuorumAuthServer authServer,
                            QuorumAuthLearner authLearner,
                            int socketTimeout,
                            boolean listenOnAllIPs,
                            int quorumCnxnThreadsSize,
                            boolean quorumSaslAuthEnabled) {
        this(mySid, view, authServer, authLearner, socketTimeout, listenOnAllIPs,
                quorumCnxnThreadsSize, quorumSaslAuthEnabled, new ConcurrentHashMap<Long, SendWorker>());
    }

    // visible for testing
    public QuorumCnxManager(final long mySid,
                            Map<Long, QuorumPeer.QuorumServer> view,
                            QuorumAuthServer authServer,
                            QuorumAuthLearner authLearner,
                            int socketTimeout,
                            boolean listenOnAllIPs,
                            int quorumCnxnThreadsSize,
                            boolean quorumSaslAuthEnabled,
                            ConcurrentHashMap<Long, SendWorker> senderWorkerMap) {
        this.senderWorkerMap = senderWorkerMap;

        this.recvQueue = new ArrayBlockingQueue<Message>(RECV_CAPACITY);
        this.queueSendMap = new ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>>();
        this.lastMessageSent = new ConcurrentHashMap<Long, ByteBuffer>();
        String cnxToValue = System.getProperty("zookeeper.cnxTimeout");
        if (cnxToValue != null) {
            this.cnxTO = Integer.parseInt(cnxToValue);
        }

        this.mySid = mySid;
        this.socketTimeout = socketTimeout;
        this.view = view;
        this.listenOnAllIPs = listenOnAllIPs;

        initializeAuth(mySid, authServer, authLearner, quorumCnxnThreadsSize,
                quorumSaslAuthEnabled);

        // Starts listener thread that waits for connection requests
        listener = new Listener();
    }

    private void initializeAuth(final long mySid,
                                final QuorumAuthServer authServer,
                                final QuorumAuthLearner authLearner,
                                final int quorumCnxnThreadsSize,
                                final boolean quorumSaslAuthEnabled) {
        this.authServer = authServer;
        this.authLearner = authLearner;
        this.quorumSaslAuthEnabled = quorumSaslAuthEnabled;
        if (!this.quorumSaslAuthEnabled) {
            LOG.debug("Not initializing connection executor as quorum sasl auth is disabled");
            return;
        }

        // init connection executors
        final AtomicInteger threadIndex = new AtomicInteger(1);
        SecurityManager s = System.getSecurityManager();
        final ThreadGroup group = (s != null) ? s.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
        ThreadFactory daemonThFactory = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r, "QuorumConnectionThread-"
                        + "[myid=" + mySid + "]-"
                        + threadIndex.getAndIncrement());
                return t;
            }
        };
        this.connectionExecutor = new ThreadPoolExecutor(3,
                quorumCnxnThreadsSize, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), daemonThFactory);
        this.connectionExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * Invokes initiateConnection for testing purposes
     *
     * @param sid
     */
    public void testInitiateConnection(long sid) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Opening channel to server " + sid);
        }
        Socket sock = new Socket();
        setSockOpts(sock);
        sock.connect(QuorumPeer.viewToVotingView(view).get(sid).electionAddr,
                cnxTO);
        initiateConnection(sock, sid);
    }

    /**
     * If this server has initiated the connection, then it gives up on the
     * connection if it loses challenge. Otherwise, it keeps the connection.
     * <p>
     * 如果此服务器已启动连接，则它会在连接丢失时放弃连接。否则，它保持连接
     */
    public void initiateConnection(final Socket sock, final Long sid) {
        try {
            startConnection(sock, sid);
        } catch (IOException e) {
            LOG.error("Exception while connecting, id: {}, addr: {}, closing learner connection",
                    new Object[]{sid, sock.getRemoteSocketAddress()}, e);
            closeSocket(sock);
            return;
        }
    }

    /**
     * Server will initiate the connection request to its peer server
     * asynchronously via separate connection thread.
     * <p>
     * Server将通过单独的连接线程异步地向其他zk server发起连接请求
     */
    public void initiateConnectionAsync(final Socket sock, final Long sid) {
        // 如果连接已经建立，则关闭当前socket连接请求
        if (!inprogressConnections.add(sid)) {
            // simply return as there is a connection request to
            // server 'sid' already in progress.
            LOG.debug("Connection request to server id: {} is already in progress, so skipping this request",
                    sid);
            closeSocket(sock);
            return;
        }

        try {
            // 加入连接池中，进行单线程通信
            connectionExecutor.execute(
                    new QuorumConnectionReqThread(sock, sid));
            // 连接管理器的计数器参数加1
            connectionThreadCnt.incrementAndGet();
        } catch (Throwable e) {
            // Imp: Safer side catching all type of exceptions and remove 'sid'
            // from inprogress connections. This is to avoid blocking further
            // connection requests from this 'sid' in case of errors.
            inprogressConnections.remove(sid);
            LOG.error("Exception while submitting quorum connection request", e);
            closeSocket(sock);
        }
    }

    /**
     * Thread to send connection request to peer server.
     * <p>
     * 启动线程向zk server发送连接请求
     */
    private class QuorumConnectionReqThread extends ZooKeeperThread {
        final Socket sock;
        final Long sid;

        QuorumConnectionReqThread(final Socket sock, final Long sid) {
            super("QuorumConnectionReqThread-" + sid);
            this.sock = sock;
            this.sid = sid;
        }

        @Override
        public void run() {
            try {
                initiateConnection(sock, sid);
            } finally {
                inprogressConnections.remove(sid);
            }
        }
    }

    /**
     * 创建连接
     *
     * @param sock
     * @param sid
     * @return
     * @throws IOException
     */
    private boolean startConnection(Socket sock, Long sid)
            throws IOException {
        DataOutputStream dout = null;
        DataInputStream din = null;
        try {
            // Sending id and challenge
            // 发送id并且鉴权
            dout = new DataOutputStream(sock.getOutputStream());
            dout.writeLong(this.mySid);
            dout.flush();

            din = new DataInputStream(
                    new BufferedInputStream(sock.getInputStream()));
        } catch (IOException e) {
            LOG.warn("Ignoring exception reading or writing challenge: ", e);
            closeSocket(sock);
            return false;
        }

        /**
         * authenticate learner
         *
         * 对socket连接执行sasl身份验证
         *
         * @see SaslQuorumAuthLearner#authenticate
         * @see SecurityUtils#createSaslClient
         */
        authLearner.authenticate(sock, view.get(sid).hostname);

        // If lost the challenge, then drop the new connection
        // 如果丢失了挑战，则删除新连接
        // sid小的zk server向sid大的zk server发起连接，否则关闭连接，间接满足每对zk server只保持一个TCP连接的规则
        if (sid > this.mySid) {
            LOG.info("Have smaller server identifier, so dropping the " +
                    "connection: (" + sid + ", " + this.mySid + ")");
            closeSocket(sock);
            // Otherwise proceed with the connection
        } else {
            // socket连接成功
            SendWorker sw = new SendWorker(sock, sid);
            RecvWorker rw = new RecvWorker(sock, din, sid, sw);
            sw.setRecv(rw);

            SendWorker vsw = senderWorkerMap.get(sid);

            if (vsw != null)
                vsw.finish();

            // SenderWorker map，存储SenderWorker线程
            senderWorkerMap.put(sid, sw);
            // send data queue
            queueSendMap.putIfAbsent(sid, new ArrayBlockingQueue<>(SEND_CAPACITY));

            // 启动SendWorker和RecvWorker线程
            sw.start();
            rw.start();

            return true;

        }
        return false;
    }

    /**
     * If this server receives a connection request, then it gives up on the new
     * connection if it wins. Notice that it checks whether it has a connection
     * to this server already or not. If it does, then it sends the smallest
     * possible long value to lose the challenge.
     */
    public void receiveConnection(final Socket sock) {
        DataInputStream din = null;
        try {
            din = new DataInputStream(
                    new BufferedInputStream(sock.getInputStream()));

            handleConnection(sock, din);
        } catch (IOException e) {
            LOG.error("Exception handling connection, addr: {}, closing server connection",
                    sock.getRemoteSocketAddress());
            closeSocket(sock);
        }
    }

    /**
     * Server receives a connection request and handles it asynchronously via
     * separate thread.
     */
    public void receiveConnectionAsync(final Socket sock) {
        try {
            connectionExecutor.execute(
                    new QuorumConnectionReceiverThread(sock));
            connectionThreadCnt.incrementAndGet();
        } catch (Throwable e) {
            LOG.error("Exception handling connection, addr: {}, closing server connection",
                    sock.getRemoteSocketAddress());
            closeSocket(sock);
        }
    }

    /**
     * Thread to receive connection request from peer server.
     */
    private class QuorumConnectionReceiverThread extends ZooKeeperThread {
        private final Socket sock;

        QuorumConnectionReceiverThread(final Socket sock) {
            super("QuorumConnectionReceiverThread-" + sock.getRemoteSocketAddress());
            this.sock = sock;
        }

        @Override
        public void run() {
            receiveConnection(sock);
        }
    }

    private void handleConnection(Socket sock, DataInputStream din)
            throws IOException {
        Long sid = null;
        try {
            // Read server id
            sid = din.readLong();
            if (sid < 0) { // this is not a server id but a protocol version (see ZOOKEEPER-1633)
                sid = din.readLong();

                // next comes the #bytes in the remainder of the message
                // note that 0 bytes is fine (old servers)
                int num_remaining_bytes = din.readInt();
                if (num_remaining_bytes < 0 || num_remaining_bytes > maxBuffer) {
                    LOG.error("Unreasonable buffer length: {}", num_remaining_bytes);
                    closeSocket(sock);
                    return;
                }
                byte[] b = new byte[num_remaining_bytes];

                // remove the remainder of the message from din
                int num_read = din.read(b);
                if (num_read != num_remaining_bytes) {
                    LOG.error("Read only " + num_read + " bytes out of " + num_remaining_bytes + " sent by server " + sid);
                }
            }
            if (sid == QuorumPeer.OBSERVER_ID) {
                /*
                 * Choose identifier at random. We need a value to identify
                 * the connection.
                 */
                sid = observerCounter.getAndDecrement();
                LOG.info("Setting arbitrary identifier to observer: " + sid);
            }
        } catch (IOException e) {
            closeSocket(sock);
            LOG.warn("Exception reading or writing challenge: " + e.toString());
            return;
        }

        // do authenticating learner
        LOG.debug("Authenticating learner server.id: {}", sid);
        authServer.authenticate(sock, din);

        //If wins the challenge, then close the new connection.
        if (sid < this.mySid) {
            /*
             * This replica might still believe that the connection to sid is
             * up, so we have to shut down the workers before trying to open a
             * new connection.
             */
            SendWorker sw = senderWorkerMap.get(sid);
            if (sw != null) {
                sw.finish();
            }

            /*
             * Now we start a new connection
             */
            LOG.debug("Create new connection to server: " + sid);
            closeSocket(sock);
            connectOne(sid);

            // Otherwise start worker threads to receive data.
        } else {
            SendWorker sw = new SendWorker(sock, sid);
            RecvWorker rw = new RecvWorker(sock, din, sid, sw);
            sw.setRecv(rw);

            SendWorker vsw = senderWorkerMap.get(sid);

            if (vsw != null)
                vsw.finish();

            senderWorkerMap.put(sid, sw);
            queueSendMap.putIfAbsent(sid, new ArrayBlockingQueue<ByteBuffer>(SEND_CAPACITY));

            sw.start();
            rw.start();

            return;
        }
    }

    /**
     * Processes invoke this message to queue a message to send. Currently,
     * only leader election uses it.
     * <p>
     * 进程调用此消息以对要发送的消息进行排队。目前，只有leader选举使用它。
     */
    public void toSend(Long sid, ByteBuffer b) {
        /**
         * If sending message to myself, then simply enqueue it (loopback).
         *
         * 如果向自己发送消息，那么只需将其保存到接收队列，即自己给自己投票
         */
        if (this.mySid == sid) {
            b.position(0);
            addToRecvQueue(new Message(b.duplicate(), sid));
            /**
             * Otherwise send to the corresponding thread to send.
             *
             * 否则发送到相应的线程发送
             */
        } else {
            /**
             * Start a new connection if doesn't have one already.
             *
             * 如果与sid连接不存在，则创建一个连接
             */
            ArrayBlockingQueue<ByteBuffer> bq = new ArrayBlockingQueue<>(SEND_CAPACITY);
            /**
             * @see ConcurrentHashMap#putIfAbsent
             * (1)如果sid存在，则返回与sid对应的ArrayBlockingQueue
             * (2)如果sid不存在，则返回null
             */
            ArrayBlockingQueue<ByteBuffer> bqExisting = queueSendMap.putIfAbsent(sid, bq);
            if (bqExisting != null) {
                addToSendQueue(bqExisting, b);
            } else {
                addToSendQueue(bq, b);
            }
            connectOne(sid);
        }
    }

    /**
     * Try to establish a connection to server with id sid.
     * <p>
     * 尝试与给定的sid zk server进行连接
     *
     * @param sid server id
     */
    synchronized public void connectOne(long sid) {
        // 未建立连接则进行创建连接
        if (!connectedToPeer(sid)) {
            InetSocketAddress electionAddr;
            // 从hashmap中获取待连接的zk serverip地址
            if (view.containsKey(sid)) {
                electionAddr = view.get(sid).electionAddr;
            } else {
                LOG.warn("Invalid server id: " + sid);
                return;
            }

            try {
                // 创建socket连接
                LOG.debug("Opening channel to server " + sid);
                Socket sock = new Socket();
                setSockOpts(sock);
                sock.connect(view.get(sid).electionAddr, cnxTO);
                LOG.debug("Connected to server " + sid);

                // Sends connection request asynchronously if the quorum
                // sasl authentication is enabled. This is required because
                // sasl server authentication process may take few seconds to
                // finish, this may delay next peer connection requests.

                // 如果启用了仲裁sasl身份验证，则异步发送连接请求。
                // 这是必需的，因为sasl服务器身份验证过程可能需要几秒钟才能完成，这可能会延迟下一个zk server连接请求。
                if (quorumSaslAuthEnabled) {
                    initiateConnectionAsync(sock, sid);
                } else {
                    initiateConnection(sock, sid);
                }
            } catch (UnresolvedAddressException e) {
                // Sun doesn't include the address that causes this
                // exception to be thrown, also UAE cannot be wrapped cleanly
                // so we log the exception in order to capture this critical
                // detail.
                LOG.warn("Cannot open channel to " + sid
                        + " at election address " + electionAddr, e);
                // Resolve hostname for this server in case the
                // underlying ip address has changed.
                if (view.containsKey(sid)) {
                    view.get(sid).recreateSocketAddresses();
                }
                throw e;
            } catch (IOException e) {
                LOG.warn("Cannot open channel to " + sid
                                + " at election address " + electionAddr,
                        e);
                // We can't really tell if the server is actually down or it failed
                // to connect to the server because the underlying IP address
                // changed. Resolve the hostname again just in case.
                if (view.containsKey(sid)) {
                    view.get(sid).recreateSocketAddresses();
                }
            }
        } else {
            LOG.debug("There is a connection already for server " + sid);
        }
    }


    /**
     * Try to establish a connection with each server if one
     * doesn't exist.
     *
     * 如果连接不存在，尝试与每个zk server建立连接
     */

    public void connectAll() {
        long sid;
        for (Enumeration<Long> en = queueSendMap.keys();
             en.hasMoreElements(); ) {
            sid = en.nextElement();
            connectOne(sid);
        }
    }


    /**
     * Check if all queues are empty, indicating that all messages have been delivered.
     *
     * 检查所有队列是否为空，表示所有消息都已传递
     */
    boolean haveDelivered() {
        for (ArrayBlockingQueue<ByteBuffer> queue : queueSendMap.values()) {
            LOG.debug("Queue size: " + queue.size());
            if (queue.size() == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Flag that it is time to wrap up all activities and interrupt the listener.
     */
    public void halt() {
        shutdown = true;
        LOG.debug("Halting listener");
        listener.halt();

        softHalt();

        // clear data structures used for auth
        if (connectionExecutor != null) {
            connectionExecutor.shutdown();
        }
        inprogressConnections.clear();
        resetConnectionThreadCount();
    }

    /**
     * A soft halt simply finishes workers.
     */
    public void softHalt() {
        for (SendWorker sw : senderWorkerMap.values()) {
            LOG.debug("Halting sender: " + sw);
            sw.finish();
        }
    }

    /**
     * Helper method to set socket options.
     * <p>
     * 设置socket options
     *
     * @param sock Reference to socket
     */
    private void setSockOpts(Socket sock) throws SocketException {
        sock.setTcpNoDelay(true);
        sock.setKeepAlive(tcpKeepAlive);
        sock.setSoTimeout(socketTimeout);
    }

    /**
     * Helper method to close a socket.
     * <p>
     * 关闭socket连接
     *
     * @param sock Reference to socket
     */
    private void closeSocket(Socket sock) {
        try {
            sock.close();
        } catch (IOException ie) {
            LOG.error("Exception while closing", ie);
        }
    }

    /**
     * Return number of worker threads
     */
    public long getThreadCount() {
        return threadCnt.get();
    }

    /**
     * Return number of connection processing threads.
     */
    public long getConnectionThreadCount() {
        return connectionThreadCnt.get();
    }

    /**
     * Reset the value of connection processing threads count to zero.
     */
    private void resetConnectionThreadCount() {
        connectionThreadCnt.set(0);
    }

    /**
     * Thread to listen on some port
     */
    public class Listener extends ZooKeeperThread {

        volatile ServerSocket ss = null;

        public Listener() {
            // During startup of thread, thread name will be overridden to
            // specific election address
            super("ListenerThread");
        }

        /**
         * Sleeps on accept().
         */
        @Override
        public void run() {
            int numRetries = 0;
            InetSocketAddress addr;
            while ((!shutdown) && (numRetries < 3)) {
                try {
                    ss = new ServerSocket();
                    ss.setReuseAddress(true);
                    if (listenOnAllIPs) {
                        int port = view.get(QuorumCnxManager.this.mySid)
                                .electionAddr.getPort();
                        addr = new InetSocketAddress(port);
                    } else {
                        addr = view.get(QuorumCnxManager.this.mySid)
                                .electionAddr;
                    }
                    LOG.info("My election bind port: " + addr.toString());
                    setName(view.get(QuorumCnxManager.this.mySid)
                            .electionAddr.toString());
                    ss.bind(addr);
                    while (!shutdown) {
                        Socket client = ss.accept();
                        setSockOpts(client);
                        LOG.info("Received connection request "
                                + client.getRemoteSocketAddress());

                        // Receive and handle the connection request
                        // asynchronously if the quorum sasl authentication is
                        // enabled. This is required because sasl server
                        // authentication process may take few seconds to finish,
                        // this may delay next peer connection requests.
                        if (quorumSaslAuthEnabled) {
                            receiveConnectionAsync(client);
                        } else {
                            receiveConnection(client);
                        }

                        numRetries = 0;
                    }
                } catch (IOException e) {
                    LOG.error("Exception while listening", e);
                    numRetries++;
                    try {
                        ss.close();
                        Thread.sleep(1000);
                    } catch (IOException ie) {
                        LOG.error("Error closing server socket", ie);
                    } catch (InterruptedException ie) {
                        LOG.error("Interrupted while sleeping. " +
                                "Ignoring exception", ie);
                    }
                }
            }
            LOG.info("Leaving listener");
            if (!shutdown) {
                LOG.error("As I'm leaving the listener thread, "
                        + "I won't be able to participate in leader "
                        + "election any longer: "
                        + view.get(QuorumCnxManager.this.mySid).electionAddr);
            }
        }

        /**
         * Halts this listener thread.
         */
        void halt() {
            try {
                LOG.debug("Trying to close listener: " + ss);
                if (ss != null) {
                    LOG.debug("Closing listener: "
                            + QuorumCnxManager.this.mySid);
                    ss.close();
                }
            } catch (IOException e) {
                LOG.warn("Exception when shutting down listener: " + e);
            }
        }
    }

    /**
     * Thread to send messages. Instance waits on a queue, and send a message as
     * soon as there is one available. If connection breaks, then opens a new
     * one.
     * <p>
     * 线程发送消息。实例等待队列，并在有可用消息时立即发送消息。如果连接中断，则打开一个新连接。
     */
    class SendWorker extends ZooKeeperThread {
        Long sid;
        Socket sock;
        RecvWorker recvWorker;
        volatile boolean running = true;
        DataOutputStream dout;

        /**
         * An instance of this thread receives messages to send
         * through a queue and sends them to the server sid.
         * <p>
         * 此线程的一个实例接收要通过队列发送的消息，并将它们发送到sid(zk server)
         *
         * @param sock Socket to remote peer
         * @param sid  Server identifier of remote peer
         */
        SendWorker(Socket sock, Long sid) {
            super("SendWorker:" + sid);
            this.sid = sid;
            this.sock = sock;
            recvWorker = null;
            try {
                dout = new DataOutputStream(sock.getOutputStream());
            } catch (IOException e) {
                LOG.error("Unable to access socket output stream", e);
                closeSocket(sock);
                running = false;
            }
            LOG.debug("Address of remote peer: " + this.sid);
        }

        synchronized void setRecv(RecvWorker recvWorker) {
            this.recvWorker = recvWorker;
        }

        /**
         * Returns RecvWorker that pairs up with this SendWorker.
         *
         * @return RecvWorker
         */
        synchronized RecvWorker getRecvWorker() {
            return recvWorker;
        }

        synchronized boolean finish() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Calling finish for " + sid);
            }

            if (!running) {
                /*
                 * Avoids running finish() twice.
                 */
                return running;
            }

            running = false;
            closeSocket(sock);
            // channel = null;

            this.interrupt();
            if (recvWorker != null) {
                recvWorker.finish();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Removing entry from senderWorkerMap sid=" + sid);
            }
            senderWorkerMap.remove(sid, this);
            threadCnt.decrementAndGet();
            return running;
        }

        synchronized void send(ByteBuffer b) throws IOException {
            byte[] msgBytes = new byte[b.capacity()];
            try {
                b.position(0);
                b.get(msgBytes);
            } catch (BufferUnderflowException be) {
                LOG.error("BufferUnderflowException ", be);
                return;
            }
            dout.writeInt(b.capacity());
            dout.write(b.array());
            dout.flush();
        }

        @Override
        public void run() {
            threadCnt.incrementAndGet();
            try {
                /**
                 * If there is nothing in the queue to send, then we
                 * send the lastMessage to ensure that the last message
                 * was received by the peer. The message could be dropped
                 * in case self or the peer shutdown their connection
                 * (and exit the thread) prior to reading/processing
                 * the last message. Duplicate messages are handled correctly
                 * by the peer.
                 *
                 * If the send queue is non-empty, then we have a recent
                 * message than that stored in lastMessage. To avoid sending
                 * stale message, we should send the message in the send queue.
                 */
                ArrayBlockingQueue<ByteBuffer> bq = queueSendMap.get(sid);
                if (bq == null || isSendQueueEmpty(bq)) {
                    ByteBuffer b = lastMessageSent.get(sid);
                    if (b != null) {
                        LOG.debug("Attempting to send lastMessage to sid=" + sid);
                        send(b);
                    }
                }
            } catch (IOException e) {
                LOG.error("Failed to send last message. Shutting down thread.", e);
                this.finish();
            }

            try {
                while (running && !shutdown && sock != null) {

                    ByteBuffer b = null;
                    try {
                        ArrayBlockingQueue<ByteBuffer> bq = queueSendMap
                                .get(sid);
                        if (bq != null) {
                            b = pollSendQueue(bq, 1000, TimeUnit.MILLISECONDS);
                        } else {
                            LOG.error("No queue of incoming messages for " +
                                    "server " + sid);
                            break;
                        }

                        if (b != null) {
                            lastMessageSent.put(sid, b);
                            send(b);
                        }
                    } catch (InterruptedException e) {
                        LOG.warn("Interrupted while waiting for message on queue",
                                e);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Exception when using channel: for id " + sid
                        + " my id = " + QuorumCnxManager.this.mySid
                        + " error = " + e);
            }
            this.finish();
            LOG.warn("Send worker leaving thread");
        }
    }

    /**
     * Thread to receive messages. Instance waits on a socket read. If the
     * channel breaks, then removes itself from the pool of receivers.
     */
    class RecvWorker extends ZooKeeperThread {
        Long sid;
        Socket sock;
        volatile boolean running = true;
        final DataInputStream din;
        final SendWorker sw;

        RecvWorker(Socket sock, DataInputStream din, Long sid, SendWorker sw) {
            super("RecvWorker:" + sid);
            this.sid = sid;
            this.sock = sock;
            this.sw = sw;
            this.din = din;
            try {
                // OK to wait until socket disconnects while reading.
                sock.setSoTimeout(0);
            } catch (IOException e) {
                LOG.error("Error while accessing socket for " + sid, e);
                closeSocket(sock);
                running = false;
            }
        }

        /**
         * Shuts down this worker
         *
         * @return boolean  Value of variable running
         */
        synchronized boolean finish() {
            if (!running) {
                /*
                 * Avoids running finish() twice.
                 */
                return running;
            }
            running = false;

            this.interrupt();
            threadCnt.decrementAndGet();
            return running;
        }

        @Override
        public void run() {
            threadCnt.incrementAndGet();
            try {
                while (running && !shutdown && sock != null) {
                    /**
                     * Reads the first int to determine the length of the
                     * message
                     */
                    int length = din.readInt();
                    if (length <= 0 || length > PACKETMAXSIZE) {
                        throw new IOException(
                                "Received packet with invalid packet: "
                                        + length);
                    }
                    /**
                     * Allocates a new ByteBuffer to receive the message
                     */
                    byte[] msgArray = new byte[length];
                    din.readFully(msgArray, 0, length);
                    ByteBuffer message = ByteBuffer.wrap(msgArray);
                    addToRecvQueue(new Message(message.duplicate(), sid));
                }
            } catch (Exception e) {
                LOG.warn("Connection broken for id " + sid + ", my id = "
                        + QuorumCnxManager.this.mySid + ", error = ", e);
            } finally {
                LOG.warn("Interrupting SendWorker");
                sw.finish();
                if (sock != null) {
                    closeSocket(sock);
                }
            }
        }
    }

    /**
     * Inserts an element in the specified queue. If the Queue is full, this
     * method removes an element from the head of the Queue and then inserts
     * the element at the tail. It can happen that the an element is removed
     * by another thread in {@link SendWorker#processMessage() processMessage}
     * method before this method attempts to remove an element from the queue.
     * This will cause {@link ArrayBlockingQueue#remove() remove} to throw an
     * exception, which is safe to ignore.
     * <p>
     * 向指定队列中插入元素。如果队列已满，则此方法从队列的头部移除元素，然后在尾部插入元素。
     * 在此方法尝试从队列中删除元素之前，可能会发生一个元素被{@link SendWorker＃processMessage() processMessage}方法中的另一个线程删除。
     * 这将导致{@link ArrayBlockingQueue #remove() remove}抛出异常，这是可以安全忽略的。
     * <p>
     * Unlike {@link #addToRecvQueue(Message) addToRecvQueue} this method does
     * not need to be synchronized since there is only one thread that inserts
     * an element in the queue and another thread that reads from the queue.
     * <p>
     * 与{@link #addToRecvQueue(Message) addToRecvQueue}不同，此方法不需要同步，因为只有一个线程在队列中插入元素，而另一个线程从队列中读取。
     *
     * @param queue  Reference to the Queue
     * @param buffer Reference to the buffer to be inserted in the queue
     */
    private void addToSendQueue(ArrayBlockingQueue<ByteBuffer> queue,
                                ByteBuffer buffer) {
        if (queue.remainingCapacity() == 0) {
            try {
                queue.remove();
            } catch (NoSuchElementException ne) {
                // element could be removed by poll()
                LOG.debug("Trying to remove from an empty " +
                        "Queue. Ignoring exception " + ne);
            }
        }

        try {
            queue.add(buffer);
        } catch (IllegalStateException ie) {
            // This should never happen
            LOG.error("Unable to insert an element in the queue " + ie);
        }
    }

    /**
     * Returns true if queue is empty.
     *
     * @param queue Reference to the queue
     * @return true if the specified queue is empty
     */
    private boolean isSendQueueEmpty(ArrayBlockingQueue<ByteBuffer> queue) {
        return queue.isEmpty();
    }

    /**
     * Retrieves and removes buffer at the head of this queue,
     * waiting up to the specified wait time if necessary for an element to
     * become available.
     * <p>
     * {@link ArrayBlockingQueue#poll(long, TimeUnit)}
     */
    private ByteBuffer pollSendQueue(ArrayBlockingQueue<ByteBuffer> queue,
                                     long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    /**
     * Inserts an element in the {@link #recvQueue}. If the Queue is full, this
     * methods removes an element from the head of the Queue and then inserts
     * the element at the tail of the queue.
     * <p>
     * This method is synchronized to achieve fairness between two threads that
     * are trying to insert an element in the queue. Each thread checks if the
     * queue is full, then removes the element at the head of the queue, and
     * then inserts an element at the tail. This three-step process is done to
     * prevent a thread from blocking while inserting an element in the queue.
     * If we do not synchronize the call to this method, then a thread can grab
     * a slot in the queue created by the second thread. This can cause the call
     * to insert by the second thread to fail.
     * Note that synchronizing this method does not block another thread
     * from polling the queue since that synchronization is provided by the
     * queue itself.
     *
     * @param msg Reference to the message to be inserted in the queue
     */
    public void addToRecvQueue(Message msg) {
        synchronized (recvQLock) {
            if (recvQueue.remainingCapacity() == 0) {
                try {
                    recvQueue.remove();
                } catch (NoSuchElementException ne) {
                    // element could be removed by poll()
                    LOG.debug("Trying to remove from an empty " +
                            "recvQueue. Ignoring exception " + ne);
                }
            }
            try {
                recvQueue.add(msg);
            } catch (IllegalStateException ie) {
                // This should never happen
                LOG.error("Unable to insert element in the recvQueue " + ie);
            }
        }
    }

    /**
     * Retrieves and removes a message at the head of this queue,
     * waiting up to the specified wait time if necessary for an element to
     * become available.
     * <p>
     * {@link ArrayBlockingQueue#poll(long, TimeUnit)}
     */
    public Message pollRecvQueue(long timeout, TimeUnit unit)
            throws InterruptedException {
        return recvQueue.poll(timeout, unit);
    }

    /**
     * 判断与给定sid(zk server)是否已建立连接
     *
     * @param peerSid
     * @return
     */
    public boolean connectedToPeer(long peerSid) {
        return senderWorkerMap.get(peerSid) != null;
    }
}
