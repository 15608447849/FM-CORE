package drug.erp.commom;

import bottle.mq_kafka.KFKProductionMessageCallback;
import bottle.mq_kafka.KafkaUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import bottle.util.TimeTool;
import com.alibaba.excel.util.StringUtils;
import jdbc.imp.GenUniqueID;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OperateLogUtil {
    /* 具体的UI组件及字面值 */
    private static class UIComponent{
        //UI元素组件名
        private String uiName;
        //UI元素组件 展示值 新
        private String valueNew;
        //UI元素组件 展示值 旧
        private String valueOld;
        //自定义操作内容
        private String operateCustomContent;
    }

    /* 一次具体操作 */
    private static class Operate {
        //数据标识
        private long oid;
        //页面业务标识名
        private String businessModel;
        //新增信息-0;修改信息-1;移除信息-2;自定义内容-3
        private int operateType = -1;
        //操作人
        private String operator;
        //操作时间 YYYY-MM-dd hh:mm:ss
        private String time;
        //具体操作内容-组合
        private String operatorContent;
        //UI组件集合
        private List<UIComponent> componentList = new ArrayList<>();
    }

    // ui元素 组装成内容
    private static void createContent(Operate op) {
        if (op.operateType == 3) return;

        StringBuilder sb = new StringBuilder();
        sb.append("【").append(op.operateType == 0? "创建数据 ":"修改数据").append("】\t");

        for (UIComponent it : op.componentList){
            if (StringUtils.isEmpty(it.uiName)) continue;

            StringBuilder tsb = new StringBuilder();
            if (op.operateType == 3){
                //自定义内容
                tsb.append(it.operateCustomContent);
            }else{
                if (!StringUtils.isEmpty(it.valueOld)) {
                    //修改数据
                    tsb.append(it.valueNew).append("→").append(StringUtils.isEmpty(it.valueNew)?" ":it.valueNew);
                }else if (!StringUtils.isEmpty(it.valueNew)){
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

    // 检查操作对象是否异常数据
    private static boolean checkOperate(Operate operate) {
        if (operate == null) return false;
        if (StringUtils.isEmpty(operate.businessModel)) return false;

        if (operate.operateType == 3){
            return !StringUtils.isEmpty(operate.operatorContent);
        }else{
            return operate.componentList != null && operate.componentList.size() != 0;
        }

    }

    // 发送日志到kafka
    private static void sendContentToKafka(Operate operate) {
        Object[] params = new Object[]{operate.oid,operate.operator,operate.time,operate.businessModel,operate.operateType,operate.operatorContent};
        KafkaUtil.asyncSendKFKMessage("log", "OPERATE_LOG", GoogleGsonUtil.javaBeanToJson(params), new KFKProductionMessageCallback() {
            @Override
            public void success(String topic, int partition, long offset, String messageType, String jsonContent) {
//                System.out.println("KAFKA 发送成功: " + jsonContent);
            }

            @Override
            public boolean fail(String topicName, String messageType, String jsonContent, Exception exception) {
                if (exception!=null)  Log4j.error("操作日志无法发送到kafka",exception);
                return true;
            }
        });
    }


    private static void recode(Operate operate){
        if (!checkOperate(operate)) return;
        if (operate.oid == 0) operate.oid = GenUniqueID.milliSecondID.currentTimestampLong();
        if (operate.operator == null) operate.operator = "未登录系统人员";
        if (operate.time == null) operate.time = TimeTool.date_yMd_Hms_2Str(new Date());

        createContent(operate);
        sendContentToKafka(operate);
    }


    public static void recodeByJsonFormat(String operator, String json){
        Operate operate = GoogleGsonUtil.jsonToJavaBean(json,Operate.class);
        if (operate == null) return;
        if (operator!=null) operate.operator = operator;
        recode(operate);
    }

    /** 操作人,操作时间,日志模块,操作内容 */
    public static void recodeByCustomFormat(String operator,String businessModel,String content){
        if (content == null) return;
        Operate operate = new Operate();
        operate.operateType = 3;
        operate.operator = operator;
        operate.businessModel = businessModel;
        operate.operatorContent = content;
        recode(operate);
    }

    /** 电商平台,用于记录 用户的操作
     *  例1:
     *  平台用户 15608447849 注册门店 平平大药房 完成后, 生成门店码 53600000, 调用:
     *      OperateLogUtil.recodeByDrugLog("15608447849",53600000,"平平大药房","注册成功")

     *  例2:
     *  平台用户 15608447849 修改账号 调用:
     *      OperateLogUtil.recodeByDrugLog("15608447849",53600000,"平平大药房","修改账号,15608447849 -> 13873140557")

     *  例4:
     *  平台用户 15608447849 提交订单 调用:
     *      OperateLogUtil.recodeByDrugLog("15608447849",53600000,"平平大药房","提交订单,单号:100xxxxxx,订单金额:50RMB...")

     * */
    public static void recodeByDrugLog(String account,int companyID,String companyName,String actionLog){
        if (actionLog == null) return;
        String operator = "门店( " + account + "-" + companyID + "-" + companyName + " )";
        String businessModel = "一块医药商城";
        recodeByCustomFormat(operator,businessModel,actionLog);
    }

}
