package bottle.tcps.aio.s;

import bottle.tcps.aio.p.FtcTcpAioManager;
import bottle.tcps.aio.p.Session;
import bottle.tcps.aio.p.SocketImp;

/**
 * Created by user on 2017/7/8.
 * 服务端 - 与某一个客户端的 会话
 */
class ClientSession extends Session {

    ClientSession(FtcTcpAioManager manager, SocketImp connect) {
        super(manager,connect);
        read();
    }

}
