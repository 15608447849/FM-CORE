package bottle.tcps.aio.p;


/**
 * Created by user on 2017/7/8.
 * ASCII 控制字符
 * bin     dec
 * 00000000	0		NUL(null)	空字符
 00000001	1		SOH(start of headling)	标题开始
 00000010	2		STX (start of text)	正文开始
 00000011	3		ETX (end of text)	正文结束
 00000100	4		EOT (end of transmission)	传输结束
 00000101	5		ENQ (enquiry)	请求
 00000110	6		ACK (acknowledge)	收到通知
 00000111	7		BEL (bell)	响铃
 00001000	8		BS (backspace)	退格
 00001001	9		HT (horizontal tab)	水平制表符
 00001010	10		LF (NL line feed, new line)	换行键
 00001011	11		VT (vertical tab)	垂直制表符
 00001100	12		FF (NP form feed, new page)	换页键
 00001101	13		CR (carriage return)	回车键
 00001110	14		SO (shift out)	不用切换
 00001111	15		SI (shift in)	启用切换
 00010000	16		DLE (data link escape)	数据链路转义
 00010001	17		DC1 (device control 1)	设备控制1
 00010010	18		DC2 (device control 2)	设备控制2
 00010011	19		DC3 (device control 3)	设备控制3
 00010100	20		DC4 (device control 4)	设备控制4
 00010101	21		NAK (negative acknowledge)	拒绝接收
 00010110	22		SYN (synchronous idle)	同步空闲
 00010111	23		ETB (end of trans. block)	传输块结束
 00011000	24		CAN (cancel)	取消
 00011001	25		EM (end of medium)	介质中断
 00011010	26		SUB (substitute)	替补
 00011011	27		ESC (escape)	溢出
 00011100	28		FS (file separator)	文件分割符
 00011101	29		GS (group separator)	分组符
 00011110	30		RS (record separator)	记录分离符
 00011111	31		US (unit separator)	单元分隔符
 01111111   127     DEL (delete)	删除
 */
public class Protocol {
    public static final byte NUL = 0;//空字符
    public static final byte SOH = 1;//传输开始
    public static final byte STX = 2;//正文开始
    public static final byte ETX = 3;//正文结束
    public static final byte EOT = 4;//传输结束
    public static final byte ENQ = 5;//请求
    public static final byte ACK = 6;//收到通知
    public static final byte BEL = 7;
    public static final byte BS = 8;
    public static final byte HT = 9;
    public static final byte LF = 10;//换行
    public static final byte VT = 11;
    public static final byte FF = 12;
    public static final byte CR = 13;//回车键
    public static final byte SO = 14;
    public static final byte SI = 15;
    public static final byte DLE = 16;
    public static final byte DC1 = 17;
    public static final byte DC2 = 18;
    public static final byte DC3 = 19;
    public static final byte DC4 = 20;
    public static final byte NAK = 21;//拒绝接收
    public static final byte SYN = 22;//同步空闲
    public static final byte ETB = 23;//传输块结束
    public static final byte CAN = 24;//取消
    public static final byte EM = 25;
    public static final byte SUB = 26;
    public static final byte ESC = 27;
    public static final byte FS = 28;//文件分割符
    public static final byte GS = 29;
    public static final byte RS = 30;
    public static final byte US = 30;
    public static final byte DEL = 127;


    public static String DEFAULT_CHARSET = "utf-8";

    public static final int DATA_TYPE_NODE = 0;// 未定义
    //字符串
    public static final int DATA_TYPE_STR = 9001;
    //字节流
    public static final int DATA_TYPE_BYTE = 9003;

    // 协议大小
    public static final int PROTOCOL_BIT_SIZE = 1 + 4 + 4 + 4 + 1 + 1 + 1;

    private static String getBit(byte by){
        StringBuffer sb = new StringBuffer();
        sb.append((by>>7)&0x1)
                .append((by>>6)&0x1)
                .append((by>>5)&0x1)
                .append((by>>4)&0x1)
                .append((by>>3)&0x1)
                .append((by>>2)&0x1)
                .append((by>>1)&0x1)
                .append((by>>0)&0x1);
        return sb.toString();
    }

    private static byte[] stringToAscii(String string) {
            byte[] bytes = new byte[string.length()];
            for(int i = 0; i < string.length();i++){
                bytes[i] = (byte) string.charAt(i);
            }
            return bytes;
    }

    private static String asciiToString(byte[] ascii){
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : ascii) {
            stringBuilder.append((char) b);
        }
        return stringBuilder.toString();
    }

    //占4位 int转数组
    private static byte[] intToByteArray(int num) {
        return new byte[] {
                (byte) ((num >> 24) & 0xFF),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) (num & 0xFF)
        };
    }
    //4位数组转int值
    static int byteArrayToInt(byte[] dataBytes, int offset) {

        return   dataBytes[offset+3] & 0xFF |
                (dataBytes[offset+2] & 0xFF) << 8 |
                (dataBytes[offset+1] & 0xFF) << 16 |
                (dataBytes[offset] & 0xFF) << 24;
    }

    static final int BUFFER_BLOCK_SIZE =   4 * 1024 * 1024 + PROTOCOL_BIT_SIZE ; // 4M 缓冲区内存大小

    //通过数据字节复制
    //1.要拷贝复制的原始数据
    //2.原始数据的读取位置(从原始数据哪个位置开始拷贝)
    //3.存放要拷贝的原始数据的目的地
    //4.开始存放的位置()
    //5.要读取的原始数据长度(拷贝多长)


    // 协议 SOH + 当前传输下标(4) + 内容类型(4位) + 内容长度(4位)  +  STX  + 具体内容  + ETX + EOT
    // 1 + 4 + 4 + 4 + 1 ... + 2
    public static byte[] protocol(int dataType,byte[] dataBytes,int dataOffset,int dataLength) {
        if (dataBytes == null || dataLength-dataOffset<=0) return null;
        byte[] bytes = new byte[PROTOCOL_BIT_SIZE + dataLength];

        //传输开始 1
        bytes[0] = SOH;

        //发送下标 4
        System.arraycopy(intToByteArray(0),0 , bytes,1, 4 );
        //数据类型 4
        System.arraycopy(intToByteArray(dataType),0 , bytes,1+4, 4 );
        //内容长度 4
        System.arraycopy(intToByteArray(dataLength),0 , bytes,1+4+4, 4 );
        //正文开始 1
        bytes[1+4+4+4] = STX;

        //具体内容
        System.arraycopy(dataBytes,dataOffset , bytes,1+4+4+4+1,dataLength);
        //正文结束
        bytes[1+4+4+4+1+dataLength] = ETX;
       //传输结束
        bytes[bytes.length-1] = EOT;
//        System.out.println(Arrays.toString(bytes));
        //System.out.println("发送" + index + " 数据大小: " + dataLength+" 实际包大小: "+ bytes.length);
        return bytes;
    }

    // 0-数据类型, 1-数据字节数组, 2-偏移量
    static Object[] pauseProtocol(byte[] bytes, int offset) throws Exception {

        //1. 确定协议头标识符号是否存在
        byte SOH = bytes[offset];
        byte STX = bytes[offset+ PROTOCOL_BIT_SIZE-3];

        if (! (SOH == Protocol.SOH && STX == Protocol.STX) ){
            // 不存在协议标识符
            throw new IllegalArgumentException("协议不匹配");
        }
        //2. 取出内容类型及内容长度
        int sendIndex =  Protocol.byteArrayToInt(bytes,offset + 1);// 获取发送下标
        int dataType = Protocol.byteArrayToInt(bytes,offset + 1 + 4 );// 获取内容类型
        int dataLength = Protocol.byteArrayToInt(bytes,offset + 1 + 4 + 4);// 获取内容长度

        //3. 取出内容
        //通过数据字节复制
        //1.要拷贝复制的原始数据
        //2.原始数据的读取位置(从原始数据哪个位置开始拷贝)
        //3.存放要拷贝的原始数据的目的地
        //4.开始存放的位置()
        //5.要读取的原始数据长度(拷贝多长)
        byte[] data = new byte[dataLength];
        //System.out.println("原始数据 " +(offset + PROTOCOL_BIT_SIZE - 2)+" 开始,转移数据: " +dataLength +" , 数据总长度: "+ bytes.length+", 目标总长度: "+ data.length );
        if (dataLength>bytes.length) throw new IllegalArgumentException("数据不完整, 源数据长度("+bytes.length+") 数据需要接收长度: "+ dataLength);
        System.arraycopy(bytes,offset + PROTOCOL_BIT_SIZE - 2 , data,0, dataLength );

        //4 获取结束符号
        byte ETX = bytes[offset + PROTOCOL_BIT_SIZE - 2 + dataLength ];
        byte EOT = bytes[offset + PROTOCOL_BIT_SIZE - 1 + dataLength ];

        /*System.out.println(
                "协议头: "+ SOH+" "+ STX + " , 协议尾: "+ETX+ " "+EOT
                        +" , 下标: "+sendIndex +" , 长度: "+dataLength
        );*/

        if (!(ETX == Protocol.ETX && EOT == Protocol.EOT)){

            for (int index = offset + PROTOCOL_BIT_SIZE - 2 ; index<bytes.length;index++){
                byte item = bytes[index];
                if (item == Protocol.SOH){
                    byte item_1 = bytes[index+ PROTOCOL_BIT_SIZE-3];
                    if (item_1 == Protocol.STX){
                         System.out.println("数据丢弃>>"
                        //+ " , 协议头: "+ SOH+" "+ STX
                        +" , 下标: "+sendIndex
                        +" , 长度: "+dataLength
                        //+ " , 协议尾: "+ETX+ " "+EOT
                        +" , 实际接收长度: "+(index-offset)
                        );
                        //System.out.println("找到下一个传输开始符 , 位置: "+ index+" 找到下一个正文开始符 , 位置: "+ (index+ PROTOCOL_BIT_SIZE-3));
                        //System.out.println("当前内容 实际长度: "+ (index-offset));
//                        byte[] errorData = new byte[index-offset];

                        return new Object[]{0,null,index-offset};//丢弃数据
                    }
                }
            }

            throw new IllegalArgumentException("数据损坏");
        }
        Object[] arrays = new Object[3];
        arrays[0] = dataType;
        arrays[1] = data;
        arrays[2] = PROTOCOL_BIT_SIZE + dataLength;
        return arrays;
    }





}
