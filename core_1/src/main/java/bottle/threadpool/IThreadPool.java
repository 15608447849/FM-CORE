package bottle.threadpool;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


public interface IThreadPool extends Closeable {
    void post(Runnable runnable);
    <V> Future<V> submit(Callable<V> callable);
}
