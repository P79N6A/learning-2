package wang.xiaoluobo.zookeeper;

import java.util.List;

/**
 * @author wangyd
 * @date 2019/1/14
 */
public class ZkClientTest {

    private static final String HOST_PORT = "localhost:2181";
    private static final String PATH = "/";

    public static void main(String[] args) {
        getChildren();

        createNode();

        lock();

        watcher();
    }


    private static void getChildren() {
        try {
            ZkClient zkClient = ZkClient.newClient(HOST_PORT, "test");
            List<String> list = zkClient.client().getChildren().forPath(PATH);
            System.out.println("path(" + PATH + ") children nodes:");
            for (String p : list) {
                System.out.println(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点/test/myseq0000000005
     * zk连接断开时，删除当前节点
     * 再次连接时，myseq0000000009，自增序号加1
     */
    private static void createNode() {
        try {
            ZkClient zkClient = ZkClient.newClient(HOST_PORT, "test");
            zkClient.createIfNotExists("/servers", null);
            final String serverPath = zkClient.createEphemeralSequential("/myseq", HOST_PORT.getBytes());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    zkClient.delete(serverPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分布式锁
     * 加锁
     * [zk: localhost:2181(CONNECTED) 146] ls /test/mylock
     * [_c_5a537c75-8ca4-490f-975d-3370860b1ff8-lock-0000000006]
     * 锁已经被释放，故值为空
     * [zk: localhost:2181(CONNECTED) 147] ls /test/mylock
     * []
     * <p>
     * 再次加锁，自增序号自增1
     * [zk: localhost:2181(CONNECTED) 148] ls /test/mylock
     * [_c_ee61f8eb-fa7b-4353-94c5-4b0093fa5654-lock-0000000007]
     * [zk: localhost:2181(CONNECTED) 149] ls /test/mylock
     * []
     */
    private static void lock() {
        try {
            ZkClient zkClient = ZkClient.newClient(HOST_PORT, "test");
            ZkClient.Lock lock = zkClient.newLock("/mylock");
            lock.lock();
            System.out.println("testLock is locking me.");
            try {
                System.out.println("doing work start");
                Thread.sleep(25 * 1000L);
                System.out.println("doing work end");
            } finally {
                if (lock != null) {
                    lock.unlock();
                    System.out.println("testLock is unlock me.");
                }
            }


            if (!lock.lock(10 * 1000L)) {
                System.out.println("I choose to giving up");
                return;
            }

            System.out.println("testLockTimeout is lock me.");
            try {
                System.out.println("doing my work start");
                Thread.sleep(10 * 1000L);
                System.out.println("doing my work end");
            } finally {
                if (lock != null) {
                    lock.unlock();
                    System.out.println("testLockTimeout is unlock me.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * [zk: localhost:2181(CONNECTED) 134] create /test/mywatcher/w "watcher"
     * Created /test/mywatcher/w
     * [zk: localhost:2181(CONNECTED) 135] set /test/mywatcher/w ""
     * cZxid = 0x29b
     * ctime = Mon Jan 14 19:39:57 CST 2019
     * mZxid = 0x29c
     * mtime = Mon Jan 14 19:40:06 CST 2019
     * pZxid = 0x29b
     * cversion = 0
     * dataVersion = 1
     * aclVersion = 0
     * ephemeralOwner = 0x0
     * dataLength = 0
     * numChildren = 0
     * [zk: localhost:2181(CONNECTED) 136] delete /test/mywatcher/w
     */
    private static void watcher() {
        try {
            ZkClient zkClient = ZkClient.newClient(HOST_PORT, "test");
            zkClient.newChildWatcher("/mywatcher", new ZkClient.ChildListener() {
                @Override
                protected void onAdd(String path, byte[] data) {
                    System.out.println("path " + path + " is created: " + new String(data));
                }

                @Override
                protected void onDelete(String path) {
                    System.out.println("path " + path + " is deleted.");
                }

                @Override
                protected void onUpdate(String path, byte[] newData) {
                    System.out.println("path " + path + " is updated: " + new String(newData));
                }
            });

            Thread.sleep(5000 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
