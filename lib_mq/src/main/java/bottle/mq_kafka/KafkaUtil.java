package bottle.mq_kafka;

import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import bottle.util.Log4j;

import java.util.*;

/**
 * @Author: leeping
 * @Date: 2020/8/28 18:16
 */
@PropertiesFilePath("/kafkamq.properties")
public class KafkaUtil {
    public static boolean isDebug = false;

    @PropertiesName("server.address")
    private static String serverAddr;

    @PropertiesName("bus.topic.list")
    private static String busTopics;

    private static final Map<String,String> topicMap = new HashMap<>();

    private static KProductionWarp production;

    static {
        ApplicationPropertiesBase.initStaticFields(KafkaUtil.class);
        try {
            if (serverAddr!=null && busTopics!=null){
                String[] strArr = busTopics.split(",");
                for (String it : strArr){
                    String[] arr = it.split(":");
                    String busKey = arr[0];
                    String useTopicName = arr[1];
                    topicMap.put(busKey,useTopicName);
                    Log4j.info("KAFKA TOPIC : "+ busKey+" -> "+ useTopicName);
                }
            }
        } catch (Exception e) {
            Log4j.error("kafka 消息发送工具无法初始化配置文件 kafkamq.properties",e);
        }
    }

    /** 业务主题标识, 返回值-是否成功调用生产者发送消息 */
    public static boolean asyncSendKFKMessage(String busTopicFlag,String k_msgType,String v_jsonStr,KFKProductionMessageCallback callback){
        String topicName = topicMap.get(busTopicFlag);
        if (topicName == null) {
            if (isDebug) Log4j.info("尝试匹配业务主题( "+ busTopicFlag +" )失败");
            return false;
        }
        if (v_jsonStr == null){
            if (isDebug) Log4j.info("请不要尝试发送空内容消息");
            return false;
        }

        // 双锁检测创建对象
        if (production == null){
            synchronized (topicMap){
                if (production == null){
                    try {
                        production = new KProductionWarp(serverAddr,true);
                    } catch (Exception e) {
                        if (isDebug) Log4j.error("不能创建kafka生产者",e);
                        return false;
                    }
                }
            }
        }

        production.sendMessageToTopicAsync(topicName,k_msgType,v_jsonStr,callback);
        return true;
    }


    private static List<KConsumerWarp> consumeList = new LinkedList<>();

    //加入消费者
    public static boolean addConsume(String groupName,String[] busTopics,KFKConsumeMessageCallback callback){
        try {
            if (callback == null || busTopics == null) {
                if (isDebug) Log4j.info("消费者处理对象不存在或未设置可关联的业务主题");
                return false;
            }

            Set<String> set = new HashSet<>();
            for (String busTopic : busTopics) {
                String name = topicMap.get(busTopic);
                if (name == null) {
                    if (isDebug) Log4j.info("尝试匹配业务主题( " + busTopic + " )失败");
                    return false;
                }
                set.add(name);
            }

            String[] topics  =set.toArray(new String[0]);
            KConsumerWarp c = new KConsumerWarp(serverAddr,groupName, callback,topics);
            consumeList.add(c);
            if (isDebug) Log4j.info("加入 kafka消费者( "+c+" ) 消息处理( "+callback+" ) 消费主题( "+ Arrays.toString(topics) +" )"  );
            return true;
        } catch (Exception e) {
            Log4j.info("无法加入消费者,"+e.getMessage());
        }
        return false;
    }

    public static void destroy(){
        if (production!=null) production.close();
        for (KConsumerWarp c : consumeList){
            c.close();
        }
    }

}
