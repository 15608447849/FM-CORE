import bottle.mq_kafka.KFKProductionMessageCallback;
import bottle.mq_kafka.KProductionWarp;
import bottle.mq_kafka.KafkaUtil;
import bottle.util.TimeTool;
import java.util.Date;


/**
 * @Author: leeping
 * @Date: 2020/8/6 14:37
 */
public class KFKTest_P {
    public static void main(String[] args) throws Exception{
        while (true){
            loopSend();
            Thread.sleep(30 * 1000);
        }

    }

    private static void loopSend() {
        //发送消息
        for (int i = 0;i<100000;i++ ){
            KafkaUtil.asyncSendKFKMessage("log", "PING-TEST-NOW", TimeTool.date_yMd_Hms_2Str(new Date()) + " 测试消息,标识-" + i, new KFKProductionMessageCallback() {
                @Override
                public void success(String topic, int partition, long offset, String messageType, String jsonContent) {
                    System.out.println(" 发送成功 " + partition +" " + offset);
                }

                @Override
                public boolean fail(String topicName, String messageType, String jsonContent, Exception exception) {
                    System.out.println(" 发送失败 " + jsonContent);
                    if (exception!=null)  exception.printStackTrace();
                    return false;
                }
            });
        }

    }
}
