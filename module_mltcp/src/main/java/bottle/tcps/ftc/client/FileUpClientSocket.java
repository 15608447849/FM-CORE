package bottle.tcps.ftc.client;

import bottle.tcps.FTCLog;
import bottle.tcps.ftc.slice.SliceInfo;
import bottle.tcps.ftc.slice.SliceMapper;
import bottle.tcps.ftc.slice.SliceScrollResult;
import bottle.tcps.ftc.slice.SliceUtil;

import bottle.util.TimeTool;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import bottle.tcps.ftc.imps.FTCProtocol;
import bottle.tcps.ftc.beans.BackupFile;

import bottle.tcps.aio.c.FtcSocketClient;
import bottle.tcps.aio.p.FtcTcpActionsAdapter;
import bottle.tcps.aio.p.Session;
import bottle.tcps.aio.p.SessionOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.*;

import static bottle.tcps.ftc.imps.FTCProtocol.*;

/**
 * Created by user on 2017/11/23.
 */
public class FileUpClientSocket extends FtcTcpActionsAdapter{

    private final FtcSocketClient socketClient;
    public final String flag ;

    private final Gson gson = new Gson();
    private static final int BUFFER_SIZE =  1024 * 4; // 1024 * 1024 * 8; //8M 1024 * 8;
    private final FBCThreadBySocketList fbcThreadBySocketList;
    private volatile RandomAccessFile randomAccessFile;
    private BackupFile curFile; //当前待上传文件信息

    private volatile boolean isUsing = false; //是否在使用中

    private SliceScrollResult result;

    FileUpClientSocket(FBCThreadBySocketList fbcThreadBySocketList, InetSocketAddress serverAddress) throws IOException {
        this.fbcThreadBySocketList = fbcThreadBySocketList;
        this.flag = String.format("\t[%s] 客户端管道 ( %s )\t", TimeTool.date_yMd_Hms_2Str(new Date()),serverAddress.toString());
        this.socketClient = new FtcSocketClient(serverAddress,this);
        this.socketClient.connectServer();//连接服务器
        waitConnection();// 等待服务器连接建立
        if (!isConnected()){
            throw new IllegalStateException("无法建立连接>> "+ serverAddress);
        }
    }

    private void waitConnection() {
        synchronized (this){
            try{
                this.wait(30 * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void connectionAnswer(){
        synchronized (this){
            try{
                this.notifyAll();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private synchronized RandomAccessFile getRandomAccessFile() throws FileNotFoundException {
        if (randomAccessFile == null) randomAccessFile = new RandomAccessFile(curFile.getFullPath(),"r");
        return randomAccessFile;
    }


    void startTransferFile(BackupFile curFile) {
        if (isUsing) return;
        this.curFile = curFile;
        isUsing = true;//设置使用中
        uploadFile();
    }

    public boolean isUsing() {
        return isUsing;
    }

    @Override
    public void connectSucceed(Session session) {
        super.connectSucceed(session);
    }

    @Override
    public void receiveString(Session session, String message) {
        connectionAnswer();
        if (message.equals("start")){
            try {
                FTCLog.info("[客户端] 连接建立成功 >> "+ session.getSocket().getLocalAddress() +" <---> " + session.getSocket().getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
//        FTCLog.info("收到字符串: \n"+message);
        try {
            Map map = gson.fromJson(message,Map.class);
            handle(map);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectClosed(Session session) {
        super.connectClosed(session);
        transOver(true);
    }

    boolean isConnected() {
        return socketClient.isAlive();
    }

    public void close() {
        socketClient.close();
    }

    //文件上传开始点
    private void uploadFile() {
        //1. 通知服务器, 发送文件 相对路径,文件名 ,分片信息,完整文件的MD5编码
        Map<String,String> map = new HashMap<>();
        map.put("protocol", FTCProtocol.C_FILE_BACKUP_QUEST);
        map.put("path", curFile.getRelPath());
        map.put("filename", curFile.getFileName());
        map.put("md5", curFile.getMD5());
        map.put("length", String.valueOf(curFile.getFileLength()));
        map.put("block",String.valueOf(SliceUtil.sliceSizeConvert(curFile.getFileLength())));
        socketClient.getSession().getOperation().writeString(gson.toJson(map));
    }

    //处理协议
    private void handle(Map<String, String> map) {
        String protocol = map.get("protocol");
//        System.out.println("客户端收到协议: " + protocol);

        switch (protocol) {
            case FTCProtocol.S_FILE_BACKUP_QUEST_ACK:
//
                serverBackupQuestAck(map);
                break;
            case S_FILE_BACKUP_NOTIFY_STREAM:
//
                transferStream(map);
                break;
            case FTCProtocol.S_FILE_BACKUP_TRS_OVER:

                isUsing = false;
                transOver(false);
                break;
        }
    }

    // 服务端响应上传文件请求 ,告知服务端本地是否存在文件及文件的分片情况
    private void serverBackupQuestAck(Map<String, String> map){
        String slice  = map.remove("slice");//服务器对文件的分片信息
        int sliceSize = Integer.parseInt(map.get("block")); //服务器对文件分片大小
        if (slice.equals("Node")){
            //通知 - 全量传输
            map.put("translate","all");
        }else{
            //计算增量
            //滚动对比差异
            ArrayList<SliceInfo> list = gson.fromJson(slice,new TypeToken<ArrayList<SliceInfo>>(){}.getType());
            //分组
            Hashtable<String,LinkedList<SliceInfo>> table = SliceUtil.sliceInfoToTable(list);
            //滚动检测
            result  = SliceUtil.scrollCheck(table,new File(curFile.getFullPath()),sliceSize);

            if (result!=null && result.getDifferentSize()>0){
                map.put("translate","diff"); //差异传输
                String diff_block_str = result.getDifferentBlockSequence();
                map.put("different",diff_block_str);
                String same_block_str = result.getSameBlockSequence();
                map.put("same",same_block_str);
            }else{
                if (result!=null && result.getSameSize() == list.size()){
                    result = null;
                    //不用传输 通知服务器完成
                    notifyEnd(map);
                    return;
                }else{
                    //全量传输
                    map.put("translate","all");
                    result = null;
                }
            }
        }
        map.put("length",String.valueOf(curFile.getFileLength()));
        map.put("protocol",C_FILE_BACKUP_TRS_START);
        socketClient.getSession().getOperation().writeString(gson.toJson(map)); //通知开始传输
    }

    //流传输
    private void transferStream(Map<String, String> map) {
        try{
            FTCLog.info(flag + " 开始传输 "+curFile.getFullPath());

            SessionOperation op =  socketClient.getSession().getOperation();
            //等待服务端通知 - 最多等待30秒
            RandomAccessFile randomAccessFile = getRandomAccessFile();
            byte[] bytes = new byte[BUFFER_SIZE];
            int len;
            long time = System.currentTimeMillis();
            if (result==null){
                //全部传输
                randomAccessFile.seek(0);
                while( isUsing && ( len = randomAccessFile.read(bytes) )> 0){
                    op.writeBytes(bytes,0,len);
                }
            }else{
                //差异传输
                long count;
                for (SliceMapper slice : result.list_diff){
                    if (!isUsing) return;
                    randomAccessFile.seek(slice.position);
                    count = slice.length;
                    while (count>0){
                        len = (int) Math.min(bytes.length,count);
                        randomAccessFile.read(bytes,0,len);
                        op.writeBytes(bytes,0, len);
                        count = count - len;
                    }
                }
            }
            if (isUsing) FTCLog.info(curFile+
                    " 大小:"+curFile.getFileLength() + " 时间:" + (System.currentTimeMillis() - time)  +" 豪秒"
            );


            notifyEnd(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //通知结束
    private void notifyEnd(Map<String, String> map) {
        //通知服务器传输完成
        map.put("protocol",C_FILE_BACKUP_TRS_END);
        socketClient.getSession().getOperation().writeString(gson.toJson(map)); //结束传输
    }

    //传输完成或连接中断
    private void transOver(boolean isCloseConn) {
        try {
            //FTCLog.info(flag+" ( "+ curFile +" )结束传输 是否正在使用中: "+ isUsing + "  , 连接是否关闭: "+ isCloseConn);
            //判断连接中断
            if (isCloseConn){
                //如果连接关闭且存在任务, 将任务放回任务队列
                if (isUsing){
                    //加入任务队列
                    if (fbcThreadBySocketList!=null && fbcThreadBySocketList.ftcBackupClient!=null){
                        FTCLog.info("连接中断,尝试加入任务(" +curFile+ ") 到队列" );
                        if (curFile!=null){
                            try {
                                fbcThreadBySocketList.ftcBackupClient.addBackupFile(new File(curFile.getFullPath()));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    isUsing = false;
                }
            }

            if (isUsing) return;

            clearCurFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fbcThreadBySocketList !=null) fbcThreadBySocketList.unLockSocket();// 通知队列可用
    }

    private synchronized void clearRandomAccessFile() {
        //关闭本地文件流等 清理任务
        if (randomAccessFile!=null){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                randomAccessFile=null;
            }
        }
    }

    //清理文件
    private void clearCurFile(){
        clearRandomAccessFile();
        curFile = null;
    }

    //检查当前服务端地址是否与指定地址一致
    boolean validServerAddress(InetSocketAddress socketAddress) {
        try {
            return socketClient.getSocket().getRemoteAddress().equals(socketAddress);
        } catch (Exception ignored) {
        }
        return false;
    }
}
