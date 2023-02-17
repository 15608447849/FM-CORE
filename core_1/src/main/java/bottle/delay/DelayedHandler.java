package bottle.delay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

public abstract class DelayedHandler<T extends IDelayedObject> extends Thread implements IDelayedObjectCallback<T> {

    public enum DelayedTimeType { DAY, HOUR, MINUTES, SECOND, MILLSECOND }

    private final Map<String, DelayedObjectWarp<T>> delayObjectMap = new ConcurrentHashMap<>();//控制唯一

    private final DelayQueue<DelayedObjectWarp<T>> delayQueue = new DelayQueue<>();// 通过延时队列控制定时处理
    private final long delayTime;// 延迟时间(默认)
    private DelayedTimeType time_type;// 延迟时间类型(默认)
    private final IDelayedObjectCallback<T> callback;// 回调对象

    public DelayedHandler(long delayTime, DelayedTimeType time_type, IDelayedObjectCallback<T> handlerCall) {
        this.delayTime = delayTime;
        this.time_type = time_type;
        this.callback = handlerCall;
        this.setDaemon(true);
        this.start();
    }

    private static long convToMillisecond(long delayTime, DelayedTimeType time_type) {
        long result = delayTime;

        switch (time_type) {
            case DAY:
                result *= 24;
            case HOUR:
                result *= 60;
            case MINUTES:
                result *= 60;
            case SECOND:
                result *= 1000;
            case MILLSECOND:
                break;
        }
        return result;
    }

    @Override
    public boolean handlerCall(T iDelayedObject) {
        if(callback!=null) return callback.handlerCall(iDelayedObject);
        else return false;
    }

    public void add(T delayed,long delayTime, DelayedTimeType time_type) {
        if (delayed == null)  return;
        if (this.delayObjectMap.containsKey(delayed.getUnqKey())) return;

        DelayedObjectWarp<T> warp = new DelayedObjectWarp<>(delayed,this, convToMillisecond(delayTime, time_type));
        this.delayObjectMap.put(delayed.getUnqKey(), warp);
        boolean add = this.delayQueue.add(warp);
        if (add){
            addSuccess(warp);
        }
    }

    public void add(T delayed) {
        add(delayed,this.delayTime, this.time_type);
    }

    public void remove(T delayed) {
        if (delayed == null) return;

        DelayedObjectWarp<T> obj = this.delayObjectMap.remove(delayed.getUnqKey());
        if (obj == null) return;

        boolean remove = this.delayQueue.remove(obj);
        if (remove) removeSuccess(obj);
    }

    protected abstract void addSuccess(DelayedObjectWarp<T> delayed);

    protected abstract void removeSuccess(DelayedObjectWarp<T> delayed);

    @Override
    public void run() {
        while (true) {
            try {
                //定时处理
                DelayedObjectWarp<T> warp = delayQueue.take();
                if ( warp.execute()) remove(warp.getObj());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
