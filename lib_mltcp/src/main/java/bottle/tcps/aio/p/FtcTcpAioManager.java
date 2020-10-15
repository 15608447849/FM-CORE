package bottle.tcps.aio.p;

import java.util.List;

/**
 * Created by user on 2017/11/22.
 */
public interface FtcTcpAioManager {

    //添加客户端连接对象
    void add(SocketImp clientSocket);
    //移除客户端连接对象
    void remove(SocketImp clientSocket);
    //所有客户端连接对象
    List<SocketImp> getCurrentClientList();
    //当前客户端数量
    int getCurrentClientSize();

}
