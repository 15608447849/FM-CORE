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

    private JDBCLogger(){}

    public static void print(String message){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.info,message));
    }


    private static void writeJDBCFile(String message){
        String time = PrintLogThread.sdf.format(new Date());
        String _message = String.format("【%s】\t%s\n",time,message);
        String dict = "./logs/jdbc/"+ PrintLogThread.sdfDict.format(new Date());
        String file = PrintLogThread.sdfFile.format(new Date());
        PrintLogThread.addMessageQueue(new LogBean(dict,file,_message));//写入错误文件
    }

    public static void error(String desc,Throwable e){
        desc = desc == null ? "":desc ;
        writeJDBCFile("desc = "+desc+",\n"+StringUtil.printExceptInfo(e));
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.error,desc,e));//写入log日志
    }
}
