package bottle.mq_kafka;


import bottle.MQLog;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

import static bottle.mq_kafka.KConfigBuild.genConsumerConfig;

/**
 * @Author: leeping
 * @Date: 2020/8/6 13:53
 */
public class KConsumerWarp extends Thread implements ConsumerRebalanceListener,KFKWarpI {

    private volatile boolean isRunning = true;

    //获取数据超时时间
    private final Duration TIMEOUT_MILLIS_POLL= Duration.ofMillis(10 * 1000);
    private final Duration TIMEOUT_MILLIS_NO_POLL = Duration.ofMillis(0);

    private final KafkaConsumer<String, String> consumer;

    private final KFKConsumeMessageCallback callback;

    private volatile ConsumerRecords<String, String> handlerRecords = null;

    private Collection<TopicPartition> handlerPartitions = null;

    private Map<TopicPartition, OffsetAndMetadata> retryOffset = null;

    //等待提交的偏移量
    private final SynchronousQueue<Map<TopicPartition, OffsetAndMetadata>> offsetQueue = new SynchronousQueue<>();

    public KConsumerWarp(String serverAddress, String groupId, KFKConsumeMessageCallback callback, String... topicNames) {
        if (callback==null) throw new IllegalArgumentException("没有消息处理实现类 KFKConsumeMessageCallback ");
        if (topicNames==null) throw new IllegalArgumentException("没有可以绑定的主题");
        this.consumer = new KafkaConsumer<>(callback.initBefore(genConsumerConfig(serverAddress,groupId)));



        this.callback = callback;

        // 绑定主题并设置监听
        this.consumer.subscribe(Arrays.asList(topicNames),this);

        final Thread handleRecordThread = new Thread(this::executeHandlerRecode);
        handleRecordThread.setName("KAFKA-CONSUMER-HANDLE"+handleRecordThread.getId());
        handleRecordThread.setDaemon(true);
        handleRecordThread.start();

        this.setName("KAFKA-CONSUMER-"+this.getId());
        this.setDaemon(true);
        this.start();
    }

    //异步处理消息
    private void executeHandlerRecode() {
        while (isRunning){
            try{
                if (handlerRecords == null){
                    synchronized (offsetQueue){
                        offsetQueue.wait();
                    }
                    continue;
                }
                boolean isCommit;
                // 提交偏移量记录
                Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

                for (ConsumerRecord<String, String> record : handlerRecords){
                    if (!isRunning) break;
                    TopicPartition partition = new TopicPartition(record.topic(),record.partition());
                    OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset()+1,"no metadata");
                    currentOffset.put(partition,offsetAndMetadata);
                    isCommit = callback.singe(record);
                    if (isCommit){
                        offsetQueue.put(currentOffset);
                    }
                }

                isCommit = callback.batch(handlerRecords);
                if (isRunning && isCommit){
                    offsetQueue.put(currentOffset);
                }

                handlerRecords = null;
            }catch (Exception e){
                MQLog.error("消费者处理线程异常",e);
            }
        }
    }

    private String getFlag(){
        return "KAFKA消费-("+this+"-Thread.currentThread().getName()"+")\t";
    }

    @Override
    public void run() {
        try {
            while (isRunning){
                try {
                    ConsumerRecords<String, String> records = consumer.poll( handlerRecords == null ?  TIMEOUT_MILLIS_POLL :  TIMEOUT_MILLIS_NO_POLL);
                    if (records.count() > 0) {
                        long sPoint = records.iterator().next().offset();
//                        MQLog.info(" KAFKA消费者>> 收到一批消息,大小: "+ records.count()+" 起点: "+ sPoint +" 终点: "+ ( sPoint+records.count() ) );
                        MQLog.debug(getFlag()+ " 收到消息,大小: "+ records.count()+" 起点: "+ sPoint +" 终点: "+ ( sPoint+records.count() ) );
                        Collection<TopicPartition> partitions = new HashSet<>();
                        if (handlerRecords == null){
                            //当前正在处理的分区
                            for (ConsumerRecord<String, String> record : records){
                                TopicPartition partition = new TopicPartition(record.topic(),record.partition());
                                partitions.add(partition);
                            }

                            consumer.pause(partitions);
                            MQLog.debug(getFlag()+" 暂停获取数据 分区 - "+partitions);
                            handlerPartitions = partitions;
                            handlerRecords = records;
                            synchronized (offsetQueue){
                                offsetQueue.notifyAll();
                            }
                        }else {
                            //异步处理正在工作中, 重新设置分区下标
                            for (ConsumerRecord<String, String> record : records){
                                TopicPartition partition = new TopicPartition(record.topic(),record.partition());
                                if ( partitions.add(partition)){
                                    OffsetAndMetadata _offsetAndMetadata = new OffsetAndMetadata(record.offset(),"no metadata");
                                    consumer.seek(partition,_offsetAndMetadata);
                                   MQLog.debug(getFlag()+" 复位 分区 - "+partition+" , 偏移量 - " + _offsetAndMetadata);
                                }
                            }
                        }
                    }

                    // 尝试提交错误的偏移量
                    if (retryOffset != null){
                        try {
                            consumer.commitSync(retryOffset);
                            retryOffset = null;
                        } catch (Exception e) {
                            MQLog.error(getFlag()+" 重试,提交偏移量异常: " + retryOffset , e);
                        }
                    }else{
                        //收集需要提交的偏移量
                        Map<TopicPartition, OffsetAndMetadata> currentOffset = offsetQueue.poll();
                        if (currentOffset!=null){
                            try {
                                consumer.commitSync(currentOffset);
//                                MQLog.info("提交偏移量完成: " + currentOffset);
                            } catch (Exception e) {
                                MQLog.error(getFlag()+" 提交偏移量异常: " + currentOffset , e);
                                retryOffset = currentOffset;
                            }
                        }
                    }
                    if (handlerRecords == null && handlerPartitions != null){
                        Collection<TopicPartition> partitions = handlerPartitions;
                        //释放分区 ,获取资源
                        consumer.resume(partitions);
//                        Log4j.info(" 恢复获取数据 分区 - "+handlerPartitions);
                        handlerPartitions = null;
                    }
                } catch (WakeupException e) {
                    //正常关闭中
//                    MQLog.info("kafka消费者正常关闭");
                }catch (Exception e){
                    MQLog.error(getFlag()+" 轮询异常",e);
                }
            }
        } finally {
            consumer.close();
        }
    }

    // 再均衡监听 1.重新分配分区之后  2.开始读取消息前
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

//        MQLog.debug(getFlag()+" onPartitionsAssigned >> 消费者 重新分配分区之后 or 开始读取消息前 :" + partitions);
        MQLog.debug(getFlag()+" [重新分配分区之后 or 开始读取消息前]" + partitions);

    }

    // 再均衡监听 1.再均衡开始前 2.停止读取消息后 , 在这里提交偏移量,下一个消费者将可以安全接管分区
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//        MQLog.info("onPartitionsRevoked >> 消费者 再均衡开始前 or 停止读取消息后 :" + partitions);
        MQLog.debug(getFlag()+" [再均衡开始前 or 停止读取消息后]" + partitions);

    }


    @Override
    public void close() {
        isRunning = false;
        synchronized (offsetQueue){
            offsetQueue.notifyAll();
        }
        consumer.wakeup();
    }


}
