package jdbc.define.session;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:15
 */
public interface SessionManagerI<S> {

    void initialize(Object... args);

    void unInitialize();

    void setSession(S session);

    S getSession();

    void closeSession();

    void beginTransaction();

    void commit();

    void rollback();

    void closeSessionAll();
}
