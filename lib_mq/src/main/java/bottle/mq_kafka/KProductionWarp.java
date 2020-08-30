package bottle.mq_kafka;


import bottle.threadpool.IOThreadPool;
import bottle.util.Log4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.TimeoutException;


import static bottle.mq_kafka.KConfigBuild.genProductionConfig;

/**
 * @Author: leeping
 * @Date: 2020/8/6 13:50
 */
public class KProductionWarp implements KFKWarpI{

    //线程池
    private final IOThreadPool pool = new IOThreadPool();

    //kafka 客户端对象
    private final Producer<String, String> producer;

    /* kafka集群地址 , 是否保证消息有序且不丢失 */
    public KProductionWarp(String serverAddress,boolean isSecurity) {
        this.producer = new KafkaProducer<>(genProductionConfig(serverAddress,isSecurity?1:0));
    }

    // 同步发送
    public void sendMessageToTopicSync(final String topicName,final String messageType,final String jsonContent,KFKProductionMessageCallback callback){
        producer.send(new ProducerRecord<>(topicName, messageType,jsonContent), (metadata, exception) -> {
            if (exception!=null){

                //如果错误, 重试
                if (exception instanceof SerializationException){
                    // 序列化错误
                    Log4j.info("序列化错误\ntopic: "+ topicName+" , messageType: "+messageType+"\n"+jsonContent);
                }
                if (exception instanceof BufferExhaustedException ){
                    //缓冲区已满
                    Log4j.info("缓冲区已满\ntopic: "+ topicName+" , messageType: "+messageType+"\n"+jsonContent);
                }
                if ( exception instanceof TimeoutException){
                    //超时异常
                    Log4j.info("超时\ntopic: "+ topicName+" , messageType: "+messageType+"\n"+jsonContent);
                }
                if (exception instanceof InterruptedException){
                    //发送线程中断异常
                    Log4j.info("发送线程终端\ntopic: "+ topicName+" , messageType: "+messageType+"\n"+jsonContent);
                }
                if (callback!=null){
                    boolean  isRetry = callback.fail(topicName,messageType,jsonContent,exception);
                    if (isRetry){
                        sendMessageToTopicAsync(topicName,messageType,jsonContent,callback);
                    }
                }
            }else {
//                Log4j.info("发送成功: "+ metadata.partition()+" , "+ metadata.offset()+" \t"+jsonContent );
                if (callback!=null){
                    callback.success(metadata.topic(),metadata.partition(),metadata.offset(),messageType,jsonContent);
                }
            }
        });
    }

    public void sendMessageToTopicAsync(final String topicName,final String messageType,final String jsonContent,KFKProductionMessageCallback callback) {
        pool.post(() -> {
            sendMessageToTopicSync(topicName,messageType,jsonContent,callback);
        });
    }

    @Override
    public void close(){
        pool.close();
        producer.close();
    }
}
