package bottle.util;

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

/**
 * @Author: leeping
 * @Date: 2019/8/16 17:01
 */
public class Log4j {

    // http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20
    private static final String logo = "\n" +
            "                    __       __                                          ___                           __                 __       __       ___                  \n" +
            "                   /\\ \\__   /\\ \\                                        /\\_ \\                         /\\ \\               /\\ \\__   /\\ \\__   /\\_ \\                 \n" +
            "   __      __  __  \\ \\ ,_\\  \\ \\ \\___      ___    _ __   __              \\//\\ \\       __      __       \\ \\ \\____    ___   \\ \\ ,_\\  \\ \\ ,_\\  \\//\\ \\       __       \n" +
            " /'__`\\   /\\ \\/\\ \\  \\ \\ \\/   \\ \\  _ `\\   / __`\\ /\\`'__\\/\\_\\               \\ \\ \\    /'__`\\  /'__`\\      \\ \\ '__`\\  / __`\\  \\ \\ \\/   \\ \\ \\/    \\ \\ \\    /'__`\\     \n" +
            "/\\ \\L\\.\\_ \\ \\ \\_\\ \\  \\ \\ \\_   \\ \\ \\ \\ \\ /\\ \\L\\ \\\\ \\ \\/ \\/_/_               \\_\\ \\_ /\\  __/ /\\  __/  __   \\ \\ \\L\\ \\/\\ \\L\\ \\  \\ \\ \\_   \\ \\ \\_    \\_\\ \\_ /\\  __/     \n" +
            "\\ \\__/.\\_\\ \\ \\____/   \\ \\__\\   \\ \\_\\ \\_\\\\ \\____/ \\ \\_\\   /\\_\\              /\\____\\\\ \\____\\\\ \\____\\/\\_\\   \\ \\_,__/\\ \\____/   \\ \\__\\   \\ \\__\\   /\\____\\\\ \\____\\    \n" +
            " \\/__/\\/_/  \\/___/     \\/__/    \\/_/\\/_/ \\/___/   \\/_/   \\/_/              \\/____/ \\/____/ \\/____/\\/_/    \\/___/  \\/___/     \\/__/    \\/__/   \\/____/ \\/____/    \n" +
            " __               ___                          _      ______      ____       __        __      __ __       __ __       ________    __      __ __         __      \n" +
            "/\\ \\__           /\\_ \\                       /' \\    /\\  ___\\    /'___\\    /'__`\\    /'_ `\\   /\\ \\\\ \\     /\\ \\\\ \\     /\\_____  \\ /'_ `\\   /\\ \\\\ \\      /'_ `\\    \n" +
            "\\ \\ ,_\\     __   \\//\\ \\     __              /\\_, \\   \\ \\ \\__/   /\\ \\__/   /\\ \\/\\ \\  /\\ \\L\\ \\  \\ \\ \\\\ \\    \\ \\ \\\\ \\    \\/___//'/'/\\ \\L\\ \\  \\ \\ \\\\ \\    /\\ \\L\\ \\   \n" +
            " \\ \\ \\/   /'__`\\   \\ \\ \\   /\\_\\             \\/_/\\ \\   \\ \\___``\\ \\ \\  _``\\ \\ \\ \\ \\ \\ \\/_> _ <_  \\ \\ \\\\ \\_   \\ \\ \\\\ \\_      /' /' \\/_> _ <_  \\ \\ \\\\ \\_  \\ \\___, \\  \n" +
            "  \\ \\ \\_ /\\  __/    \\_\\ \\_ \\/_/_               \\ \\ \\   \\/\\ \\L\\ \\ \\ \\ \\L\\ \\ \\ \\ \\_\\ \\  /\\ \\L\\ \\  \\ \\__ ,__\\  \\ \\__ ,__\\  /' /'     /\\ \\L\\ \\  \\ \\__ ,__\\ \\/__,/\\ \\ \n" +
            "   \\ \\__\\\\ \\____\\   /\\____\\  /\\_\\               \\ \\_\\   \\ \\____/  \\ \\____/  \\ \\____/  \\ \\____/   \\/_/\\_\\_/   \\/_/\\_\\_/ /\\_/       \\ \\____/   \\/_/\\_\\_/      \\ \\_\\\n" +
            "    \\/__/ \\/____/   \\/____/  \\/_/                \\/_/    \\/___/    \\/___/    \\/___/    \\/___/       \\/_/        \\/_/   \\//         \\/___/       \\/_/         \\/_/\n" +
            "                                                                                                                                                                 \n" +
            "                                                                                                                                                                 \n";

    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sdfDict = new SimpleDateFormat("yyyyMMdd");
    public final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HH");

    // log4j2
    private final static Logger logger = LogManager.getLogger();

    public interface PrintCallback{
        void callback(Object message,Throwable throwable);
    }

    private static List<PrintCallback> callbackList = new ArrayList<>();

    public static void addCallback(PrintCallback callback) {
        callbackList.add(callback);
    }

    private enum LogLevel{
        trace,debug,info,error,warn,fatal,specifyFile
    }

    private static class LogBean{
        private final String directory;
        private final String file;
        private final LogLevel level;
        private final Object content;
        private final Throwable e;

        // 指定文件
        private LogBean(String directory,String file,String content) {
            this.directory = directory;
            this.file = file;
            this.level = LogLevel.specifyFile;
            this.content = content;
            e = null;
        }

        // 日志
        private LogBean(LogLevel level, Object content, Throwable e) {
            this.directory = null;
            this.file = null;
            this.level = level;
            this.content = content;
            this.e = e;
        }

        // 日志
        private LogBean(LogLevel level, Object content) {
            this.directory = null;
            this.file = null;
            this.level = level;
            this.content = content;
            this.e = null;
        }
    }

    // 对指定文件写入内容
    private static void appendContentToFile(String dictPath, String fileName, String content) {
        try {
            File dict = new File(dictPath);
            if (!dict.exists()){
                if (!dict.mkdirs()) return;
            }
            File file = new File(dict,fileName);
            try(FileOutputStream fos = new FileOutputStream(file, true);){
                try(FileChannel channel = fos.getChannel();){
                    byte[] data = content.getBytes(StandardCharsets.UTF_8);
                    ByteBuffer buf = ByteBuffer.wrap(data);
                    buf.put(data);
                    buf.flip();
                    channel.write(buf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static LinkedBlockingQueue<String[]> writeFieContentQueue = new LinkedBlockingQueue<>();

    private static void toWriteFileQueue(String directory, String file, String content) {
        writeFieContentQueue.offer(new String[]{directory,file,content});
    }

    private final static Thread handleWriteFieContentThread = new Thread(() -> {
        while (true){
            try {
                String[] arr = writeFieContentQueue.take();
                appendContentToFile(arr[0],arr[1],arr[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    // 日志消息队列
    private final static LinkedBlockingQueue<LogBean> messageQueue = new LinkedBlockingQueue<>();
    // 日志消息处理线程
    private static final Thread handleMessageQueueT =  new Thread(() -> {
        while (true){
            try {
                LogBean bean = messageQueue.take();
                if (bean.level == LogLevel.trace){
                    logger.trace(bean.content);
                }else if (bean.level == LogLevel.debug){
                    logger.debug(bean.content);
                }else if (bean.level == LogLevel.info){
                    logger.info(bean.content);
                }else if (bean.level == LogLevel.error){
                    logger.error(bean.content,bean.e);
                }else if (bean.level == LogLevel.warn){
                    logger.warn(bean.content);
                }else if (bean.level == LogLevel.fatal){
                    logger.fatal(bean.content);
                }else if (bean.level == LogLevel.specifyFile){
                    toWriteFileQueue(bean.directory,bean.file,String.valueOf(bean.content));
                }
                for (PrintCallback callback: callbackList){
                    callback.callback(bean.content,bean.e);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    });

    // 添加日志消息到队列
    private static void addMessageQueue(LogBean item){
        try {
            messageQueue.put(item);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    static {
        System.err.println(logo);
        try {
            //异步日志
            System.setProperty("Log4jContextSelector",
                    "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        } catch (Exception e) {
            e.printStackTrace();
        }

        handleWriteFieContentThread.setDaemon(true);
        handleWriteFieContentThread.setName("log-file-handle"+handleWriteFieContentThread.getId());
        handleWriteFieContentThread.start();

        handleMessageQueueT.setName("log-message-handle"+handleMessageQueueT.getId());
        handleMessageQueueT.setDaemon(true);
        handleMessageQueueT.start();
    }

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

}
