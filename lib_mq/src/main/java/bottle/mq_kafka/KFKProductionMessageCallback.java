package bottle.mq_kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * @Author: leeping
 * @Date: 2020/8/28 14:21
 */
public interface KFKProductionMessageCallback {

    //发送成功
    void success(String topic, int partition, long offset, String messageType, String jsonContent);
    //发送失败
    boolean fail(String topicName, String messageType, String jsonContent, Exception exception);

}
