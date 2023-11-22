package bottle.log;

import org.slf4j.LoggerFactory;

public final class Slf4jExecute implements LogPrintI {
    private static final org.slf4j.Logger slf4j = LoggerFactory.getLogger(Object.class);

    @Override
    public void trace(Object message) {
        slf4j.trace(String.valueOf(message));
    }

    @Override
    public void debug(Object message) {
        slf4j.debug(String.valueOf(message));
    }

    @Override
    public void info(Object message) { slf4j.info(String.valueOf(message)); }

    @Override
    public void error(Throwable e) {
        slf4j.error("",e);
    }

    @Override
    public void error(Object message, Throwable e) {
        slf4j.error(String.valueOf(message),e);
    }

    @Override
    public void warn(Object message) {
        slf4j.warn(String.valueOf(message));
    }

    @Override
    public void fatal(Object message) {
        System.out.println("[slf4j not support] fatal> "+ message);
    }

    @Override
    public String toString() {
        return super.toString()+",imp=("+slf4j+")";
    }
}
