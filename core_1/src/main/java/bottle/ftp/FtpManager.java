package bottle.ftp;

/**
 * Created by lzp on 2017/5/11.
 * ftp 管理器
 */
public interface FtpManager {
    //获取客户端
    FtpClientI getClient(FtpInfo info);
    /* 返回客户端 */
    void backClient(FtpClientI client,boolean isDestroy);
}
