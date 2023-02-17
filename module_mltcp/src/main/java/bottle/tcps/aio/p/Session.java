package bottle.tcps.aio.p;



import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


/**
 * Created by user on 2017/7/8.
 * 通讯会话
 */
public abstract class Session implements CompletionHandler<Integer, ByteBuffer>{

    private final FtcTcpAioManager ftcTcpManager;
    private final SocketImp socketImp;

    private final SessionOperation operation =  new SessionOperation(this);
    private final BufferSendHandler sendBufferHandler = new BufferSendHandler(this);
    private final BufferReceiveHandler receiveBufferHandler = new BufferReceiveHandler(this); //接收内容处理者

    public Session(FtcTcpAioManager manager, SocketImp connect) {
        this.ftcTcpManager = manager;
        this.socketImp = connect;
    }

    //系统读取到信息 回调到这里
    @Override
    public void completed(Integer integer, ByteBuffer buffer) {

        if (buffer!=null && socketImp.isAlive()) {
            if (integer == -1){
                //一个客户端 连接异常
                socketImp.getAction().error(this,null, new SocketException("socket connect is closed."));
                socketImp.getAction().connectClosed(this);
                return;
            }
            if (integer > 0){
                receiveBufferHandler.handlerBuffer(buffer,integer);
            }
            DirectBufferUtils.clean(buffer);
        }
        read();
    }

    @Override
    public void failed(Throwable throwable, ByteBuffer buffer) {
        //读取数据异常
        socketImp.getAction().error(this,throwable,null);
        socketImp.getAction().connectClosed(this);
    }

    /**
     * 读取数据(异步)
     */
    public void read(){
        if (socketImp.isAlive()){
            ByteBuffer reviewBuffer = DirectBufferUtils.createByteBuffer(Protocol.BUFFER_BLOCK_SIZE);
            reviewBuffer.clear();
            socketImp.getSocket().read(reviewBuffer, reviewBuffer,this);//系统从管道读取数据
        }else{
            socketImp.getAction().error(this,null, new SocketException("socket connect is closed."));
        }
    }

    /**
     * 发送数据(同步-堵塞)
     */
    public void send(byte[] bytes) {
        if (bytes != null && socketImp.isAlive()){ sendBufferHandler.sendTo(bytes); }
    }
    public void clear(){
        sendBufferHandler.close();
        receiveBufferHandler.close();
    }
    public void close(){
        //清理
        clear();
        //关闭管道
        socketImp.close();
    }

    public SessionOperation getOperation(){return operation;}

    public AsynchronousSocketChannel getSocket(){
        return socketImp.getSocket();
    }

    public SocketImp getSocketImp(){ return socketImp; }
}
