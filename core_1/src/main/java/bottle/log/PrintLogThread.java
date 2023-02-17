package bottle.log;

import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static bottle.log.WriteLogThread.addWriteFileQueue;

public class PrintLogThread{

    // http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20
    private static final String logo =              "\n"+
                    "  ______                        \n" +
                    " |  ____|                       \n" +
                    " | |__ ___ _ __   __ _  ___ ___ \n" +
                    " |  __/ __| '_ \\ / _` |/ __/ _ \\\n" +
                    " | |  \\__ \\ |_) | (_| | (_|  __/\n" +
                    " |_|  |___/ .__/ \\__,_|\\___\\___|\n" +
                    "          | |                   \n" +
                    "          |_|                   ";


    /* 年月日时分秒 */
    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /* 年月日 */
    public final static SimpleDateFormat sdfDict = new SimpleDateFormat("yyyyMMdd");
    /* 年月日时 */
    public final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HH");

    /* 打印回调函数 */
    private static final List<PrintCallback> callbackList = new ArrayList<>();

    public static synchronized void addCallback(PrintCallback callback) {
        callbackList.add(callback);
    }

    // 默认实现
    private static LogPrintI logPrintI = new LogPrintI() {
        private final org.slf4j.Logger logger = LoggerFactory.getLogger(PrintLogThread.class);

        @Override
        public void trace(Object message) {
            logger.trace(String.valueOf(message));
        }

        @Override
        public void debug(Object message) {
            logger.debug(String.valueOf(message));
        }

        @Override
        public void info(Object message) { logger.info(String.valueOf(message)); }

        @Override
        public void error(Throwable e) {
            logger.error("",e);
        }

        @Override
        public void error(Object message, Throwable e) {
            logger.error(String.valueOf(message),e);
        }

        @Override
        public void warn(Object message) {
            logger.warn(String.valueOf(message));
        }

        @Override
        public void fatal(Object message) {
            logger.warn("fatal> "+ message);
        }
    };


    // 日志消息队列
    private final static LinkedBlockingQueue<LogBean> messageQueue = new LinkedBlockingQueue<>();

    public static void setPrint(LogPrintI execute) {
        logPrintI = execute;
    }

    private static final Thread thread = new Thread(){
        @Override
        public void run() {
            while (true){
                try {
                    LogBean bean = messageQueue.take();
                    if (bean.level == LogLevel.specifyFile){
                        addWriteFileQueue(bean.directory,bean.file,
                                bean.threadName+"\t"+bean.content);
                    }
                    for (PrintCallback callback: callbackList){
                        try {
                            callback.callback( bean );
                        } catch (Exception e) {
                            if (logPrintI!=null)
                                logPrintI.error(e);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    };


    // 添加日志消息到队列
    public static void addMessageQueue(LogBean bean){
        try {
            messageQueue.put(bean);

            if (logPrintI!=null) {
                if (bean.level == LogLevel.trace) {
                    logPrintI.trace(bean.threadName+"\t"+bean.content);
                } else if (bean.level == LogLevel.debug) {
                    logPrintI.debug(bean.threadName+"\t"+bean.content);
                } else if (bean.level == LogLevel.info) {
                    logPrintI.info(bean.threadName+"\t"+bean.content);
                } else if (bean.level == LogLevel.error) {
                    logPrintI.error(bean.threadName+"\t"+bean.content, bean.e);
                } else if (bean.level == LogLevel.warn) {
                    logPrintI.warn(bean.threadName+"\t"+bean.content);
                } else if (bean.level == LogLevel.fatal) {
                    logPrintI.fatal(bean.threadName+"\t"+bean.content);
                }
            }

        } catch (Exception ignored) {}

    }


    static {
        System.err.println(logo);
        thread.setName("recode-log-"+thread.getId());
        thread.setDaemon(true);
        thread.start();
    }


}
