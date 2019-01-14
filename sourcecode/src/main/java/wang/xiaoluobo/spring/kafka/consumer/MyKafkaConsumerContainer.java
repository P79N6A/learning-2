package wang.xiaoluobo.spring.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangyd
 * @date 2017-07-03
 */
@Component
public class MyKafkaConsumerContainer implements DisposableBean {

    @Autowired
    private MyKafkaConfig myKafkaConfig;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * key: groupId
     * value: ConcurrentMessageListenerContainer
     */
    private ConcurrentHashMap<String, ConcurrentMessageListenerContainer> containers = new ConcurrentHashMap<>();


    private ConsumerFactory<String, Object> consumerFactory(String groupId) {
        return new DefaultKafkaConsumerFactory(consumerConfigs(groupId));
    }

    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, myKafkaConfig.bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, myKafkaConfig.isAutoCommit);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, myKafkaConfig.autoCommitIntervalMs);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, myKafkaConfig.sessionTimeoutMs);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, myKafkaConfig.consumerKeySerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, myKafkaConfig.consumerValueSerializer);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, myKafkaConfig.maxPollRecords);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, myKafkaConfig.requestTimeoutMs);
        return props;
    }

    private ContainerProperties containerProperties(String topic) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        MyKafkaConsumerListener remedyKafkaConsumerListener = new MyKafkaConsumerListener(kafkaTemplate, myKafkaConfig);
        containerProperties.setMessageListener(remedyKafkaConsumerListener);
        return containerProperties;
    }

    public void createContainer(String topic, String groupId){
        ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer(consumerFactory(groupId), containerProperties(topic));
        container.setConcurrency(myKafkaConfig.concurrencySize);
        container.getContainerProperties().setPollTimeout(myKafkaConfig.requestTimeoutMs);
        container.start();

        containers.put(myKafkaConfig.groupId, container);
    }


    public void stopAllContainer(){
        try {
            this.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopContainer(String groupId){
        ConcurrentMessageListenerContainer concurrentMessageListenerContainer = containers.get(groupId);
        if(concurrentMessageListenerContainer != null){
            concurrentMessageListenerContainer.stop();

            containers.remove(groupId);
        }
    }

    @Override
    public void destroy() throws Exception {
        Collection<ConcurrentMessageListenerContainer> cc = containers.values();
        if(cc != null && cc.size() > 0){
            Iterator<ConcurrentMessageListenerContainer> it = cc.iterator();
            ConcurrentMessageListenerContainer c = null;
            while (it.hasNext()){
                c = it.next();
                if(c != null){
                    c.stop();
                }
            }
        }
    }
}
