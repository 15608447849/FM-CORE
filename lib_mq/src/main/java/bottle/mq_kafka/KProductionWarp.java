package bottle.mq_kafka;


import bottle.MQLog;
import bottle.log.PrintLogThread;
import bottle.threadpool.IOThreadPool;
import bottle.util.StringUtil;
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
    private static final IOThreadPool pool = new IOThreadPool();

    //kafka 客户端对象
    private final Producer<String, String> producer;

    protected boolean isClose = false;

    /* kafka集群地址 , 是否保证消息有序且不丢失 */
    public KProductionWarp(String serverAddress,boolean isSecurity) {
        this.producer = new KafkaProducer<>(genProductionConfig(serverAddress,isSecurity?1:0));

    }


    // 同步发送
    public void sendMessageToTopicSync(final String topicName,final String messageType,final String jsonContent,KFKProductionMessageCallback callback){
        if (jsonContent.getBytes().length >  5 * 1024 * 1024 ){
            MQLog.warn("KAFKA-PRODUCT-数据内容超过5M大小限制 json="+jsonContent);
            return;
        }

        producer.send(new ProducerRecord<>(topicName, messageType,jsonContent), (metadata, exception) -> {
            if (exception!=null){
                //发送失败
                MQLog.warn("KAFKA-PRODUCT-发送失败\ttopic="+ topicName+" messageType="+messageType+" json="+jsonContent +
                        "\n" + StringUtil.printExceptInfo(exception) );

                if (exception instanceof SerializationException){
                    // 序列化错误
                    return;
                }
                if (exception instanceof BufferExhaustedException ){
                    //缓冲区已满
                    return;
                }
                if ( exception instanceof TimeoutException){
                    //超时异常
                    return;
                }
                if (exception instanceof InterruptedException){
                    //发送线程中断异常
                    return;
                }

                // 尝试重试
                if (callback!=null){
                    boolean isRetry = callback.fail(topicName,messageType,jsonContent,exception);
                    if (isRetry){
                        //MQLog.warn("KAFKA-PRODUCT-重新发送\ttopic="+ topicName+" messageType="+messageType+" json="+jsonContent);
                        sendMessageToTopicAsync(topicName,messageType,jsonContent,callback);

                    }
                }


            }else {
                // 发送成功
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
        isClose = true;
    }
}
