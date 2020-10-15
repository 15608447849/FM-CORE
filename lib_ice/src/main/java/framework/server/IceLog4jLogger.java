package framework.server;

import Ice.Logger;
import bottle.util.Log4j;


/**
 * @Author: leeping
 * @Date: 2019/4/3 10:57
 */
public class IceLog4jLogger implements Ice.Logger {

    @Override
    public Logger cloneWithPrefix(String prefix) {
        return new IceLog4jLogger();
    }

    @Override
    public void error(String message) {
        Log4j.error( message,null );
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public void print(String message) {
        Log4j.info( message );
    }

    @Override
    public void trace(String category, String message) {
        Log4j.trace(category+" , "+message);
    }

    @Override
    public void warning(String message) {
        Log4j.warn( message);
    }
}
