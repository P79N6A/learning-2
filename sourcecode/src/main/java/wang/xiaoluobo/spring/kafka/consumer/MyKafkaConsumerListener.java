package wang.xiaoluobo.spring.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Utils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;

/**
 * @author wangyd
 * @date 2017-06-01
 */
public class MyKafkaConsumerListener implements MessageListener<String, Object> {

    private KafkaTemplate kafkaTemplate;

    private MyKafkaConfig myKafkaConfig;


    public MyKafkaConsumerListener(KafkaTemplate kafkaTemplate, MyKafkaConfig myKafkaConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.myKafkaConfig = myKafkaConfig;
    }

    @Override
    public void onMessage(ConsumerRecord<String, Object> consumerRecord) {
        try {
            String topic = consumerRecord.topic();
            String key = consumerRecord.key();
            Object value = consumerRecord.value();
            System.out.println(String.format("receive kafka data [topic={},key={},value={}]:", topic, key, new String((byte[]) value)));

            // kafka写入数据分区算法，按此算法可以将同一key写入同一个分区
            byte[] serializedKey = new StringSerializer().serialize(topic, key);
            int partition = (Utils.murmur2(serializedKey) & 0x7fffffff) % myKafkaConfig.concurrencySize;
//            kafkaTemplate.send(myKafkaConfig.topicName, key, value);

        } catch (Exception e) {
            System.out.println("consume data failed.");
        }
    }
}
