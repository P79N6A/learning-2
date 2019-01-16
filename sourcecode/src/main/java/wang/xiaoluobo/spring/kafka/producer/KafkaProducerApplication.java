package wang.xiaoluobo.spring.kafka.producer;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import wang.xiaoluobo.redis.configuration.RedisConfig;

import javax.annotation.PostConstruct;

/**
 * @author wangyd
 * @date 2018/9/18
 */
@SpringBootApplication
@Log4j2
@Configuration
@Import(RedisConfig.class)
public class KafkaProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${spring.kafka.template.default-topic:test_topic}")
    private String TOPIC_NAME;

    @PostConstruct
    public void start() {
        StringBuilder sb = null;
        while (true) {
            try {
                sb = new StringBuilder()
                        .append(System.currentTimeMillis());

                String str = sb.toString();
                log.info("send msg({}) to kafka.", str);
                kafkaTemplate.send(TOPIC_NAME, String.valueOf(RandomUtils.nextInt(0, 3)), str.getBytes());
                Thread.sleep(500L);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
