package wang.xiaoluobo.spring.kafka.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangyd
 * @date 2017-07-02
 */
@Configuration
public class MyKafkaConfig {

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    protected String bootstrapServers;

    @Value("${kafka.topic}")
    protected String topicName;


    @Value("${kafka.consumer.groupId}")
    protected String groupId;

    @Value("${kafka.consumer.clientId}")
    protected String clientId;


    @Value("${kafka.consumer.session.timeout-ms:30000}")
    protected String sessionTimeoutMs;

    @Value("${kafka.consumer.enable.autocommit:true}")
    protected boolean enableAutoCommit;

    @Value("${kafka.consumer.autocommit.interval-ms:500}")
    protected String autoCommitIntervalMs;

    @Value("${kafka.consumer.key-serializer:kafka.serializer.StringEncoder}")
    protected String consumerKeySerializer;

    @Value("${kafka.consumer.value-serializer:kafka.serializer.StringEncoder}")
    protected String consumerValueSerializer;

    @Value("${kafka.consumer.concurrency-size:1}")
    protected int concurrencySize;

    @Value("${kafka.consumer.isAutoCommit:false}")
    protected boolean isAutoCommit;

    @Value("${kafka.consumer.request.timeout-ms:31000}")
    protected int requestTimeoutMs;

    @Value("${kafka.consumber.max-poll-records:100}")
    protected int maxPollRecords;
}
