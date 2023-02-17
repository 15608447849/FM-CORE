package bottle.threadpool;
import java.util.concurrent.*;

public class IOSingerThreadPool implements IThreadPool {

    private final ExecutorService executor;

    public IOSingerThreadPool(int num) {
        if (num<=1){
            executor = Executors.newSingleThreadExecutor();
        }else {
            executor = Executors.newFixedThreadPool(Math.min(num,Runtime.getRuntime().availableProcessors()*8));
        }

    }

    @Override
    public void post(Runnable runnable){
        executor.execute(runnable);
    }

    @Override
    public <V> Future<V> submit(Callable<V> callable) {
        return executor.submit(callable);
    }
    @Override
    public void close(){
       executor.shutdownNow();
    }
}
