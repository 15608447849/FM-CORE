package bottle.log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import static bottle.log.PrintLogThread.except;

public class WriteLogThread extends Thread {
    private static final LinkedBlockingQueue<String[]> writeFieContentQueue = new LinkedBlockingQueue<>();

    public static void addWriteFileQueue(String directory, String file, String content) {
        try {
            if (!content.endsWith("\n")){
                content+="\n";
            }
            writeFieContentQueue.add(new String[]{directory,file,content});
        } catch (Exception e) {
            except(e);
        }
    }

    // 对指定文件追加写入内容
    public static void appendContentToFile(String dictPath, String fileName, String content) {
        try {
            File dict = new File(dictPath);
            if (!dict.exists()){
                if (!dict.mkdirs()) return;
            }
            File file = new File(dict,fileName);
            try(FileOutputStream fos = new FileOutputStream(file, true)){
                try(FileChannel channel = fos.getChannel();){
                    byte[] data = content.getBytes(StandardCharsets.UTF_8);
                    ByteBuffer buf = ByteBuffer.wrap(data);
                    buf.put(data);
                    buf.flip();
                    channel.write(buf);
                }
            }
        } catch (Exception e) {
           except(e);
        }
    }

    private static final Thread thread = new Thread(() -> {
        while (true){
            try {
                String[] arr = writeFieContentQueue.take();
                appendContentToFile(arr[0],arr[1],arr[2]);
            } catch (Exception e) {
               except(e);
            }
        }
    });

    static {
        thread.setName("wlt-"+thread.getId());
        thread.setDaemon(true);
        thread.start();
    }

}
