package bottle.tcps.ftc.client;





import bottle.tcps.FTCLog;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by user on 2017/11/24.
 */
class FBCThreadBySocketList {

    private final int max;

    private final HashMap<InetSocketAddress, List<FileUpClientSocket>> maps = new HashMap<>();

    FtcBackupClient ftcBackupClient;

    FBCThreadBySocketList(FtcBackupClient ftcBackupClient, int max) {
        this.ftcBackupClient = ftcBackupClient;
        this.max = max;
    }

    private void removeErrorSocket(List<FileUpClientSocket> list) {
        FileUpClientSocket socket;
        Iterator<FileUpClientSocket> iterator = list.iterator();
        while (iterator.hasNext()){
            socket = iterator.next();
            //无效连接
            if (!socket.isConnected()) {
                socket.close();
                iterator.remove();
                FTCLog.info(socket.flag + " 队列移除");
            }
        }
    }

    synchronized FileUpClientSocket getSocket(InetSocketAddress serverAddress) throws Exception{
//            (Thread.currentThread()+" 获取socket,当前队列数" + list.size() +" , 最大数:"+ max);
        List<FileUpClientSocket> list = maps.computeIfAbsent(serverAddress, k -> new ArrayList<>(max));
        removeErrorSocket(list);
        FileUpClientSocket socket;
        if (list.size()>0){
            for (FileUpClientSocket fileUpClientSocket : list) {
                socket = fileUpClientSocket;
//                    (Thread.currentThread()+"队列获取socket:"+socket.getFlag());
                //地址相同 , 未使用 , 连接中
                if (socket.validServerAddress(serverAddress) && socket.isConnected() && !socket.isUsing()) {
                    return socket;
                }
            }
        }

        if (list.size()<max){
            //在最大连接限制内 , 创建sok连接
            socket = new FileUpClientSocket(this,serverAddress);
            list.add(socket);
            return socket;
        }else{
//            FTCLog.info("SOCKET 队列进入等待中...");
            //最大连接数
            lockSocketList();
//            FTCLog.info("SOCKET 队列结束等待");
        }
        return null;

    }

    private void lockSocketList() {
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void unLockSocket() {
        synchronized (this){
            notifyAll();
        }
    }
}
