package bottle.tcps.ftc.client;

import bottle.tcps.FTCLog;
import bottle.tcps.ftc.beans.BackupTask;
import bottle.threadpool.IOThreadPool;
import bottle.util.GoogleGsonUtil;

import bottle.tcps.ftc.imps.FtcBackAbs;
import bottle.tcps.ftc.beans.BackupFile;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2017/11/23.
 * 文件同步客户端
 */
public class FtcBackupClient extends FtcBackAbs {

    private final FBCTimeTaskOp timeMaps;
    private final FBCThreadBySocketList socketList;
    private final FBCThreadByFileQueue fileQueue;
    private final FileVisitor fileVisitor;

    //存储相关联的同步服务器地址
    private final List<InetSocketAddress> serverAddressList;

    public FtcBackupClient(String directory,int socketMax) {
        //同时传输最大数量
        super(directory);
        if (socketMax == 0) socketMax = Runtime.getRuntime().availableProcessors() * 16;

        this.socketList = new FBCThreadBySocketList(this,socketMax);
        this.fileQueue = new FBCThreadByFileQueue(this);
        this.fileVisitor = new FileVisitor(this);
        this.timeMaps = new FBCTimeTaskOp(this);

        this.serverAddressList = new ArrayList<>();
    }
    private IOThreadPool pool = new IOThreadPool();
    /**
     * 绑定socket客户端
     */
    void bindSocketSyncUpload(BackupTask task){
            try {
                FileUpClientSocket sock = socketList.getSocket(task.getServerAddress());
                if (sock == null) throw new IllegalStateException("没有可用连接");
                sock.startTransferFile(task.getBackupFile());
            } catch (Exception e) {
//                FTCLog.info("服务端通讯异常, "+ e);
                pool.post(() -> {
                    if (task.getLoopCount() > 100){
                        FTCLog.info("放弃任务( " + task + " ) , 无法同步任务到目标服务器");
                        return;
                    }
                    //连接不上服务器 ,放入队列 (最多尝试三次)
                    fileQueue.offsetTask(task);
                });

            }
    }

    private BackupFile genBackupFile(File file) {
        String path  = file.getAbsolutePath();
        String dirPath = new File(this.directory).getAbsolutePath();
        if (path.startsWith(dirPath)){
            path = path.substring(dirPath.length());
            return new BackupFile(dirPath,path);
        }
        return null;
    }

    /**
     * 从服务器列表获取
     */
    public void addBackupFile(File file) {
        if (isFilterSuffixFile(file)) return;
        BackupFile backupFile = genBackupFile(file);
        if (backupFile!=null){
            for (InetSocketAddress socket : serverAddressList){
                fileQueue.offsetTask(new BackupTask(socket,backupFile));
            }
        }
    }

    private final List<String> filterSuffixList = new ArrayList<>();
    /**
     * 添加同步文件后缀过滤
     */
    public void addFilterSuffix(String... filterSuffix){
        if (filterSuffix!=null){
            Collections.addAll(filterSuffixList, filterSuffix);
        }
    }

    /**
     * 是否是过滤文件
     */
    boolean isFilterSuffixFile(File file){
        String fileName = file.getName();
        if (fileName.contains(".")){
            String suffix = file.getName().substring(file.getName().lastIndexOf("."));
            return filterSuffixList.contains(suffix);
        }
        return false;
    }

    /**
     * 遍历目录
     * 1. 获取目录下 过滤掉指定后缀 的 所有文件列表
     * 2. 通知服务器 文件列表, 移除相同文件
     * 3.
     */
    public synchronized void ergodicDirectory(){
        try {
            this.fileVisitor.startVisitor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在某个时间段进行同步
     * list - {"11:00:00","2017-11-27 14:00:00"}
     */
    public void setTime(String json){
        try {
            List<String> list = GoogleGsonUtil.json2List(json,String.class);
            for (String timeStr:list){
                timeMaps.setTime(timeStr);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 监听文件变化 : 设置发现文件后同步到目的地
     */
    public void addServerAddress(InetSocketAddress... serverAddressArray){
        for (InetSocketAddress it : serverAddressArray){
            addServerAddress(it);
        }
    }

    private synchronized void  addServerAddress(InetSocketAddress serverAddress) {
        if (serverAddress==null) return;
        if (!serverAddressList.contains(serverAddress)){
            FTCLog.info("添加远程同步服务器地址 : "+serverAddress);
            serverAddressList.add(serverAddress);
        }
    }

    public synchronized List<InetSocketAddress> getServerAddressList() {
        return serverAddressList;
    }


}
