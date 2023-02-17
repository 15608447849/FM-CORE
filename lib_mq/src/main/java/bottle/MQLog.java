package bottle;

import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.log.PrintLogThread;
import bottle.util.StringUtil;

import java.util.Date;

import static bottle.log.PrintLogThread.sdfDict;

public class MQLog {
    public static void info(String message){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.info,message));
    }

    public static void debug(String message){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.debug,message));
    }
    public static void warn(String message){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.warn,message));
    }

    public static void activemq_write(String flag,String message){

        String time = PrintLogThread.sdf.format(new Date());
        String threadThreadName = Thread.currentThread().getName();
        String _message = String.format("【%s】【%s】\t%s\n",time,threadThreadName,message);

        String dict = "./logs/activemq/"+flag;
        String file = PrintLogThread.sdfFile.format(new Date());

        PrintLogThread.addMessageQueue(new LogBean(dict,file,_message));
    }

    public static void error(String desc,Throwable e){
        desc = desc == null ? "":desc ;
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.error,desc,e));
    }


}
