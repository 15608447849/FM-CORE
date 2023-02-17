package jdbc.imp;


/**
 * @Author: leeping
 * @Date: 2020/4/29 16:57
 */
public class GenUniqueID {

    private volatile long lastTimestampSec = 0L;
    private volatile int sequence = 0;

    private final boolean isMillisecond;

    public GenUniqueID(boolean isMillisecond) {
        this.isMillisecond = isMillisecond;
    }

    private long getCurrentTimeMillis(){
        return isMillisecond ? System.currentTimeMillis() : System.currentTimeMillis() / 1000L; // 转秒
    };

    /**
     * 根据时间戳生成唯一ID
     */
    public synchronized String currentTimestampString(boolean addTimestamp,boolean addSeq) {
        long curTimestampSec = getCurrentTimeMillis();
        long diff = lastTimestampSec - curTimestampSec; //时间差
        if (diff > 0) {
            throw new RuntimeException(String.format("当前时间戳比最后时间戳小 %d!", diff));
        }else{
            if (lastTimestampSec == curTimestampSec) {
                // 当前毫秒内，则+1
                sequence++;
                if (sequence > 999) {
                    while (getCurrentTimeMillis() == curTimestampSec);// 等待下一秒;
                    return currentTimestampString(addTimestamp,addSeq);
                }

            } else {
                //进入下一秒
                sequence = 0;
                lastTimestampSec = curTimestampSec;
            }
        }


        StringBuilder sb  = new StringBuilder();
        if (addTimestamp){
            sb.append(String.format("%d", lastTimestampSec));
        }
        if (addSeq){
           sb.append(String.format("%03d",sequence));
        }
        return sb.toString();
    }

    public long currentTimestampLong(){
        return Long.parseLong(currentTimestampString(true,true));
    }

    public long currentSequenceLong(){
        return Long.parseLong(currentTimestampString(false,true));
    }


    public static final GenUniqueID secondID = new GenUniqueID(false);
    public static final GenUniqueID milliSecondID = new GenUniqueID(true);

//    public static void main(String[] args) {
//        GenUniqueID genUniqueID = new GenUniqueID(true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    System.out.println(genUniqueID.currentTimestampLong());
//                }
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    System.out.println(genUniqueID.currentTimestampLong());
//                }
//            }
//        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    System.out.println(genUniqueID.currentSequenceLong());
//                }
//            }
//        }).start();
//    }

}
