package bottle.tcps.aio.p;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by user on 2017/11/23.
 * 对接收的数据内容 拼接-处理-分发
 *
 */
class BufferReceiveHandler {

    private Session session;

    BufferReceiveHandler(Session session) {
        this.session = session;
    }

    void handlerBuffer(ByteBuffer buffer, Integer len) {
        try {
            buffer.flip();
            byte[] bytes = new byte[len];
            buffer.get(bytes,0,len);
            buffer.clear();
            // 拼接剩余数据拼接 -> 数据处理,协议解析
            handle(dataMosaic(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] ramBytes = null;
    //存入剩余数据
    private void storeRemainBytes(byte[] bytes,int offset) {
        int size = bytes.length-offset;
        ramBytes = new byte[size];
        //1.要拷贝复制的原始数据
        //2.原始数据的读取位置(从原始数据哪个位置开始拷贝)
        //3.存放要拷贝的原始数据的目的地
        //4.开始存放的位置()
        //5.要读取的原始数据长度(拷贝多长)
        System.arraycopy(bytes,offset,ramBytes,0,size);
        //System.out.println("存储数据,剩余大小: "+ size +" 位置: "+ offset);

//        if (offset<5000) System.out.println(Arrays.toString(ramBytes));
    }

    /**
     * 数据拼接
     */
    private byte[] dataMosaic(byte[] bytes) {
//        ("取出 : "+ buf);
        // 1 是否还有上一次剩余未处理的数据 , 有 - 拼接在此段数据前
        if (ramBytes!=null){
            //获取剩余数据,清空剩余数据
//            System.out.println("上次剩余数据大小: "+ ramBytes.length +
//                    " , 本次需要处理数据总大小: "+ (ramBytes.length + bytes.length));

            byte[] curBytes = new byte[ ramBytes.length + bytes.length];
            //1.要拷贝复制的原始数据
            //2.原始数据的读取位置(从原始数据哪个位置开始拷贝)
            //3.存放要拷贝的原始数据的目的地
            //4.开始存放的位置()
            //5.要读取的原始数据长度(拷贝多长)
            System.arraycopy(ramBytes,0,curBytes,0,ramBytes.length);
            System.arraycopy(bytes,0, curBytes, ramBytes.length, bytes.length);
            ramBytes = null;
            return curBytes;
        }
        return bytes;
    }


    /**
     * 解析协议
     * 对剩余数据进行存储
     * SOH + STX + 当前传输下标(4) + NUL +  内容类型(4位) + NUL + 内容长度(4位)  + NUL + 具体内容  + ETX + EOT
     */
    private void handle(byte[] bytes) {
        int offset=0;
        while (offset<bytes.length){
            try {
                Object[] arrays = Protocol.pauseProtocol(bytes,offset);
                int dataType = (int) arrays[0];
                byte[] data = (byte[]) arrays[1];
                int offsetLen = (int) arrays[2];
                //对指定类型进行判断
                toSessionHandle(dataType,data);
                //移动下标
                offset += offsetLen;
            } catch (Exception e) {
                //存入剩余
                storeRemainBytes(bytes,offset);
                return;
            }
        }
    }

    private void toSessionHandle(int dataType, byte[] data) {
        try {

            SocketImp socketImp = session.getSocketImp();
            if (dataType == Protocol.DATA_TYPE_STR){
                String message;
                try {
                    message = new String(data,Protocol.DEFAULT_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    message = new String(data);
                }
                socketImp.getAction().receiveString(session,message);
            }
            if (dataType == Protocol.DATA_TYPE_BYTE){
                socketImp.getAction().receiveBytes(session,data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(){
        session = null;
        if (ramBytes!=null) ramBytes = null;
    }

}
