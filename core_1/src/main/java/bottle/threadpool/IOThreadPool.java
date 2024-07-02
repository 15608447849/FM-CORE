package bottle.threadpool;
import bottle.log.PrintLogThread;
import bottle.log.WriteLogThread;

import java.util.Date;
import java.util.concurrent.*;

import static bottle.util.StringUtil.printExceptInfo;


public class IOThreadPool extends Thread implements IThreadPool {
    private static final String TAG = "IOTP";
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ThreadPoolExecutor executor;

    private boolean isLoop = true;
    private long createCount = 0;
    private long postCount = 0;

    private final String name;
    public IOThreadPool(int capacity) {
        name = TAG+"-"+getId() ;
        executor = createIoExecutor(capacity);
        setDaemon(true);
        setName(name);
        start();
    }

    public IOThreadPool() {
        this(500);
    }

    //核心线程数,最大线程数,非核心线程空闲时间,存活时间单位,线程池中的缓冲队列
    private ThreadPoolExecutor createIoExecutor(int capacity) {
         return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 8,
                 Math.max(Runtime.getRuntime().availableProcessors() * 8,capacity),
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>( capacity ),
                 r -> {
                     Thread thread = new Thread(r);
                     createCount++;
                     thread.setName(name+"-"+createCount+"-"+thread.getId());
                     recode("create thread: "+ thread+" , current runnable total: "+ postCount);
                     return thread;
                 },
                 (runnable, executor) -> {
                     //超过IO线程池处理能力的任务,进入单线程执行队列
                     boolean isAdd = queue.offer(runnable);
                     recode("abandon runnable: "+runnable +" , add result: "+ isAdd + " , current queue size: "+ queue.size() );
                 }
         );
    }

    private void recode(String str) {
        String info = PrintLogThread.sdf.format(new Date())+" ["+name+"]\t" + str+"\n";
        System.out.print(info);
        WriteLogThread.appendContentToFile("./logs/iotp/", PrintLogThread.sdfDict.format(new Date()), info);
    }

    @Override
    public void run() {
        while (isLoop){
            try{
                //如果存在任务 , 一直执行 ,直到队列空, 进入等待执行
                Runnable runnable = queue.take();
                if (isLoop) {
                    recode("queue threaded execute: "+runnable);
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        recode("queue threaded execute error: "+runnable + "\n"+ printExceptInfo(e));
                    }
                }
            }catch (Exception e){
                recode(printExceptInfo(e));
            }
        }
    }

    @Override
    public void post(Runnable runnable){
        postCount++;
        executor.execute(runnable);
    }

    @Override
    public <V> Future<V> submit(Callable<V> callable) {
        postCount++;
        return executor.submit(callable);
    }

    @Override
    public void close(){
        queue.clear();
        isLoop = false;
        if (executor!=null) executor.shutdownNow();
    }
}
