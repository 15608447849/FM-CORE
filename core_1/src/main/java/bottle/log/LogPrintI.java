package bottle.log;

public interface LogPrintI {
    void trace(Object message);
    void debug(Object message);
    void info(Object message);
    void error(Throwable e);
    void error(Object message,Throwable e);
    void warn(Object message);
    void fatal(Object message);
}
