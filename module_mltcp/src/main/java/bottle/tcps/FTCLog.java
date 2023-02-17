package bottle.tcps;

import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.log.PrintLogThread;

public class FTCLog {
    public static void info(Object object){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.info,object));
    }
    public static void error(Object object,Throwable e){
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.error,object,e));
    }
}
