package bottle.mq_kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: leeping
 * @Date: 2020/8/28 11:33
 */
public class KConfigBuild {

    private KConfigBuild(){ }

    private static final AtomicInteger index  = new AtomicInteger(0);

    private static Map<String, Object> commProp(String bootstrap_servers){
        Map<String, Object> configs = new HashMap<>();

        configs.put("bootstrap.servers",bootstrap_servers);
        // 关闭闲置连接 毫秒
        configs.put("connections.max.idle.ms",5 * 60 * 1000);
        // 等待服务器响应的最大时间
        configs.put("request.timeout.ms",60 * 1000);
        configs.put("client.id","kfk-client-"+ index.getAndIncrement() +"-"+bootstrap_servers);
        // 日志拦截器
        configs.put("interceptor.classes", kCommLogInterceptor.class.getName());

        return configs;
    }


    /*
    生产者
     mode:
        0 - 快速模式
        1 - 安全模式
     */
    static Map<String, Object> genProductionConfig(String bootstrap_servers,int mode){
            Map<String, Object> configs = commProp(bootstrap_servers);
            String serializer = org.apache.kafka.common.serialization.StringSerializer.class.getName();
            configs.put("key.serializer",serializer);
            configs.put("value.serializer",serializer);

            // 服务端响应级别
            configs.put("acks",mode == 0 ? "1":"all");

            // 发送内存缓冲区大小
            configs.put("buffer.memory",5 * 1024 * 1024);

            //发送消息的最大值
            configs.put("max.request.size",5 * 1024 * 1024);

            // 控制 KafkaProducer.send() 和 KafkaProducer.partitionsFor() 将阻塞多长时间
            configs.put("max.block.ms",mode == 0 ? 30 * 1000 : 60 * 1000);

            // 压缩
            configs.put("compression.type","gzip");

            // 重试次数
            configs.put("retries",mode == 0 ? 1 : 3);

            // 重试保证顺序
            configs.put("max.in.flight.requests.per.connection",mode == 0 ? 10 : 1);

            // 生产者将确保每个消息正好一次复制写入到stream ,幂等
            configs.put("enable.idempotence",mode == 1);

            // 消息批量发送大小 以字节为单位
            configs.put("batch.size",mode == 0 ? 0 : 5 * 1024 * 1024);

            // 消息批次最多等待时间 毫秒
            configs.put("linger.ms", mode == 0 ? 0 : 10 * 1000);


            return configs;
    }

    /*
      消费者
      禁止手动提交
    */
    static Map<String,Object> genConsumerConfig(String bootstrap_servers,String group_id){
        Map<String, Object> configs = commProp(bootstrap_servers);
        String deserializer = org.apache.kafka.common.serialization.StringDeserializer.class.getName();
        configs.put("group.id",group_id);
        configs.put("key.deserializer",deserializer);
        configs.put("value.deserializer",deserializer);

        // 禁止自动提交 手动处理
        configs.put("enable.auto.commit",false);
        // 当Kafka中没有初始offset或如果当前的offset不存在时 处理方式
        configs.put("auto.offset.reset","earliest");
//        configs.put("auto.offset.reset","latest");

        //服务器拉取消息返回的最小数据量 字节
        configs.put("fetch.min.bytes",5 * 1024 * 1024);
        //如果没有足够的数据满足 最多等待多久返回有效数据 毫秒
        configs.put("fetch.max.wait.ms", 30 * 1000);

        // 指定服务器从每个分区内返回的消息最大字节数 必须与  服务器配置max.message.size 一致
        configs.put("max.partition.fetch.bytes", 5 * 1024 * 1024);

        // poll()每次调用间隔内,允许最大的消息处理时间
        configs.put("max.poll.interval.ms",60 * 1000);
        // 用于发现消费者故障的超时时间 消费者周期性的发送心跳到broker，表示其还活着 如果会话超时期满之前没有收到心跳，那么broker将从分组中移除消费者
        configs.put("session.timeout.ms",30 * 1000);
        // 心跳用于确保消费者的会话保持活动状态 该值必须必比session.timeout.ms小 ,在 broker配置范围内 group.min.session.timeout.ms(6000) ~  group.max.session.timeout.ms(300000)
        configs.put("heartbeat.interval.ms",10 * 1000);

        // 在单次调用poll()中返回的最大记录数
        configs.put("max.poll.records",Integer.MAX_VALUE);

        //消费者群组 分区分配策略
        configs.put("partition.assignment.strategy",org.apache.kafka.clients.consumer.RoundRobinAssignor.class.getName());

        return configs;
    }
}
