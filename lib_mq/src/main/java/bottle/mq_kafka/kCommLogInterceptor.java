package bottle.mq_kafka;

import bottle.util.Log4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: leeping
 * @Date: 2020/8/28 14:16
 */ // 生产者发送消息 日志拦截器
public class kCommLogInterceptor implements ProducerInterceptor<String, String>, ConsumerInterceptor<String, String> {

    @Override
    public void configure(Map<String, ?> configs) { }

    // 生产者 准备发送
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        if (KafkaUtil.isDebug) Log4j.debug("准备发送\n\t" + record);
        return record;
    }

    // 生产者 发送结果
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
//        if(isPrint) Log4j.debug("ProducerInterceptor onAcknowledgement:\t主题:" + metadata.topic()+" ,分区:"+ metadata.partition()+" ,偏移量:"+ metadata.offset());
        if (exception != null) {
            if (KafkaUtil.isDebug) Log4j.error("ProducerInterceptor onAcknowledgement exception", exception);
        }
    }

    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        if (KafkaUtil.isDebug) Log4j.debug("可消费消息条数\t" + records.count());
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        if (KafkaUtil.isDebug) Log4j.debug("提交消息偏移\n\t" + offsets);
    }

    @Override
    public void close() { }
}
