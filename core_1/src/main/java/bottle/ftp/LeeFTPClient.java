package bottle.ftp;



import it.sauronsoftware.ftp4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzp on 2017/5/11.
 * ftp客户端程序
 */
public class LeeFTPClient extends it.sauronsoftware.ftp4j.FTPClient implements FtpClientI {

    public static boolean isDebug = true;


    private static final String TAG = "FTP客户端";
    private FtpInfo info;
    private int state = 0;//0未使用  1使用中 2不可用

    LeeFTPClient() {
        this.setPassive(true);
        this.setType(FTPClient.TYPE_BINARY);
    }

    @Override
    public void setFtpInfo(FtpInfo ftpUser) {
        if (ftpUser == null) throw new IllegalStateException("ftp信息错误");
        this.info = ftpUser;
    }

    @Override
    public boolean check(FtpInfo ftpUser) {
        return this.info.equals(ftpUser);
    }

    @Override
    public boolean connect() {
        if (isConnected()) return true;
        try {
           String [] welcomeInfo = connect(info.getHost(),info.getPort());
            for (String str : welcomeInfo){
                if (isDebug) System.out.println(TAG+" 成功连接> "+ getHost() +" - "+ getPort()+" "+ str);
            }
        return true;
        } catch (IOException | FTPException | FTPIllegalReplyException ignored) {

        }
        return false;
    }

    @Override
    public boolean login() {
        if (isAuthenticated()) return true;
        try {
            login(info.getUserName(),info.getPassword());
            setAutoNoopTimeout(20 * 1000);
            if (isCompressionSupported()){
                setCompressionEnabled(true);
            }
            if (isDebug) System.out.println(TAG+" 登录> "+ getUsername()+ " - "+ getPassword()+" "+ currentDirectory() );
            return true;
        } catch (IOException | FTPException | FTPIllegalReplyException ignored) {

        }
        return false;
    }

    @Override
    public void logout() {
        if (isDebug) System.out.println(TAG+" 登出> "+ getUsername()+ " - "+ getPassword());
        if (isAuthenticated()) try {
            super.logout();
        } catch (IOException | FTPException | FTPIllegalReplyException ignored) {

        }
    }

    @Override
    public void disconnect() {
        if (isConnected()){
            try {
                super.disconnect(true);
            } catch (IOException | FTPException | FTPIllegalReplyException ignored) {

            }finally {
                try {
                    super.disconnect(false);
                } catch (Exception ignored) {
                }
            }
            if (isDebug) System.out.println(TAG+" 断开连接> "+ getHost()+ ":"+ getPort());
        }
    }

    @Override
    public void inUse() {
        this.state = 1;
    }

    @Override
    public void notUsed() {
        this.state = 0;
    }

    @Override
    public void isError() {
        this.state = 2;
    }

    @Override
    public boolean isUsable() {
        return state==0; //可用
    }

    @Override
    public boolean isNotUsed() {
        return state==0 || state == 2; //未使用或者错误
    }

    @Override
    public List<String> getFtpFileList(String remoteDir) {
        if (remoteDir.lastIndexOf("/")!=remoteDir.length()-1){
            remoteDir+="/";
        }
        List<String> list = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = list(remoteDir);
            for (FTPFile ftpFile : ftpFiles){
                String name = ftpFile.getName();
                if (ftpFile.getType() == FTPFile.TYPE_FILE){
                    list.add(remoteDir+name);
                }
                if (ftpFile.getType() == FTPFile.TYPE_DIRECTORY){
                    list.addAll(getFtpFileList(remoteDir+name+"/"));
                }
            }
        } catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException | FTPAbortedException | FTPListParseException ignored) { }
        return list;
    }

    @Override
    public long getFtpFileSize(String ftpAbsulutePath) {
        long size = 0;
        try {
           size =  fileSize(ftpAbsulutePath);
        } catch (IOException | FTPException | FTPIllegalReplyException ignored) {
        }
        return size;
    }

    @Override
    public void downloadFile(String remotePath, OutputStream out, long startPoint, FTPDataTransferListener listener) {
        try {
            download(remotePath,out,startPoint,listener);

        } catch (IOException | FTPIllegalReplyException | FTPException | FTPAbortedException | FTPDataTransferException e) {
            listener.failed();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uploadFile(String remoteDir, String remoteFileName, File localFile, FTPDataTransferListener listener) {
        try {
            if (remoteDir==null || remoteDir.length()==0) remoteDir="/";//改变当前目录到根目录
            if (isDebug) System.out.println(info+"( "+currentDirectory()+" )上传文件> " + localFile);
            if (!localFile.exists()) {
                listener.aborted();
                throw new IllegalArgumentException("file '" + localFile.getAbsolutePath()+"' no exist.");
            }
            if (remoteFileName == null || remoteFileName.equals("")) remoteFileName = localFile.getName();

            try (InputStream inputStream = new FileInputStream(localFile)) {

                uploadInputStream(remoteDir,remoteFileName,inputStream,listener);
            } catch (IOException e) {
                listener.aborted();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            listener.failed();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uploadInputStream(String remoteDir, String remoteFileName, InputStream inputStream, FTPDataTransferListener listener) {
        try {
            if (inputStream == null){
                listener.aborted();
                throw new IllegalArgumentException("inputStream = " + inputStream);
            }
            if (remoteDir==null || remoteDir.length()==0) remoteDir="/";//改变当前目录到根目录
            if (isDebug) System.out.println(info+"( "+currentDirectory()+" )上传> " + remoteDir+" "+ remoteFileName );
            changeDirectory("/");
            if (!currentDirectory().equals(remoteDir)) {//判断远程路径
                //层级创建目录
                final String[] dirNameArr = remoteDir.split("/");

                int i = 0;
                while (i<dirNameArr.length) {
                    if (dirNameArr[i].length()>0){
                        //尝试进入,无法进入,尝试创建再次进入
                        try {
                            if (isDebug) System.out.println(info+"( "+currentDirectory()+" )上传> 尝试进入目录 "+ dirNameArr[i]);
                            changeDirectory(dirNameArr[i]);
                        } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException e) {
                            if (e instanceof FTPException
                                    && ((FTPException) e).getCode() == 550) {
                                if (isDebug) System.out.println(info+"( "+currentDirectory()+" )上传文件> 尝试创建目录 "+ dirNameArr[i]);
                                //创建目录
                                createDirectory(dirNameArr[i]);
                                //再次进入
                                changeDirectory(dirNameArr[i]);
                            } else {
                                throw e;
                            }
                        }
                    }
                    i++;
                }
            }
            upload(remoteFileName, inputStream, 0, 0, listener);
        } catch (Exception e) {
            listener.failed();
            throw new RuntimeException(e);
        }
    }

}
