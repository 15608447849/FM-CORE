package jdbc.define.log;

import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.log.PrintLogThread;
import bottle.util.StringUtil;
import java.util.Date;

/**
 * @Author: leeping
 * @Date: 2019/8/16 11:04
 */
public final class JDBCLogger {

    private static final ThreadLocal<Throwable> errors = new ThreadLocal<>();

    private JDBCLogger(){}

    public static void print(String message){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.info,message));
    }

    private static String timeRang(long elapsedTmeMs) {
        // 5~10秒内
        if (elapsedTmeMs>=5*1000L && elapsedTmeMs<10*1000L){
            return "sec_5To10";
        }
        // 10~30秒内
        if (elapsedTmeMs>=10*1000L && elapsedTmeMs<30*1000L){
            return "sec_10To30";
        }
        // 30~60秒内
        if (elapsedTmeMs>30*1000L && elapsedTmeMs<60*1000L){
            return "sec_30To60";
        }
        // 大于一分钟
        return "more";

    }

    public static void writeSlowQuery(String sql,long connTime,long exeTime,long readTime){
        long elapsedTmeMs = connTime + exeTime + readTime ;
        if (elapsedTmeMs < 5*1000L) return;
        String time = PrintLogThread.sdf.format(new Date());
        String _message = String.format("【%s】\t%s\n\tconnect time: %d ms, execute time: %d ms, read time: %d ms\n",time,sql,connTime,exeTime,readTime);
        String dict = "./logs/jdbc/slow/";
        String file =  timeRang(elapsedTmeMs);
        PrintLogThread.addMessageQueue(new LogBean(dict,file,_message).setEnableCallback(false));//写入错误文件
    }



    private static void writeJDBCFile(String message){
        String time = PrintLogThread.sdf.format(new Date());
        String _message = String.format("【%s】\t%s\n",time,message);
        String dict = "./logs/jdbc/"+ PrintLogThread.sdfDict.format(new Date());
        String file = PrintLogThread.sdfFile.format(new Date());
        PrintLogThread.addMessageQueue(new LogBean(dict,file,_message).setEnableCallback(false));//写入错误文件
    }

    public static void error(String desc,Throwable e){
        errors.set(e);
        desc = desc == null ? "":desc ;
        writeJDBCFile("desc = "+desc+",\n"+StringUtil.printExceptInfo(e));
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.error,desc,e).setEnableCallback(false));//写入log日志
    }

    public static Throwable currentThreadJdbcError(){
        return errors.get();
    }
}
