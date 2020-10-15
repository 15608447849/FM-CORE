package bottle.tcps.ftc.server;

import bottle.tcps.ftc.imps.FtcBackAbs;
import bottle.tcps.aio.p.FtcTcpActionsAdapter;
import bottle.tcps.aio.s.FtcSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by user on 2017/11/23.
 * 文件同步 - 服务端
 */
public class FtcBackupServer extends FtcBackAbs implements FtcSocketServer.ServerCallback {

    //本地服务端socket 地址
    private final FtcSocketServer sockSer;

    public FtcBackupServer(String directory, String ip, int port) throws IOException {
        super(directory);
        //socket 服务端
        sockSer = new FtcSocketServer(new InetSocketAddress(ip, port),this);
        //接入客户端
        sockSer.openListener().launchAccept();
    }

    public FtcSocketServer getSockSer() {
        return sockSer;
    }

    @Override
    public FtcTcpActionsAdapter createClientAction() {
        return new FileUpServerHandle(getDirectory());
    }
}
