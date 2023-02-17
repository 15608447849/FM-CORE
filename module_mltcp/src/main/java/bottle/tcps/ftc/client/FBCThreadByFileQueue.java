package bottle.tcps.ftc.client;

import bottle.tcps.FTCLog;
import bottle.tcps.ftc.beans.BackupTask;



import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by user on 2017/11/24.
 */
public class FBCThreadByFileQueue extends Thread {

    private final BlockingQueue<BackupTask> queue = new LinkedBlockingDeque<>(); ;
    private final BlockingQueue<BackupTask> queueError = new LinkedBlockingDeque<>();
    private final BlockingQueue<BackupTask> queueError2 = new LinkedBlockingDeque<>();

    private FtcBackupClient ftcBackupClient;
    FBCThreadByFileQueue(FtcBackupClient ftcBackupClient) {
        this.ftcBackupClient = ftcBackupClient;
        this.setName("ftc-t-"+getId());
        this.setDaemon(true);
        //启动定时器
        startTimer();
        this.start();
    }

    private void startTimer() {
        Timer timer = new Timer(true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                while (true){
                    try {
                        BackupTask backupTask = queueError.poll();
                        if (backupTask == null) break;
                        offsetTask(backupTask);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        },15 * 1000L,   60 * 1000L);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                while (true){
                    try {
                        BackupTask backupTask = queueError2.poll();
                        if (backupTask == null) break;
                        offsetTask(backupTask);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        },30 * 1000L, 2 *  60 * 1000L);

    }

    @Override
    public void run() {
        while (true){
            try {
                //在队列中查询,如果存在需要同步的文件 - 打开socket连接 -> 尝试上传文件
                BackupTask task = queue.poll();
                if (task != null){
                    ftcBackupClient.bindSocketSyncUpload(task);
                }else {
                    Thread.sleep(10000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    void offsetTask(BackupTask task) {
        boolean flag = queue.offer(task.incLoopCount());
        if (!flag){
            flag = queueError.offer(task);
            if (!flag){
                flag = queueError2.offer(task);
                if (!flag) FTCLog.info("无法添加任务: "+ task);
            }
        }
//        if (flag) FTCLog.info("添加任务: "+ task);
    }
}
