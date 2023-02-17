package bottle.tcps.aio.s;

import bottle.tcps.FTCLog;
import bottle.tcps.aio.p.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by user on 2017/7/8.
 * aio 实现
 */
public class FtcSocketServer implements CompletionHandler<AsynchronousSocketChannel, ClientConnect>, FtcTcpAioManager {

    public interface ServerCallback{
        FtcTcpActionsAdapter createClientAction();
    }

    //监听本地地址信息
    private InetSocketAddress address;
    //异步连接socket
    private AsynchronousServerSocketChannel listener;

    private ServerCallback serverCallback;

    //客户端列表
    private final LinkedList<SocketImp> clientConnectList = new LinkedList<>();

    public FtcSocketServer(InetSocketAddress address,ServerCallback serverCallback) {
        this.address = address;
        this.serverCallback = serverCallback;
    }

    //打开监听连接
    public FtcSocketServer openListener() throws IOException{
        listener = AsynchronousServerSocketChannel.open(AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*16))).bind(address);
        listener.setOption(StandardSocketOptions.SO_REUSEADDR,true); //即使socket断了，重新调用前面的socket函数不会再去占用新的一个，而是始终就是一个端口，这样防止socket始终连接不上，会不断地换新端口

        FTCLog.info("TCP SERVER LISTEN : "+listener.getLocalAddress());
        return this;
    }

    //开始接入,等待客户端连接
    public void launchAccept(){
        //接收一个连接
        if (listener!=null && listener.isOpen() && serverCallback!=null){
           try{
               //创建
               ClientConnect clientConnect = new ClientConnect();
               clientConnect.setFtcTcpAioManager(this);
               //关联回调实现对象
               FtcTcpActions clientAction = serverCallback.createClientAction();
               if (clientAction!=null){
                   clientConnect.setAction(clientAction);
                   listener.accept(clientConnect, this);
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

    @Override
    public void completed(AsynchronousSocketChannel asynchronousSocketChannel, ClientConnect clientConnect) {
        //连接成功 处理当前连接
        if (clientConnect!=null){
            clientConnect.setSocket(asynchronousSocketChannel).initial();
        }
        launchAccept();
    }

    @Override
    public void failed(Throwable throwable, ClientConnect clientConnect) {
        //连接失败
        if (clientConnect!=null){
            clientConnect.close();
        }
        launchAccept();
    }

    @Override
    public synchronized void add(SocketImp clientConnect) {
        clientConnectList.add(clientConnect);
    }

    @Override
    public synchronized void remove(SocketImp clientConnect) {
        clientConnectList.remove(clientConnect);
    }

    @Override
    public List<SocketImp> getCurrentClientList(){
        return clientConnectList;
    }

    @Override
    public int getCurrentClientSize() {
        return clientConnectList.size();
    }
}
