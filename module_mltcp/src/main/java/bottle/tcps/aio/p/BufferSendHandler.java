package bottle.tcps.aio.p;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public class BufferSendHandler {
    private Session session;
    BufferSendHandler(Session session) {
        this.session = session;
    }

    public void sendTo(byte[] bytes) {
        SocketImp socketImp = session.getSocketImp();
        ByteBuffer sendBuffer = DirectBufferUtils.createByteBuffer(Protocol.BUFFER_BLOCK_SIZE);
        try {
//            System.out.println("需要发送总数据大小: "+ bytes.length);
            // 计算分段数
            int diff = bytes.length / Protocol.BUFFER_BLOCK_SIZE;
            int index = 0;
            while (index<diff){
                sendFunction(socketImp,bytes,index * Protocol.BUFFER_BLOCK_SIZE,Protocol.BUFFER_BLOCK_SIZE,sendBuffer);
                index++;
            }
            int ram = bytes.length % Protocol.BUFFER_BLOCK_SIZE;
            if (ram>0){
                sendFunction(socketImp,bytes,index * Protocol.BUFFER_BLOCK_SIZE,ram,sendBuffer);
            }

        } catch (Exception e) {
            //发送数据异常
            socketImp.getAction().error(session,null,e);
            socketImp.getAction().connectClosed(session);
        }
        DirectBufferUtils.clean(sendBuffer);
    }

    private void sendFunction(SocketImp socketImp, byte[] bytes, int index, int length, ByteBuffer buffer) throws Exception{
        buffer.clear();
        buffer.put(bytes,index,length);
        buffer.flip();
        Future<Integer> future =  socketImp.getSocket().write(buffer); //发送消息到管道
        while(!future.isDone());
        int sendLen = future.get();

        //对比实发大小处理
        if (sendLen<length){
            System.out.println("已发送 数据信息,  起点: "+ index +" ,数据长度: "+length + ", 实际发送大小: "+ sendLen);
            //对剩余未发送成功部分进行发送
            int _offset = index+sendLen;
            int _len = length-sendLen;
            sendFunction(socketImp,bytes,_offset,_len,buffer);
        }

    }


    public void close() {
        session = null;
    }
}
