package bottle.util;

import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.log.LogPrintI;
import bottle.log.PrintLogThread;
import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static bottle.log.PrintLogThread.addMessageQueue;

/**
 * @Author: leeping
 * @Date: 2019/8/16 17:01
 */
public class Log4j {

    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public final static SimpleDateFormat sdfDict = new SimpleDateFormat("yyyyMMdd");
    public final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HH");

    public static void trace(Object obj){
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

    public static void error(String message, Throwable t){
        addMessageQueue(new LogBean(LogLevel.error,message,t));
    }

    public static void warn(Object obj){
        addMessageQueue(new LogBean(LogLevel.error,obj));
    }

    public static void fatal(Object obj){
        addMessageQueue(new LogBean(LogLevel.fatal,obj));
    }

    /* 写入指定文件 */
    public static void writeLogToSpecFile(String dict,String file,String message){
        addMessageQueue(new LogBean(dict,file,message));
    }

    // log4j2
    private final static Logger logger = LogManager.getLogger();

    static {

        PrintLogThread.logPrintI = new LogPrintI() {
            @Override
            public void trace(Object message) {
                logger.trace(message);
            }

            @Override
            public void debug(Object message) {
                logger.debug(message);
            }

            @Override
            public void info(Object message) {
                logger.info(message);
            }

            @Override
            public void error(Object message, Throwable e) {
                logger.error(message,e);
            }

            @Override
            public void warn(Object message) {
                logger.warn(message);
            }

            @Override
            public void fatal(Object message) {
                logger.fatal(message);
            }
        };

        try {
            //异步日志
            System.setProperty("Log4jContextSelector",
                    "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
