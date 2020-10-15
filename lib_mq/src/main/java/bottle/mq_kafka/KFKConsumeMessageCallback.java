package bottle.mq_kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Map;

/**
 * @Author: leeping
 * @Date: 2020/8/28 14:21
 */
public interface KFKConsumeMessageCallback {

    //开始创建执行者之前,进行必要的参数或独有参数设置
    default Map<String, Object> initBefore(Map<String, Object> map) {
        return map;
    }

    // 批量处理 返回值 : true 立即提交
    boolean batch(ConsumerRecords<String, String> records);

    // 逐条处理 返回值 true , 立即提交
    boolean singe(ConsumerRecord<String,String> record);

}
