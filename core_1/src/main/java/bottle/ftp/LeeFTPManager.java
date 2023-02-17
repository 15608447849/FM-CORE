package bottle.ftp;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: leeping
 * @Date: 2020/5/25 17:34
 */
public class LeeFTPManager extends ArrayList<FtpClientI> implements FtpManager,Runnable, Closeable {



    private static class InstantHolder{
        private static LeeFTPManager instants = new LeeFTPManager();
    }
    public static LeeFTPManager getInstants(){
        return InstantHolder.instants;
    }

    private volatile boolean isRuning = true;
    private ReentrantLock lock = new ReentrantLock();
    private final Thread mThread = new Thread(this);
    private LeeFTPManager() {
        mThread.start(); //启动
    }

    @Override
    public FtpClientI getClient(FtpInfo info) {
        try{
            lock.lock();
            if (info==null){ return null; }
            //先去列表查看
            Iterator<FtpClientI> itr = iterator();
            FtpClientI client = null;
            while (itr.hasNext()){
                FtpClientI _client = itr.next();
                if (_client.isUsable() && _client.check(info)){
                    //可用 并且是 同一个 ftp客户端
                    client = _client;
                    break;
                }
            }
            if (client == null){
                client = new LeeFTPClient();
                client.setFtpInfo(info);
                if (!client.connect() || !client.login()){ //连接-登录
                    return null;
                }
                client.inUse();//设置为使用中
                add(client);
            }
            return client;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void backClient(FtpClientI client, boolean isDestroy) {
        if (!isDestroy){
            client.notUsed();
        }else{
            remove(client);
            client.isError();
            client.logout();//登出
            client.disconnect();//断开
        }
    }

    @Override
    public void run() {
        while (isRuning){
            try {
                //检测FTP客户端 是否正在使用中 - 1分钟一次
                check();
                synchronized (this){
                    this.wait(10 * 60 * 1000L);
                }
            }catch(Exception ignored){ }
        }
    }

    //检测
    private void check() {
        try{
            lock.lock();
            if (size() > 0){
                Iterator<FtpClientI> itr = iterator();
                FtpClientI client;
                while (itr.hasNext()){
                    client = itr.next();
                    if (client.isNotUsed()){
                        itr.remove();
                        client.logout();//登出
                        client.disconnect();//断开
                    }
                }
            }
        }finally {
            lock.unlock();
        }
    }

    private void closeAllClient() {
        Iterator<FtpClientI> itr = iterator();
        FtpClientI client;
        while (itr.hasNext()){
            client = itr.next();
            itr.remove();
            client.logout();//登出
            client.disconnect();//断开
        }
    }

    @Override
    public void close() throws IOException {
        isRuning = false;
        synchronized (this){
            this.notifyAll();
        }
//        断开所有连接
        closeAllClient();
    }

    private static FtpClientI genClient(String host,int port,String userName,String password){
        FtpInfo ftpInfo = new FtpInfo(host,port,userName,password);
        FtpClientI clientI = LeeFTPManager.getInstants().getClient(ftpInfo);
        if (clientI == null) {
            throw new RuntimeException("无法获取客户端");
        }
        return clientI;
    }

    public static List<String> query(String host,int port,String userName,String password,String remoteDict){
        FtpClientI clientI = genClient(host,port,userName,password);
        List<String> list = clientI.getFtpFileList(remoteDict);
        LeeFTPManager.getInstants().backClient(clientI,true);
        return list;
    }


    public static boolean upload(String host,int port,String userName,String password,String remoteDict, String remoteFile, InputStream inputStream){
        FtpClientI clientI = genClient(host,port,userName,password);
        try {
            clientI.uploadInputStream(remoteDict, remoteFile,inputStream, new FTPDataTransferListener() {
                @Override
                public void started() {

                }

                @Override
                public void transferred(int length) {
                }

                @Override
                public void completed() {

                }

                @Override
                public void aborted() {

                }

                @Override
                public void failed() {

                }
            });
            LeeFTPManager.getInstants().backClient(clientI,false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LeeFTPManager.getInstants().backClient(clientI,true);
        }
        return false;
    }

    public static boolean upload(String host,int port,String userName,String password,String remoteDict, String remoteFile, File file){
        try (FileInputStream fis = new FileInputStream(file)){
            return upload(host,port,userName,password,remoteDict,remoteFile,fis);
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }


    public static boolean download(String host,int port,String userName,String password,String remotePath, OutputStream out) {
        FtpClientI clientI = genClient(host,port,userName,password);
        try {
            clientI.downloadFile(remotePath, out,0, new FTPDataTransferListener() {
                @Override
                public void started() {

                }

                @Override
                public void transferred(int length) {
                }

                @Override
                public void completed() {

                }

                @Override
                public void aborted() {

                }

                @Override
                public void failed() {

                }
            });
            LeeFTPManager.getInstants().backClient(clientI,false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LeeFTPManager.getInstants().backClient(clientI,true);
        }
        return false;
    }

    public static void stop(){
        try {
            LeeFTPManager.getInstants().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
