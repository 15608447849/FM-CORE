package drug.erp.commom;

import bottle.log.PrintCallback;
import bottle.mq_kafka.KFKProductionMessageCallback;
import bottle.mq_kafka.KafkaUtil;
import bottle.util.Log4j;

import static bottle.log.PrintLogThread.addCallback;
import static bottle.util.StringUtil.printExceptInfo;

public class PrintLogCollect {

    /* 发送打印日志到kafka */
    private final static PrintCallback callback = (message, throwable) -> {
        StringBuilder stringBuilder = new StringBuilder();
        if (message!=null){
            stringBuilder.append(message);
        }
        if (throwable != null){
            stringBuilder.append("\n").append(printExceptInfo(throwable));
        }
        if (stringBuilder.length()>0){
            KafkaUtil.asyncSendKFKMessage("log", "PRINT_LOG", stringBuilder.toString(),null);
        }
    };

    public static void load(){

    }

    static {
        System.out.println("****************************************** 启动打印日志收集器 ******************************************");
        addCallback(callback);
    }
}
