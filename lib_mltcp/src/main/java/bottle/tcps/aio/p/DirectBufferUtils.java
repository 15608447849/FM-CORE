package bottle.tcps.aio.p;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

public class DirectBufferUtils {
    public static ByteBuffer createByteBuffer(int size){
        return ByteBuffer.allocateDirect(size);//申请堆外内存
    }

    public static void clean(final ByteBuffer byteBuffer) {
        byteBuffer.clear();
        if (byteBuffer.isDirect()) {
            ((DirectBuffer)byteBuffer).cleaner().clean();
        }
    }











}
