package bottle.tcps.aio.s;


import bottle.tcps.aio.p.*;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by user on 2017/7/8.
 * 读取数据 和 写入数据
 */
public class ClientConnect implements SocketImp {
    private FtcTcpAioManager server;
    //与某一个客户端的管道
    private AsynchronousSocketChannel socket;
    //与客户端的 会话
    private ClientSession session;
    //通讯实现对象
    private FtcTcpActions action;

   public ClientConnect setFtcTcpAioManager(FtcTcpAioManager server) {
        this.server = server;
        return this;
    }

    @Override
    public AsynchronousSocketChannel getSocket() {
        return socket;
    }

    public ClientConnect setSocket(AsynchronousSocketChannel socket) {
        this.socket = socket;
        return this;
    }
    public ClientConnect initial(){
        server.add(this);
        session = new ClientSession(server,this);
        action.connectSucceed(session);
        return this;
    }
    @Override
    public boolean isAlive(){
        return socket!=null && socket.isOpen();
    }

    @Override
    public FtcTcpActions getAction() {
        return action;
    }

    @Override
    public void setAction(FtcTcpActions action) {
        this.action = action;
    }


    @Override
    public void close(){
        try {
            if (socket!=null){
                try{
                    socket.shutdownInput();
                }catch (IOException e){
                    e.printStackTrace();
                }
                try {
                    socket.shutdownOutput();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (session!=null){
                try {
                    session.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            action.error(session,null,e);
        }finally {
            action.connectClosed(session);//客户端在服务端的连接
            server.remove(this);//服务器 队列中移除一个连接管理对象.
            socket = null;
            server = null;
            session = null;
        }
    }

    @Override
    public Session getSession() {
        return session;
    }



}
