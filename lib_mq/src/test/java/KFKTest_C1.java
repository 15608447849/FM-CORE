import bottle.mq_kafka.KConsumerWarp;
import bottle.mq_kafka.KFKConsumeMessageCallback;

import bottle.mq_kafka.KafkaUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2020/8/6 14:58
 */
public class KFKTest_C1 {

    public static void main(String[] args) throws Exception {

        HashSet<Long> set = new HashSet<>();

        KafkaUtil.addConsume("测试组1",new String[]{ "log"},new KFKConsumeMessageCallback() {
            @Override
            public boolean batch(ConsumerRecords<String, String> records) {

                for (ConsumerRecord<String, String> r : records){
                    System.out.println(r.offset()+" "+r.key()+" "+r.value());
                }

                return true;
            }

            @Override
            public boolean singe(ConsumerRecord<String, String> record) {
                if (!set.add(record.offset())){
                    Log4j.info("处理消息重复 : "+ record.offset());
                }

//                return true
                return false;
            }
        });

       Thread.sleep(60 * 60 * 1000);
    }
}
