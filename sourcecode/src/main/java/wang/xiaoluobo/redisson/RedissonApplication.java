package wang.xiaoluobo.redisson;

import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * redis 分布式锁
 * @see RedissonAutoConfiguration#redisson()
 */
@SpringBootApplication
public class RedissonApplication {

    @Autowired
    private RedissonClient redissonClient;

    public static void main(String[] args) {
        SpringApplication.run(RedissonApplication.class, args);
    }

    @PostConstruct
    public void init() {
        RLock lock = redissonClient.getLock("myRedisson");
        while (true) {
            try {
                lock.tryLock(5, 5, TimeUnit.SECONDS);
                Thread.sleep(RandomUtils.nextInt(0, 10) * 1000L);
                System.out.println("get redisson lock");
            } catch (Exception e) {
                System.out.println("lock error");
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
