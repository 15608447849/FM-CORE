package mytest;

import bottle.mq_kafka.KFKProductionMessageCallback;
import bottle.mq_kafka.KafkaUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import jdbc.imp.TomcatJDBC;
import jdbc.imp.TomcatJDBCDAO;
import jdbc.imp.TomcatJDBCTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: leeping
 * @Date: 2020/9/25 17:11
 */
public class ErpOperateLogWrite {
    /* 具体的UI组件及字面值 */
    private static class UIComponent{
        String uiName;
        String valueNew;
        String valueOld;
        String operateCustomContent;
    }
    /* 一次具体操作 */
    private static class Operate {
        long oid;
        //页面业务标识名
        String businessModel;
        //新增信息-0;修改信息-1;移除信息-2;自定义内容-3
        int operateType;
        //操作人
        String operator;
        //操作时间
        String time;
        //具体操作内容-组合
        String operatorContent;
        //UI组件集合
        List<UIComponent> componentList;
    }
    // 发送日志到kafka
    private static void sendContentToKafka(Operate operate) {
        Object[] params = new Object[]{operate.oid,operate.operator,operate.time,operate.businessModel,operate.operateType,operate.operatorContent};
        KafkaUtil.asyncSendKFKMessage("log", "OPERATE_LOG", GoogleGsonUtil.javaBeanToJson(params), new KFKProductionMessageCallback() {
            @Override
            public void success(String topic, int partition, long offset, String messageType, String jsonContent) {
                Log4j.info("用户日志操作发送到kafka 存入分区 "+partition+" , 偏移量 "+offset);
            }
            @Override
            public boolean fail(String topicName, String messageType, String jsonContent, Exception exception) {
                if (exception!=null)  Log4j.error("用户日志操作无法发送到kafka",exception);
                return true;
            }
        });
    }

    // ui元素 组装成内容
    private static void createContent(Operate op) {
//        Log4j.info("日志: " + GoogleGsonUtil.javaBeanToJson(op));
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(op.operateType == 0? "创建数据 ":"修改数据").append("】\t");
        for (UIComponent it : op.componentList){
            if (StringUtil.isEmpty(it.uiName)) continue;
            StringBuilder tsb = new StringBuilder();
            if (op.operateType == 3){
                //自定义内容
                tsb.append(it.operateCustomContent);
            }else{
                if (!StringUtil.isEmpty(it.valueOld)) {
                    //修改数据
                    tsb.append(it.valueNew).append("→").append(StringUtil.isEmpty(it.valueNew)?" ":it.valueNew);
                }else if (!StringUtil.isEmpty(it.valueNew)){
                    //新增数据
                    tsb.append(it.valueNew);
                }
            }
            if (tsb.length()>0){
                sb.append("●").append(it.uiName).append("∶").append(tsb.toString());
            }
            sb.append("；");
        }
        op.operatorContent = sb.deleteCharAt(sb.length() - 1).toString();
    }
    public static void main(String[] args) throws Exception {

    }
}
