package bottle.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static bottle.log.WriteLogThread.addWriteFileQueue;

public class PrintLogThread{
    /* 年月日时分秒 */
    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /* 年月日 */
    public final static SimpleDateFormat sdfDict = new SimpleDateFormat("yyyyMMdd");
    /* 年月日时 */
    public final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HH");
    // 默认实现
    private static LogPrintI logPrintI = new Log4j2Execute();

    /* 日志回调接收 */
    private static final List<PrintCallback> callbackList = new ArrayList<>();

    // 日志消息队列
    private static final LinkedBlockingQueue<LogBean> logsQueue = new LinkedBlockingQueue<>();


    public static void setPrint(LogPrintI execute) {
        logPrintI = execute;
    }

    public static void except(Throwable throwable){
        if (logPrintI == null) throwable.printStackTrace();
        else logPrintI.error(throwable);
    }

    public static synchronized void addCallback(PrintCallback callback) {
        callbackList.add(callback);
//        if (logPrintI!=null) logPrintI.info(" add log callback : "+ callback +" current size: "+ callbackList.size());
    }

    // 添加日志消息到队列
    public static void addMessageQueue(LogBean bean){
        try {
            if (logPrintI!=null) {
                String content = String.valueOf(bean.content);
                if (bean.level == LogLevel.specifyFile){
                    addWriteFileQueue(bean.directory,bean.file,  bean.threadName+"\t"+content);
                }
                else if (bean.level == LogLevel.trace) {
                    logPrintI.trace(content);
                } else if (bean.level == LogLevel.debug) {
                    logPrintI.debug(content);
                } else if (bean.level == LogLevel.info) {
                    logPrintI.info(content);
                } else if (bean.level == LogLevel.error) {
                    logPrintI.error(content,bean.e);
                } else if (bean.level == LogLevel.warn) {
                    logPrintI.warn(content);
                } else if (bean.level == LogLevel.fatal) {
                    logPrintI.fatal(content);
                }
            }

            // 添加到队列
            if (bean.isEnableCallback) logsQueue.add(bean);

        } catch (Exception e) {
            except(e);
        }

    }

    private static final Thread callbackThread = new Thread(() -> {
        while (true){
            try {
                LogBean bean = logsQueue.take();
                // 日志回调
                for (PrintCallback callback: callbackList){
                    try {
                        callback.callback( bean );
                    } catch (Exception e) {
                        except(e);
                    }
                }
            } catch (Exception ex) {
                except(ex);
            }
        }
    });

    static {
        callbackThread.setName("log-"+ callbackThread.getId());
        callbackThread.setDaemon(true);
        callbackThread.start();
    }


}
