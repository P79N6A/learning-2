/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.server.quorum;

import org.apache.yetus.audience.InterfaceAudience;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.auth.QuorumAuth;
import org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical;
import org.apache.zookeeper.server.quorum.flexible.QuorumMaj;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 该类负责读取解析zoo.cfg配置文件
 *
 * zk配置中心
 */
@InterfaceAudience.Public
public class QuorumPeerConfig {
    private static final Logger LOG = LoggerFactory.getLogger(QuorumPeerConfig.class);

    protected InetSocketAddress clientPortAddress;
    protected String dataDir;
    protected String dataLogDir;

    /**
     * zk server之前心跳时间
     * zoo.cfg默认值是2000ms
     * {@code ZooKeeperServer.DEFAULT_TICK_TIME = 3000}
     */
    protected int tickTime = ZooKeeperServer.DEFAULT_TICK_TIME;

    /**
     * zk server接收单台客户端最大连接数(防止被攻击或被单个客户端耗费过多资源)
     */
    protected int maxClientCnxns = 60;

    /** defaults to -1 if not set explicitly */
    protected int minSessionTimeout = -1;
    /** defaults to -1 if not set explicitly */
    protected int maxSessionTimeout = -1;

    /**
     * follower连接并同步到leader的最大时间
     *
     * 默认值是10
     */
    protected int initLimit;

    /**
     * leader和follower间心跳检测的最大延迟时间(超过syncLimit，则follower会被丢弃)
     *
     * 默认值是5
     */
    protected int syncLimit;

    /**
     * Leader选举算法:
     * 0: 原始的基于UDP的版本
     * 1: 快速Leader选举基于UDP的无身份验证的版本
     * 2: 快速Leader选举基于UDP的身份验证的版本
     * 3: 快速Leader选举基于TCP的版本
     *
     * 目前默认值是3
     */
    protected int electionAlg = 3;
    protected int electionPort = 2182;
    protected boolean quorumListenOnAllIPs = false;
    protected final HashMap<Long,QuorumServer> servers =
        new HashMap<>();
    protected final HashMap<Long,QuorumServer> observers =
        new HashMap<>();

    /**
     * sid
     */
    protected long serverId;

    /**
     * key: sid
     * value: 权重，默认值为1
     */
    protected HashMap<Long, Long> serverWeight = new HashMap<>();

    /**
     * key: sid
     * value: gid
     * serverGroup与serverWeight同时使用，权重等于0的zk server不参与投票
     *
     * This document gives an example of how to use hierarchical quorums. The basic idea is very simple.
     * First, we split servers into groups, and add a line for each group listing the servers that form this group.
     * Next we have to assign a weight to each server.
     *
     * 使用分层仲裁的示例(每组是否存活，判断该组存活zk server权重是否过半)
     * (1)将servers分成组，并为该组的server的每个组添加一行
     * (2)为每个server分配权重
     *
     * group.1=1:2:3
     * group.2=4:5:6
     * group.3=7:8:9
     *
     * weight.1=1
     * weight.2=1
     * weight.3=1
     * weight.4=1
     * weight.5=1
     * weight.6=1
     * weight.7=1
     * weight.8=1
     * weight.9=1
     */
    protected HashMap<Long, Long> serverGroup = new HashMap<>();

    /**
     * group计数器
     */
    protected int numGroups = 0;

    /**
     * 所有仲裁验证器都必须实现一个名为containsQuorum的方法，该方法验证一组zk server标识符是否构成仲裁
     *
     * @see QuorumVerifier
     */
    protected QuorumVerifier quorumVerifier;

    /**
     * 客户端与zookeeper通信过程中会产生非常多的日志，而且zookeeper也会将内存中的数据作为snapshot保存下来，
     *
     * zoo.cfg autopurge.snapRetainCount zk保留多少个snapshot快照(最少3个snapshot快照)
     */
    protected int snapRetainCount = 3;

    /**
     * zoo.cfg autopurge.purgeInterval zk多少小时清理一次日志snapshot快照
     *
     * 默认为0，表示禁用自动清理
     */
    protected int purgeInterval = 0;

    protected boolean syncEnabled = true;

    protected LearnerType peerType = LearnerType.PARTICIPANT;

    /** Configurations for the quorumpeer-to-quorumpeer sasl authentication */
    protected boolean quorumServerRequireSasl = false;
    protected boolean quorumLearnerRequireSasl = false;
    protected boolean quorumEnableSasl = false;
    protected String quorumServicePrincipal = QuorumAuth.QUORUM_KERBEROS_SERVICE_PRINCIPAL_DEFAULT_VALUE;
    protected String quorumLearnerLoginContext = QuorumAuth.QUORUM_LEARNER_SASL_LOGIN_CONTEXT_DFAULT_VALUE;
    protected String quorumServerLoginContext = QuorumAuth.QUORUM_SERVER_SASL_LOGIN_CONTEXT_DFAULT_VALUE;
    protected int quorumCnxnThreadsSize;

    /**
     * Minimum snapshot retain count.
     * @see org.apache.zookeeper.server.PurgeTxnLog#purge(File, File, int)
     */
    private final int MIN_SNAP_RETAIN_COUNT = 3;

    @SuppressWarnings("serial")
    public static class ConfigException extends Exception {
        public ConfigException(String msg) {
            super(msg);
        }
        public ConfigException(String msg, Exception e) {
            super(msg, e);
        }
    }

    private static String[] splitWithLeadingHostname(String s)
            throws ConfigException
    {
        /* Does it start with an IPv6 literal? */
        if (s.startsWith("[")) {
            int i = s.indexOf("]:");
            if (i < 0) {
                throw new ConfigException(s + " starts with '[' but has no matching ']:'");
            }

            String[] sa = s.substring(i + 2).split(":");
            String[] nsa = new String[sa.length + 1];
            nsa[0] = s.substring(1, i);
            System.arraycopy(sa, 0, nsa, 1, sa.length);

            return nsa;
        } else {
            return s.split(":");
        }
    }

    /**
     * Parse a ZooKeeper configuration file
     * @param path the patch of the configuration file
     * @throws ConfigException error processing configuration
     */
    public void parse(String path) throws ConfigException {
        File configFile = new File(path);

        LOG.info("Reading configuration from: " + configFile);

        try {
            if (!configFile.exists()) {
                throw new IllegalArgumentException(configFile.toString()
                        + " file is missing");
            }

            Properties cfg = new Properties();
            FileInputStream in = new FileInputStream(configFile);
            try {
                cfg.load(in);
            } finally {
                in.close();
            }

            parseProperties(cfg);
        } catch (IOException e) {
            throw new ConfigException("Error processing " + path, e);
        } catch (IllegalArgumentException e) {
            throw new ConfigException("Error processing " + path, e);
        }
    }

    /**
     * Parse config from a Properties.
     * @param zkProp Properties to parse from.
     * @throws IOException
     * @throws ConfigException
     */
    public void parseProperties(Properties zkProp)
    throws IOException, ConfigException {
        int clientPort = 0;
        String clientPortAddress = null;

        // --------------------读取配置文件开始--------------------
        for (Entry<Object, Object> entry : zkProp.entrySet()) {
            String key = entry.getKey().toString().trim();
            String value = entry.getValue().toString().trim();
            if (key.equals("dataDir")) {
                dataDir = value;
            } else if (key.equals("dataLogDir")) {
                dataLogDir = value;
            } else if (key.equals("clientPort")) {
                clientPort = Integer.parseInt(value);
            } else if (key.equals("clientPortAddress")) {
                clientPortAddress = value.trim();
            } else if (key.equals("tickTime")) {
                tickTime = Integer.parseInt(value);
            } else if (key.equals("maxClientCnxns")) {
                maxClientCnxns = Integer.parseInt(value);
            } else if (key.equals("minSessionTimeout")) {
                minSessionTimeout = Integer.parseInt(value);
            } else if (key.equals("maxSessionTimeout")) {
                maxSessionTimeout = Integer.parseInt(value);
            } else if (key.equals("initLimit")) {
                initLimit = Integer.parseInt(value);
            } else if (key.equals("syncLimit")) {
                syncLimit = Integer.parseInt(value);
            } else if (key.equals("electionAlg")) {
                electionAlg = Integer.parseInt(value);
            } else if (key.equals("quorumListenOnAllIPs")) {
                quorumListenOnAllIPs = Boolean.parseBoolean(value);
            } else if (key.equals("peerType")) {
                if (value.toLowerCase().equals("observer")) {
                    peerType = LearnerType.OBSERVER;
                } else if (value.toLowerCase().equals("participant")) {
                    peerType = LearnerType.PARTICIPANT;
                } else
                {
                    throw new ConfigException("Unrecognised peertype: " + value);
                }
            } else if (key.equals( "syncEnabled" )) {
                syncEnabled = Boolean.parseBoolean(value);
            } else if (key.equals("autopurge.snapRetainCount")) {
                snapRetainCount = Integer.parseInt(value);
            } else if (key.equals("autopurge.purgeInterval")) {
                purgeInterval = Integer.parseInt(value);
            } else if (key.startsWith("server.")) {
                int dot = key.indexOf('.');
                long sid = Long.parseLong(key.substring(dot + 1));
                String parts[] = splitWithLeadingHostname(value);
                if ((parts.length != 2) && (parts.length != 3) && (parts.length !=4)) {
                    LOG.error(value
                       + " does not have the form host:port or host:port:port " +
                       " or host:port:port:type");
                }
                LearnerType type = null;
                String hostname = parts[0];
                Integer port = Integer.parseInt(parts[1]);
                Integer electionPort = null;
                if (parts.length > 2){
                	electionPort=Integer.parseInt(parts[2]);
                }
                if (parts.length > 3){
                    if (parts[3].toLowerCase().equals("observer")) {
                        type = LearnerType.OBSERVER;
                    } else if (parts[3].toLowerCase().equals("participant")) {
                        type = LearnerType.PARTICIPANT;
                    } else {
                        throw new ConfigException("Unrecognised peertype: " + value);
                    }
                }
                if (type == LearnerType.OBSERVER){
                    observers.put(Long.valueOf(sid), new QuorumServer(sid, hostname, port, electionPort, type));
                } else {
                    servers.put(Long.valueOf(sid), new QuorumServer(sid, hostname, port, electionPort, type));
                }
            } else if (key.startsWith("group")) {
                int dot = key.indexOf('.');
                long gid = Long.parseLong(key.substring(dot + 1));

                numGroups++;

                String parts[] = value.split(":");
                for(String s : parts){
                    long sid = Long.parseLong(s);
                    if(serverGroup.containsKey(sid))
                        throw new ConfigException("Server " + sid + "is in multiple groups");
                    else
                        serverGroup.put(sid, gid);
                }

            } else if(key.startsWith("weight")) {
                int dot = key.indexOf('.');
                long sid = Long.parseLong(key.substring(dot + 1));
                serverWeight.put(sid, Long.parseLong(value));
            } else if (key.equals(QuorumAuth.QUORUM_SASL_AUTH_ENABLED)) {
                quorumEnableSasl = Boolean.parseBoolean(value);
            } else if (key.equals(QuorumAuth.QUORUM_SERVER_SASL_AUTH_REQUIRED)) {
                quorumServerRequireSasl = Boolean.parseBoolean(value);
            } else if (key.equals(QuorumAuth.QUORUM_LEARNER_SASL_AUTH_REQUIRED)) {
                quorumLearnerRequireSasl = Boolean.parseBoolean(value);
            } else if (key.equals(QuorumAuth.QUORUM_LEARNER_SASL_LOGIN_CONTEXT)) {
                quorumLearnerLoginContext = value;
            } else if (key.equals(QuorumAuth.QUORUM_SERVER_SASL_LOGIN_CONTEXT)) {
                quorumServerLoginContext = value;
            } else if (key.equals(QuorumAuth.QUORUM_KERBEROS_SERVICE_PRINCIPAL)) {
                quorumServicePrincipal = value;
            } else if (key.equals("quorum.cnxn.threads.size")) {
                quorumCnxnThreadsSize = Integer.parseInt(value);
            } else {
                System.setProperty("zookeeper." + key, value);
            }
        }
        // --------------------读取配置文件结束--------------------


        // --------------------参数校验开始-----------------------
        // 校验sasl配置是否正确
        if (!quorumEnableSasl && quorumServerRequireSasl) {
            throw new IllegalArgumentException(
                    QuorumAuth.QUORUM_SASL_AUTH_ENABLED
                    + " is disabled, so cannot enable "
                    + QuorumAuth.QUORUM_SERVER_SASL_AUTH_REQUIRED);
        }
        if (!quorumEnableSasl && quorumLearnerRequireSasl) {
            throw new IllegalArgumentException(
                    QuorumAuth.QUORUM_SASL_AUTH_ENABLED
                    + " is disabled, so cannot enable "
                    + QuorumAuth.QUORUM_LEARNER_SASL_AUTH_REQUIRED);
        }
        // If quorumpeer learner is not auth enabled then self won't be able to
        // join quorum. So this condition is ensuring that the quorumpeer learner
        // is also auth enabled while enabling quorum server require sasl.
        if (!quorumLearnerRequireSasl && quorumServerRequireSasl) {
            throw new IllegalArgumentException(
                    QuorumAuth.QUORUM_LEARNER_SASL_AUTH_REQUIRED
                    + " is disabled, so cannot enable "
                    + QuorumAuth.QUORUM_SERVER_SASL_AUTH_REQUIRED);
        }


        // Reset to MIN_SNAP_RETAIN_COUNT if invalid (less than 3)
        // PurgeTxnLog.purge(File, File, int) will not allow to purge less
        // than 3.
        // 如果无效(小于3)PurgeTxnLog.purge(File，File，int)将不允许清除少于3，则重置为MIN_SNAP_RETAIN_COUNT
        if (snapRetainCount < MIN_SNAP_RETAIN_COUNT) {
            LOG.warn("Invalid autopurge.snapRetainCount: " + snapRetainCount
                    + ". Defaulting to " + MIN_SNAP_RETAIN_COUNT);
            snapRetainCount = MIN_SNAP_RETAIN_COUNT;
        }

        if (dataDir == null) {
            throw new IllegalArgumentException("dataDir is not set");
        }
        if (dataLogDir == null) {
            dataLogDir = dataDir;
        }
        if (clientPort == 0) {
            throw new IllegalArgumentException("clientPort is not set");
        }
        if (clientPortAddress != null) {
            this.clientPortAddress = new InetSocketAddress(
                    InetAddress.getByName(clientPortAddress), clientPort);
        } else {
            this.clientPortAddress = new InetSocketAddress(clientPort);
        }

        if (tickTime == 0) {
            throw new IllegalArgumentException("tickTime is not set");
        }
        if (minSessionTimeout > maxSessionTimeout) {
            throw new IllegalArgumentException(
                    "minSessionTimeout must not be larger than maxSessionTimeout");
        }


        /**
         * 校验servers配置信息
         *
         * servers size:
         * 0: 启动失败
         * 1: 不能存在observer zk server，无法选出leader
         * 2: 集群至少3个节点，servers.size() % 2 == 0(非最佳配置，可以启动集群，建议奇数个server)
         */
        if (servers.size() == 0) {
            if (observers.size() > 0) {
                throw new IllegalArgumentException("Observers w/o participants is an invalid configuration");
            }
            // Not a quorum configuration so return immediately - not an error
            // case (for b/w compatibility), server will default to standalone
            // mode.
            return;
        } else if (servers.size() == 1) {
            if (observers.size() > 0) {
                throw new IllegalArgumentException("Observers w/o quorum is an invalid configuration");
            }

            // HBase currently adds a single server line to the config, for
            // b/w compatibility reasons we need to keep this here.
            LOG.error("Invalid configuration, only one server specified (ignoring)");
            servers.clear();
        } else if (servers.size() > 1) {
            if (servers.size() == 2) {
                LOG.warn("No server failure will be tolerated. " +
                    "You need at least 3 servers.");
            } else if (servers.size() % 2 == 0) {
                LOG.warn("Non-optimial configuration, consider an odd number of servers.");
            }
            if (initLimit == 0) {
                throw new IllegalArgumentException("initLimit is not set");
            }
            if (syncLimit == 0) {
                throw new IllegalArgumentException("syncLimit is not set");
            }
            /**
             * If using FLE, then every server requires a separate election
             * port.
             *
             * 使用fast leader election
             */
            if (electionAlg != 0) {
                for (QuorumServer s : servers.values()) {
                    if (s.electionAddr == null)
                        throw new IllegalArgumentException(
                                "Missing election port for server: " + s.id);
                }
            }


            /**
             * Default of quorum config is majority
             */
            if(serverGroup.size() > 0){
                if(servers.size() != serverGroup.size())
                    throw new ConfigException("Every server must be in exactly one group");
                /**
                 * The deafult weight of a server is 1
                 */
                for(QuorumServer s : servers.values()){
                    if(!serverWeight.containsKey(s.id))
                        serverWeight.put(s.id, (long) 1);
                }

                /**
                 * Set the quorumVerifier to be QuorumHierarchical
                 * 通过serverGroup和serverWeight分组计算组是否存活，再通过组存活计算zk server集群是否存活(过半原则)
                 *
                 * @param serverGroup  参考成员变量注释
                 * @param serverWeight 参考成员变量注释
                 * @see QuorumHierarchical#containsQuorum
                 */
                quorumVerifier = new QuorumHierarchical(numGroups,
                        serverWeight, serverGroup);
            } else {
                /**
                 * The default QuorumVerifier is QuorumMaj
                 *
                 * zk {@link QuorumVerifier} 默认实现 {@link QuorumMaj}
                 * 即投票是否过半
                 */
                LOG.info("Defaulting to majority quorums");
                quorumVerifier = new QuorumMaj(servers.size());
            }

            // Now add observers to servers, once the quorums have been
            // figured out
            // 将observers添加到servers中
            servers.putAll(observers);


            // dataDir目录下会生成一个myid配置文件，存储zk server的sid
            File myIdFile = new File(dataDir, "myid");
            if (!myIdFile.exists()) {
                throw new IllegalArgumentException(myIdFile.toString()
                        + " file is missing");
            }
            BufferedReader br = new BufferedReader(new FileReader(myIdFile));
            String myIdString;
            try {
                myIdString = br.readLine();
            } finally {
                br.close();
            }
            try {
                serverId = Long.parseLong(myIdString);
                MDC.put("myid", myIdString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("serverid " + myIdString
                        + " is not a number");
            }

            
            // Warn about inconsistent peer type
            // 这个变量名称。。。，设置当前zk server的server state
            LearnerType roleByServersList = observers.containsKey(serverId) ? LearnerType.OBSERVER
                    : LearnerType.PARTICIPANT;
            if (roleByServersList != peerType) {
                LOG.warn("Peer type from servers list (" + roleByServersList
                        + ") doesn't match peerType (" + peerType
                        + "). Defaulting to servers list.");
    
                peerType = roleByServersList;
            }
        }
        // --------------------参数校验开始-----------------------
    }

    public InetSocketAddress getClientPortAddress() { return clientPortAddress; }
    public String getDataDir() { return dataDir; }
    public String getDataLogDir() { return dataLogDir; }
    public int getTickTime() { return tickTime; }
    public int getMaxClientCnxns() { return maxClientCnxns; }
    public int getMinSessionTimeout() { return minSessionTimeout; }
    public int getMaxSessionTimeout() { return maxSessionTimeout; }

    public int getInitLimit() { return initLimit; }
    public int getSyncLimit() { return syncLimit; }
    public int getElectionAlg() { return electionAlg; }
    public int getElectionPort() { return electionPort; }
    
    public int getSnapRetainCount() {
        return snapRetainCount;
    }

    public int getPurgeInterval() {
        return purgeInterval;
    }
    
    public boolean getSyncEnabled() {
        return syncEnabled;
    }

    public QuorumVerifier getQuorumVerifier() {
        return quorumVerifier;
    }

    public Map<Long,QuorumServer> getServers() {
        return Collections.unmodifiableMap(servers);
    }

    public long getServerId() { return serverId; }

    public boolean isDistributed() { return servers.size() > 1; }

    public LearnerType getPeerType() {
        return peerType;
    }

    public Boolean getQuorumListenOnAllIPs() {
        return quorumListenOnAllIPs;
    }
}
