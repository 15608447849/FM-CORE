package bottle.mq_kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * @Author: leeping
 * @Date: 2020/8/28 14:21
 */
public interface KFKConsumeMessageCallback {

    // 批量处理 返回值 : true 立即提交
    boolean batch(ConsumerRecords<String, String> records);

    // 逐条处理 返回值 true , 立即提交
    boolean singe(ConsumerRecord<String,String> record);

}
