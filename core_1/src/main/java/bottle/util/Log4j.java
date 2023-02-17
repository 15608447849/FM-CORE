package bottle.util;

import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.log.PrintLogThread;
import bottle.log.logimp.Log4j2Execute;
import java.text.SimpleDateFormat;
import java.util.Date;

import static bottle.log.PrintLogThread.addMessageQueue;

/**
 * @Author: leeping
 * @Date: 2019/8/16 17:01
 */
public class Log4j {
    public final static SimpleDateFormat sdf = PrintLogThread.sdf;
    public final static SimpleDateFormat sdfDict = PrintLogThread.sdfDict;
    public final static SimpleDateFormat sdfFile = PrintLogThread.sdfFile;

    /* 写入指定文件 */
    public static void writeLogToSpecFile(String dict,String file,String message){
        addMessageQueue(new LogBean(dict,file,message));
    }

    // 因业务关系,特别处理
    public static void trace(Object obj){
        String str = String.valueOf(obj);
        String time = Log4j.sdf.format(new Date());
        String _message = String.format("%s\t%s\n",time,str);
        Log4j.writeLogToSpecFile("./logs/trace/", Log4j.sdfDict.format(new Date()), _message);
        _trace(obj);
    }
    public static void _trace(Object obj){
        addMessageQueue(new LogBean(LogLevel.trace,obj));
    }
    public static void debug(Object obj){
        addMessageQueue(new LogBean(LogLevel.debug,obj));
    }
    public static void info(Object obj){
        addMessageQueue(new LogBean(LogLevel.info,obj));
    }
    public static void info(String... str){
        if(str==null) return;
        StringBuilder sb = new StringBuilder();
        for (String s : str){
            sb.append(s).append(" ");
        }
        addMessageQueue(new LogBean(LogLevel.info,sb.toString()));
    }
    public static void error(Throwable t){ addMessageQueue(new LogBean(LogLevel.error,"",t)); }
    public static void error(String message, Throwable t){ addMessageQueue(new LogBean(LogLevel.error,message,t)); }
    public static void warn(Object obj){
        addMessageQueue(new LogBean(LogLevel.warn,obj));
    }
    public static void fatal(Object obj){
        addMessageQueue(new LogBean(LogLevel.fatal,obj));
    }

    static {
        PrintLogThread.setPrint(new Log4j2Execute());
    }

}
