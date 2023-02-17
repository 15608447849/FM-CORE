package bottle.util;

public class ThreadTool {

    public static void threadSleep(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
