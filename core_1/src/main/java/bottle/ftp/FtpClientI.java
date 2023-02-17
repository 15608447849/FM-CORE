package bottle.ftp;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by lzp on 2017/5/11.
 *
 */
public interface FtpClientI {
    void setFtpInfo(FtpInfo ftpUser);//设置ftp信息
    boolean check(FtpInfo ftpUser);//检查是否是指定ftp服务器
    boolean connect();//连接
    boolean login();//登陆
    void logout();//登出
    void disconnect();//断开
    void inUse();
    void notUsed();
    void isError();
    boolean isUsable();//是否可用
    boolean isNotUsed();//未被使用
    List<String> getFtpFileList(String remoteDir);
    long getFtpFileSize(String ftpServerPath);
    void downloadFile(String remotePath, OutputStream out, long startPoint, it.sauronsoftware.ftp4j.FTPDataTransferListener listener);
    void uploadFile(String remoteDir, String remoteFileName, File localFile, it.sauronsoftware.ftp4j.FTPDataTransferListener listener);
    void uploadInputStream(String remoteDir, String remoteFileName, InputStream inputStream, it.sauronsoftware.ftp4j.FTPDataTransferListener listener);
}
