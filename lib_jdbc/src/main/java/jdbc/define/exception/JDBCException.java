package jdbc.define.exception;

import jdbc.define.log.JDBCLogger;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:34
 */
public class JDBCException extends RuntimeException{

    public JDBCException(String desc) {
        super(desc);
    }

    public JDBCException(Throwable e) {
        super(e);
    }

    public JDBCException(String desc, Throwable e) {
        super(desc, e);
        JDBCLogger.error(desc,e);
    }

}
