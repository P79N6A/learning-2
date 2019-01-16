package wang.xiaoluobo.spring.kafka.consumer;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.xiaoluobo.redis.configuration.RedisConfig;

import javax.annotation.PostConstruct;

/**
 * @author wangyd
 * @date 2017/7/6
 */
@SpringBootApplication
@Configuration
@Log4j2
@Import(RedisConfig.class)
public class KafkaConsumerApplication implements DisposableBean {

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApplication.class, args);
    }

    @Autowired
    private MyKafkaConfig myKafkaConfig;

    @Autowired
    private MyKafkaConsumerContainer myKafkaConsumerContainer;


    @PostConstruct
    public void start() {
        myKafkaConsumerContainer.createContainer(myKafkaConfig.topicName, myKafkaConfig.groupId);
    }

    @Override
    public void destroy() throws Exception {
        myKafkaConsumerContainer.stopAllContainer();
    }
}
