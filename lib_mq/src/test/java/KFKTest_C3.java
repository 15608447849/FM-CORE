import bottle.mq_kafka.KFKConsumeMessageCallback;
import bottle.mq_kafka.KafkaUtil;
import bottle.util.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.HashSet;

/**
 * @Author: leeping
 * @Date: 2020/8/6 14:58
 */
public class KFKTest_C3 {

    public static void main(String[] args) throws Exception {

        HashSet<Long> set = new HashSet<>();

        KafkaUtil.addConsume("测试组3",new String[]{ "log"},new KFKConsumeMessageCallback() {
            @Override
            public boolean batch(ConsumerRecords<String, String> records) {
                return true;
            }

            @Override
            public boolean singe(ConsumerRecord<String, String> record) {
                if (!set.add(record.offset())){
                    Log4j.info("处理消息重复 : "+ record.offset());
                }
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return true
                return false;
            }
        });

       Thread.sleep(60 * 60 * 1000);
    }
}
