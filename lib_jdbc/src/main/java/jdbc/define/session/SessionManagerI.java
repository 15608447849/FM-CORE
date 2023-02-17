package jdbc.define.session;

import java.util.Properties;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:15
 */
public interface SessionManagerI<S> {

    void initialize(Properties properties);

    void unInitialize();

    void setSession(S session);

    S getSession();

    void closeSession();

    void beginTransaction();

    void commit();

    void rollback();

    void closeSessionAll();
}
