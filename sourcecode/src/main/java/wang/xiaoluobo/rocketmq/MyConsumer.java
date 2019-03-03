package wang.xiaoluobo.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/3/17 16:34
 */
public class MyConsumer implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(MyConsumer.class);

    private DefaultMQPushConsumer defaultMQPushConsumer;
    private String consumerGroup = "MyConsumerGroup";
    private String namesrvAddr = "127.0.0.1:9876";


    /**
     * 主题订阅
     * key主题与value子主题expression(以||分隔)关系hashmap
     */
    private static Map<String, String> scriptionsMap = new HashMap<String, String>();

    static {
        scriptionsMap.put("MyTopic", "MyTag");
        scriptionsMap.put("MyTopic", "myTag1");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 参数信息
        logger.info("DefaultMQPushConsumer initialize!");
        logger.info(consumerGroup);
        logger.info(namesrvAddr);

        // 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
        // 注意：ConsumerGroupName需要由应用来保证唯一
        defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQPushConsumer.setInstanceName(String.valueOf(System.currentTimeMillis()));
        defaultMQPushConsumer.setVipChannelEnabled(false);

        // 订阅指定MyTopic下tags等于MyTag
//        defaultMQPushConsumer.subscribe("MyTopic", "MyTag");

        if (null != scriptionsMap) {
            defaultMQPushConsumer.setSubscription(scriptionsMap);
        }

        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 设置为集群消费(区别于广播消费)
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);

        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            // 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                MessageExt msg = msgs.get(0);
                System.out.println("msg-->" + new String(msg.getBody()));

                if (msg.getTopic().equals("MyTopic")) {
                    // TODO 执行Topic的消费逻辑
                    if (msg.getTags() != null && msg.getTags().equals("MyTag")) {
                        // TODO 执行Tag的消费
                    }
                }
                // 如果没有return success ，consumer会重新消费该消息，直到return success
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // Consumer对象在使用之前必须要调用start初始化，初始化一次即可
        defaultMQPushConsumer.start();
        logger.info("DefaultMQPushConsumer start success!");
    }

    @Override
    public void destroy() {
        defaultMQPushConsumer.shutdown();
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setScriptionsMap(Map<String, String> scriptionsMap) {
        this.scriptionsMap = scriptionsMap;
    }

}
