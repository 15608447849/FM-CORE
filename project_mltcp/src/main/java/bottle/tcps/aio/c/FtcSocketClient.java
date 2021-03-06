package bottle.tcps.aio.c;


import bottle.tcps.aio.p.FtcTcpActions;
import bottle.tcps.aio.p.Session;
import bottle.tcps.aio.p.SocketImp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

/**
 * Created by user on 2017/7/8.
 * 1 发送字符串
 * 2 接受字符串
 * 3 接受数据
 *
 */
public class FtcSocketClient implements SocketImp, CompletionHandler<Void, Void> {
    //连接等待时间
    private final long connectingTime;
    public AsynchronousSocketChannel socket;
    private InetSocketAddress localAddress;
    private AsynchronousChannelGroup asynchronousChannelGroup;
    private boolean isConnected;
    private InetSocketAddress serverAddress;
    private FtcTcpActions communicationAction;
    private final ServerSession session = new ServerSession(this);//读取写入

    public FtcSocketClient(InetSocketAddress serverAddress, FtcTcpActions communicationAction) {
        this(null,serverAddress,communicationAction,3000);
    }

    public FtcSocketClient(InetSocketAddress localAddress, InetSocketAddress serverAddress, FtcTcpActions communicationAction, long reTime) {
        this.localAddress = localAddress;
        this.connectingTime = reTime;
        this.serverAddress = serverAddress;
        this.communicationAction = communicationAction;

    }
    //连接服务器
    public void connectServer() throws IOException{

        if (isAlive()) return;

        if (asynchronousChannelGroup == null){
            asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
        }

        socket = AsynchronousSocketChannel.open(asynchronousChannelGroup);

        if (localAddress!=null){
            socket.bind(localAddress);
        }

        socket.setOption(StandardSocketOptions.SO_KEEPALIVE,true);//保持连接 https://blog.csdn.net/gavin1203/article/details/5290609
        socket.setOption(StandardSocketOptions.TCP_NODELAY,true); //https://blog.csdn.net/lclwjl/article/details/80154565
//        socket.setOption(StandardSocketOptions.SO_LINGER,0); //当主动关闭方设置了setSoLinger（true,0）时，并调用close后，立该发送一个RST标志给对端，该TCP连接将立刻夭折
        socket.connect(serverAddress,null,this);

        if (!isAlive()){
            //等待连接
            synchronized (this){
                try {
                    this.wait(connectingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //如果连接不上服务器 - 报异常
            if (!isAlive()){
                throw new SocketException("local client( "+ socket.getLocalAddress() + " ) connect remote server( " + serverAddress + " ) fail.");
            }
        }


    }


    @Override
    public void completed(Void aVoid, Void aVoid2) {
        isConnected = true;
        synchronized (this){
            this.notifyAll();
        }
        //成功连接
        communicationAction.connectSucceed(session);
        //启动数据读取
        session.read();
    }

    @Override
    public void failed(Throwable throwable, Void aVoid) {
        //连接失败异常,关闭连接
        synchronized (this){
            notifyAll();
        }
        communicationAction.error(session,throwable,null);
        communicationAction.connectFail(session);
        communicationAction.connectClosed(session);
    }

    /**
     * 关闭连接
     */
    private void closeConnect() {
        if (socket == null) return;
        try {
            session.clear();//清理会话
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                socket.close();//关闭管道
            }
        } catch (Exception e) {
            communicationAction.error(session,null,e);
        }finally {
            isConnected = false;
            socket = null;
        }
    }



    @Override
    public AsynchronousSocketChannel getSocket() {
        return socket;
    }

    @Override
    public boolean isAlive() {
        return socket!=null && socket.isOpen()&&isConnected;
    }

    @Override
    public FtcTcpActions getAction() {
        return communicationAction;
    }

    @Override
    public void setAction(FtcTcpActions action) {
        this.communicationAction = action;
    }

    @Override
    public void close() {
        closeConnect();
    }

    @Override
    public Session getSession() {
        return session;
    }

}
