package bottle.ftp;

/**
 * Created by lzp on 2017/5/8.
 * FTP信息
 */
public class FtpInfo {
    private final String host;
    private final int port;
    private final String userName;
    private final String password;

    public FtpInfo(String host, int port, String userName, String password) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "用户名: " + userName+" ,密码: " + password+" ,主机地址: " + host+" ,端口号: " + port;
    }

    //比较是不是同一个ftp的内容
    @Override
    public boolean equals(Object o) {
        if (o == null){
            return false;
        }
        if (o instanceof FtpInfo){
            FtpInfo fuser = (FtpInfo) o;
            return this.host.equals(fuser.getHost()) && this.port == fuser.getPort() && this.userName.equals(fuser.getUserName()) && this.password.equals(fuser.getPassword());
        }
       return super.equals(o);
    }

    @Override
    public int hashCode() {
        return host.hashCode()+port+userName.hashCode()+password.hashCode();
    }


}
