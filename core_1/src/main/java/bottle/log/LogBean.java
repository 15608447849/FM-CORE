package bottle.log;

public class LogBean {
    protected boolean isEnableCallback = true;
    public final String threadName = Thread.currentThread().getName();
    public final long threadId = Thread.currentThread().getId();
    public final String threadState = Thread.currentThread().getState().name();
    public final long timestamp = System.currentTimeMillis();
    public final String directory;
    public final String file;
    public final LogLevel level;
    public final Object content;
    public final Throwable e;

    // 指定文件
    public LogBean(String directory,String file,String content) {
        this.directory = directory;
        this.file = file;
        this.level = LogLevel.specifyFile;
        this.content = content;
        e = null;
    }

    // 其他日志
    public LogBean(LogLevel level, Object content) {
        this.directory = null;
        this.file = null;
        this.level = level;
        this.content = content;
        this.e = null;
    }

    // 错误日志
    public LogBean(LogLevel level, Object content, Throwable e) {
        this.directory = null;
        this.file = null;
        this.level = level;
        this.content = content;
        this.e = e;
    }

    public LogBean setEnableCallback(boolean flag){
        this.isEnableCallback = flag;
        return this;
    }

}
