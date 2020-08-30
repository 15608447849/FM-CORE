package jdbc.define.session;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:25
 */
public abstract class SessionManagerAbs<S> implements SessionManagerI<S> {

    private final ThreadLocal<S> localSession = new ThreadLocal<>();
    private final ThreadLocal<Boolean> invoking = new ThreadLocal<>();


    @Override
    public void setSession(S session) {
        this.localSession.set(session);
    }

    @Override
    public S getSession() {
        return this.localSession.get();
    }


    @Override
    public boolean isTransactionInvoking() {
        return Boolean.TRUE == this.invoking.get();
    }

    @Override
    public void setTransactionInvoking(boolean isInvoking) {
        this.invoking.set(isInvoking);
    }

    @Override
    public void unInitialize() {
        this.localSession.remove();
        this.invoking.remove();
    }
}
