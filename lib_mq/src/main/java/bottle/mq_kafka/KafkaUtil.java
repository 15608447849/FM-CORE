package bottle.mq_kafka;

import bottle.MQLog;
import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;


import java.util.*;

/**
 * @Author: leeping
 * @Date: 2020/8/28 18:16
 */
@PropertiesFilePath("/kafkamq.properties")
public class KafkaUtil {

    public static boolean isEnable = false;

    @PropertiesName("server.address")
    private static String serverAddr;

    @PropertiesName("bus.topic.list")
    private static String busTopics;

    private static final Map<String,String> topicMap = new HashMap<>();
    private static final List<KProductionWarp> productionList = new ArrayList<>();
    private static final List<KConsumerWarp> consumeList = new LinkedList<>();

    private static KProductionWarp defaulf_product;



    static {
        ApplicationPropertiesBase.initStaticFields(KafkaUtil.class);
        try {
            if (busTopics!=null){
                String[] strArr = busTopics.split(",");
                for (String it : strArr){
                    String[] arr = it.split(":");
                    String busKey = arr[0];
                    String useTopicName = arr[1];
                    topicMap.put(busKey,useTopicName);
                    MQLog.info("KAFKA TOPIC : "+ busKey+" ==> "+ useTopicName);
                }
                isEnable = true;
            }

            if (serverAddr!=null){
               defaulf_product = createKProduction(serverAddr,true);
            }
        } catch (Exception e) {
            MQLog.error("kafka 消息发送工具无法初始化配置文件 kafkamq.properties",e);
        }
    }


    public static KProductionWarp createKProduction(String bootstrap_server,boolean isSecurity){
        try {
            KProductionWarp kProductionWarp = new KProductionWarp(bootstrap_server, isSecurity);
            productionList.add(kProductionWarp);
            return kProductionWarp;
        } catch (Exception e) {
            MQLog.error("不能创建kafka生产者 "+ bootstrap_server,e);
        }
        return null;
    }

    /* 指定生产者进行发送 */
    public static void asyncSendKDKMessageSpecTopic(KProductionWarp product,String topicName,String k_msgType,String v_jsonStr,KFKProductionMessageCallback callback){
        if (product == null){
            MQLog.info("KAFKA PRODUCT IS NULL ,SEND FAIL : "+ topicName+" -> "+ k_msgType +" : "+ v_jsonStr );
            return;
        }
        if (product.isClose) return;
        product.sendMessageToTopicAsync(topicName,k_msgType,v_jsonStr,callback);
    }

    /** 业务主题标识, 返回值-是否成功调用生产者发送消息 */
    public static boolean asyncSendKFKMessage(String busTopicFlag,String k_msgType,String v_jsonStr,KFKProductionMessageCallback callback){
        if (!isEnable) return false;

        String topicName = topicMap.get(busTopicFlag);
        if (topicName == null) {
            MQLog.info("尝试匹配业务主题( "+ busTopicFlag +" )失败");
            return false;
        }
        if (v_jsonStr == null){
            MQLog.info("请不要尝试发送空内容消息");
            return false;
        }
        asyncSendKDKMessageSpecTopic(defaulf_product,topicName,k_msgType,v_jsonStr,callback);
        return true;
    }



    //加入消费者
    public static boolean addConsume(String groupName,String[] busTopics,KFKConsumeMessageCallback callback){
        if (!isEnable) return false;

        try {
            if (callback == null || busTopics == null) {
                MQLog.info("消费者处理对象不存在或未设置可关联的业务主题");
                return false;
            }

            Set<String> set = new HashSet<>();
            for (String busTopic : busTopics) {
                String name = topicMap.get(busTopic);
                if (name == null) {
                    MQLog.info("尝试匹配业务主题( " + busTopic + " )失败");
                    return false;
                }
                set.add(name);
            }

            String[] topics  =set.toArray(new String[0]);
            KConsumerWarp c = new KConsumerWarp(serverAddr,groupName, callback,topics);
            consumeList.add(c);
            MQLog.info("加入 kafka消费者( "+c+" ) 消息处理( "+callback+" ) 消费主题( "+ Arrays.toString(topics) +" )"  );
            return true;
        } catch (Exception e) {
            MQLog.error("无法加入消费者",e);
        }
        return false;
    }

    public static void destroy(){
        if (isEnable){
            for (KProductionWarp it : productionList) {
                it.close();
            }
            for (KConsumerWarp it : consumeList){
                it.close();
            }
            productionList.clear();
            consumeList.clear();
            isEnable = false;
             MQLog.info("KAFKA PRODUCT AND CONSUME ALL CLOSE"  );
        }

    }

}
