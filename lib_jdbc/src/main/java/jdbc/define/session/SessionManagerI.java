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

    void loadDefaultTransactionIsolationLevel();

    void setSessionTransactionIsolationLevel(TransactionIsolationLevel transIsoLevel);

    TransactionIsolationLevel getCurrentTransactionIsolationLevel();

    boolean isTransactionInvoking();

    void setTransactionInvoking(boolean isInvoking);

    void beginTransaction();

    void commit();

    void rollback();

    void setConnectionFail();
}
