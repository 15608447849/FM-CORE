import bottle.tcps.aio.p.DirectBufferUtils;

import java.nio.ByteBuffer;

/**
 * @Author: leeping
 * @Date: 2020/10/12 17:45
 */
public class tt {
    public static void main(String[] args) throws Exception{
        byte[] bytes = new byte[10];
        for (int i = 0; i<bytes.length;i++){
            bytes[i] = (byte)127;
        }
        int offset = 2;
        int size = bytes.length - offset;

        ByteBuffer ramContentBuffer = DirectBufferUtils.createByteBuffer(size);

        ramContentBuffer.clear();
        ramContentBuffer.put(bytes,offset,size);
    }
}
