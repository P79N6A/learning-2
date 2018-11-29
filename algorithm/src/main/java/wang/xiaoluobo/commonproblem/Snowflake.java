package wang.xiaoluobo.commonproblem;

/**
 * snowflake的结构由5部分组成(以-分隔)，一共64位(Long型-转换成字符串长度为18):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * <p>
 * (1)第一位为未使用，
 * (2)41位为毫秒级时间(41位可以使用69年)
 * (3-4)5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点)
 * (5)12位是毫秒内的计数(12位的计数顺序号支持每个节点每毫秒产生4096个ID序号)
 * <p>
 * snowflake生成的ID整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由datacenter和workerId作区分)，并且效率较高。snowflake每秒能够产生26万个ID
 *
 * @author wangyd
 * @date 2018/11/29
 */
public class Snowflake {

    private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public Snowflake(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGenerate();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGenerate();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGenerate();
        }
        return timestamp;
    }

    protected long timeGenerate() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        Snowflake snowflake = new Snowflake(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = snowflake.nextId();
            System.out.println(id);
        }
    }
}
