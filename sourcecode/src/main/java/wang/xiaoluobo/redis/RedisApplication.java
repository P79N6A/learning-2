package wang.xiaoluobo.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import wang.xiaoluobo.redis.configuration.RedisConfig;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * redis template实现分布式锁
 * @author wangyd
 * @date 2018/8/16
 */
@SpringBootApplication
@Import(RedisConfig.class)
public class RedisApplication {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    /**
     * redis template分布式锁
     */
    @PostConstruct
    public void init() {
        try {
            redisTemplate.opsForValue().setIfAbsent("test", "test", 30, TimeUnit.SECONDS);

            int n = 100;
            CountDownLatch countDownLatch = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                int tmp = i;
                new Thread(() -> {
                    String value = redisTemplate.opsForValue().get("test");
                    if (StringUtils.isNotBlank(value)) {
                        String oldValue = redisTemplate.opsForValue().getAndSet("test", String.valueOf(tmp));
                        if (value.equals(oldValue)) {
                            System.out.println(Thread.currentThread().getName() + "---get lock success---" + System.currentTimeMillis() + "---update value---" + tmp);
                        } else {
                            System.out.println(Thread.currentThread().getName() + "***get lock failed***" + System.currentTimeMillis() + "***value***" + tmp);
                        }
                        countDownLatch.countDown();
                    }
                }).start();
            }

            countDownLatch.await();
            System.out.println(redisTemplate.opsForValue().get("test"));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("redis application shut down success.")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
