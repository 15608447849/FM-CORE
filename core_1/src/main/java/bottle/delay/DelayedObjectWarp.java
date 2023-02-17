package bottle.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Author: leeping
 * @Date: 2020/7/7 13:11
 */
public class DelayedObjectWarp<T extends IDelayedObject> implements Delayed {
    private T obj;

    private IDelayedObjectCallback<T> callback;

    private long removeTime;

    public DelayedObjectWarp() {}

    public DelayedObjectWarp(T obj, IDelayedObjectCallback<T> callback, long delayedTimeSec) {
        this.obj = obj;
        this.callback = callback;
        this.removeTime = delayedTimeSec + System.currentTimeMillis();
    }

    public boolean execute() {
        return this.callback.handlerCall(this.obj);
    }

    public T getObj() {
        return obj;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(removeTime - System.currentTimeMillis(), TimeUnit.MICROSECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DelayedObjectWarp<?> that = (DelayedObjectWarp<?>) o;

        return that.obj.getUnqKey().equals(this.obj.getUnqKey());
    }

    @Override
    public int hashCode() {
        return this.obj != null ? this.obj.getUnqKey().hashCode() : 0;
    }

    @Override
    public int compareTo(Delayed o) {
        if (this == o) return 0;

        if (o instanceof DelayedObjectWarp) {
            DelayedObjectWarp<?> cd = (DelayedObjectWarp<?>) o;

            long diff = this.removeTime  - cd.removeTime;

            return diff < 0 ? -1 : diff > 0 ? 1 : 0;
        }
        return -1;
    }


}
