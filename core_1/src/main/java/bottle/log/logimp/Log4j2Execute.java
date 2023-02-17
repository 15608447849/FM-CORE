package bottle.log.logimp;

import bottle.log.LogPrintI;

public class Log4j2Execute implements LogPrintI {
    private static final org.apache.logging.log4j.Logger log4j2 = org.apache.logging.log4j.LogManager.getLogger();

    static {
        try {
            //异步日志
            System.setProperty("Log4jContextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //桥接jul
            org.apache.logging.log4j.jul.Log4jBridgeHandler.install(true,null,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trace(Object message) {
        log4j2.trace(message);
    }

    @Override
    public void debug(Object message) {
        log4j2.debug(message);
    }

    @Override
    public void info(Object message) {
        log4j2.info(message);
    }

    @Override
    public void error(Throwable e) { log4j2.error(e); }

    @Override
    public void error(Object message, Throwable e) {
        log4j2.error(message,e);
    }

    @Override
    public void warn(Object message) { log4j2.warn(message); }

    @Override
    public void fatal(Object message) {
        log4j2.fatal(message);
    }

}
