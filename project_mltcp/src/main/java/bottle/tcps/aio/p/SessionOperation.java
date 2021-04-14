package bottle.tcps.aio.p;

/**
 * Created by user on 2017/11/22.
 */
public class SessionOperation {

    private final Session session ;
    SessionOperation(Session session) {
        this.session = session;
    }

    /**
     *
     * message 字符串消息
     * message 解码格式,默认UTF-8
     *
     */

    public void writeString(String message){

        try {
            byte[] data = message.getBytes(Protocol.DEFAULT_CHARSET);
            if(data.length == 0) return;
            session.send(Protocol.protocol( Protocol.DATA_TYPE_STR,data,0,data.length));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    /**
     * bytes 字节数组
     */
    public void writeBytes(byte[] bytes,int offset,int length){ //偏移量,长度
        session.send(Protocol.protocol(Protocol.DATA_TYPE_BYTE,bytes,offset,length));
    }
}
