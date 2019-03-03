package wang.xiaoluobo.rocketmq;

import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/3/17 16:34
 */
public class MyProducer implements InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(MyProducer.class);

    private DefaultMQProducer defaultMQProducer;
    private String producerGroup = "MyProducerGroup";
    private String namesrvAddr = "127.0.0.1:9876";


    /**
     * 向mq发送消息内容
     */
    public boolean sendMsg2MQ(String topic, String tags, String message) {
        if (StringUtils.isBlank(message)) {
            logger.debug("message is null, do not send.");
            return false;
        }

        logger.debug("message is topic({})-->tags({})-->message({}).", topic, tags, message);
        Message msg = new Message(topic, tags, message.getBytes());
        SendResult sendResult = null;

        try {
            sendResult = this.defaultMQProducer.send(msg);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (sendResult != null) {
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 参数信息
        logger.info("DefaultMQProducer initialize!");
        logger.info(producerGroup);
        logger.info(namesrvAddr);

        // 初始化
        defaultMQProducer = new DefaultMQProducer(producerGroup);
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        defaultMQProducer.setInstanceName(String.valueOf(System.currentTimeMillis()));
        defaultMQProducer.setVipChannelEnabled(false);

        defaultMQProducer.start();
        logger.info("DefaultMQProudcer start success!");
    }

    @Override
    public void destroy() {
        defaultMQProducer.shutdown();
    }

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }
}
