package wang.xiaoluobo.zookeeper;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * zk client
 *
 * 更多curator api可以参考 https://zjcscut.github.io/2018/12/16/zookeeper-curator-usage
 *
 * @author wangyd
 * @date 2019/1/14
 */
public class ZkClient {

    private static final ExponentialBackoffRetry DEFAULT_RETRY_STRATEGY = new ExponentialBackoffRetry(1000, 3);

    private CuratorFramework client;

    private ZkClient() {

    }

    /**
     * Create a client instance
     *
     * @param hosts host strings: zk01:2181,zk02:2181,zk03:2181
     */
    public static ZkClient newClient(String hosts) {
        return newClient(hosts, DEFAULT_RETRY_STRATEGY, "");
    }

    /**
     * Create a client instance
     *
     * @param hosts     host strings: zk01:2181,zk02:2181,zk03:2181
     * @param namespace path root, such as app name
     */
    public static ZkClient newClient(String hosts, String namespace) {
        return newClient(hosts, DEFAULT_RETRY_STRATEGY, namespace);
    }

    /**
     * Create a client instance
     *
     * @param hosts         host strings: zk01:2181,zk02:2181,zk03:2181
     * @param namespace     path root, such as app name
     * @param retryStrategy client retry strategy
     */
    public static ZkClient newClient(String hosts, ExponentialBackoffRetry retryStrategy, String namespace) {
        ZkClient zc = new ZkClient();
        zc.client = CuratorFrameworkFactory.builder()
                .connectString(hosts).retryPolicy(retryStrategy).namespace(namespace).build();
        zc.client.start();
        return zc;
    }

    public CuratorFramework client() {
        return client;
    }

    /**
     * Create an persistent path
     *
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String create(String path, byte[] data) throws Exception {
        return client.create().withMode(CreateMode.PERSISTENT).forPath(path, data);
    }

    /**
     * Create an persistent path
     *
     * @param path path
     * @param data string data
     * @return the path created
     * @throws Exception
     */
    public String create(String path, String data) throws Exception {
        return create(path, data.getBytes("UTF-8"));
    }

    /**
     * Create an persistent path, save the object to json
     *
     * @param path path
     * @param obj  object
     * @return the path created
     * @throws Exception
     */
    public String createJson(String path, Object obj) throws Exception {
        return create(path, JSON.toJSONString(obj));
    }

    /**
     * Create an persistent path
     *
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createSequential(String path, byte[] data) throws Exception {
        return client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, data);
    }

    /**
     * Create an persistent path
     *
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createSequential(String path, String data) throws Exception {
        return createSequential(path, data.getBytes("UTF-8"));
    }

    /**
     * Create an persistent path
     *
     * @param path path
     * @param obj  a object
     * @return the path created
     * @throws Exception
     */
    public String createSequentialJson(String path, Object obj) throws Exception {
        return createSequential(path, JSON.toJSONString(obj).getBytes("UTF-8"));
    }

    /**
     * Create an ephemeral path
     *
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeral(String path, byte[] data) throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data);
    }

    /**
     * Create an ephemeral path
     *
     * @param path path
     * @param data string data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeral(String path, String data) throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes("UTF-8"));
    }

    /**
     * Create an ephemeral path
     *
     * @param path path
     * @param obj  object data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralJson(String path, Object obj) throws Exception {
        return createEphemeral(path, JSON.toJSONString(obj));
    }

    /**
     * Create an ephemeral path
     *
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralSequential(String path, byte[] data) throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data);
    }

    /**
     * Create an ephemeral path
     *
     * @param path path
     * @param data string data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralSequential(String path, String data) throws Exception {
        return createEphemeralSequential(path, data.getBytes("UTF-8"));
    }

    /**
     * Create an ephemeral and sequential path
     *
     * @param path path
     * @param obj  object
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralSequentialJson(String path, Object obj) throws Exception {
        return createEphemeralSequential(path, JSON.toJSONString(obj));
    }

    /**
     * Create a node if not exists
     *
     * @param path path
     * @param data path data
     * @return return true if create
     * @throws Exception
     */
    public Boolean createIfNotExists(String path, byte[] data) throws Exception {
        Stat pathStat = client.checkExists().forPath(path);
        if (pathStat == null) {
            String nodePath = client.create().forPath(path, data);
            return Strings.isNullOrEmpty(nodePath) ? Boolean.FALSE : Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Delete the node
     *
     * @param path node path
     */
    public void delete(String path) {
        try {
            client.delete().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException("failed to delete path");
        }
    }

    /**
     * get the data of path
     *
     * @param path the node path
     * @return the byte data of the path
     */
    public byte[] get(String path) {
        try {
            return client.getData().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException("failed to get path data");
        }
    }

    /**
     * get the node data as string
     *
     * @param path path data
     * @return return the data string or null
     */
    public String getString(String path) {
        byte[] data = get(path);
        if (data != null) {
            try {
                return new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * get the node data as an object
     *
     * @param path  node path
     * @param clazz class
     * @return json object or null
     */
    public <T> T getJson(String path, Class<T> clazz) {
        byte[] data = get(path);
        if (data != null) {
            try {
                String json = new String(data, "UTF-8");
                return JSON.parseObject(json, clazz);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * new a watcher of path child
     *
     * @param path     the parent path
     * @param listener a listener
     *                 NOTE:
     *                 Only watch first level children, not recursive
     */
    public void newChildWatcher(String path, ChildListener listener) throws Exception {
        newChildWatcher(path, listener, Boolean.FALSE);
    }

    /**
     * new a watcher of path child
     *
     * @param path          the parent path
     * @param listener      a listener
     * @param cacheNodeData cache child or not
     *                      NOTE:
     *                      Only watch first level children, not recursive
     */
    public void newChildWatcher(String path, ChildListener listener, Boolean cacheNodeData) throws Exception {
        new ChildWatcher(path, cacheNodeData, listener);
    }

    public static abstract class ChildListener {

        protected void onAdd(String path, byte[] data) {
        }

        protected void onDelete(String path) {
        }

        protected void onUpdate(String path, byte[] newData) {
        }
    }

    /**
     * Child Node Watcher
     */
    private class ChildWatcher {

        private final PathChildrenCache cacher;

        public ChildWatcher(String path, Boolean cache, final ChildListener listener) throws Exception {
            cacher = new PathChildrenCache(client, path, cache);
            cacher.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    PathChildrenCacheEvent.Type eventType = event.getType();
                    String path = event.getData().getPath();
                    switch (eventType) {
                        case CHILD_ADDED:
                            listener.onAdd(path, client.getData().forPath(path));
                            break;
                        case CHILD_REMOVED:
                            listener.onDelete(path);
                            break;
                        case CHILD_UPDATED:
                            listener.onUpdate(path, client.getData().forPath(path));
                            break;
                        case CONNECTION_RECONNECTED:
                            cacher.rebuild();
                        default:
                            break;
                    }
                }
            });
            cacher.start();
        }
    }

    /**
     * lock the path
     *
     * @param path the path
     */
    public Lock newLock(String path) {
        return new Lock(path);
    }

    /**
     * Distribute lock
     */
    public class Lock {

        private InterProcessMutex mutex;

        private String path;

        private Boolean locked = Boolean.FALSE;

        private Lock(String path) {
            this.path = path;
            this.mutex = new InterProcessMutex(client, path);
        }

        public void lock() {
            try {
                mutex.acquire();
                locked = Boolean.TRUE;
            } catch (Exception e) {
                locked = Boolean.FALSE;
            }
        }

        /**
         * lock with timeout
         *
         * @param timeout timeout(ms)
         * @return lock successfully or not
         */
        public Boolean lock(long timeout) {
            try {
                locked = mutex.acquire(timeout, TimeUnit.MILLISECONDS);
                return locked;
            } catch (Exception e) {
                locked = Boolean.FALSE;
            }
            return Boolean.FALSE;
        }

        public void unlock() {
            if (locked) {
                try {
                    mutex.release();
                } catch (Exception e) {
                    throw new RuntimeException("failed to unlock: " + this.path);
                }
            }
        }
    }
}
