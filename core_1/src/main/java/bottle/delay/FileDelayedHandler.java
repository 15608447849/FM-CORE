package bottle.delay;

import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import com.google.common.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;



public class FileDelayedHandler<T extends IDelayedObject> extends DelayedHandler<T>{

    private final String queueKey;
    private final File folder;
    public FileDelayedHandler(String queueKey, long delayTime, DelayedTimeType time_type, IDelayedObjectCallback<T> callback ) {
        super(delayTime, time_type, callback);
        this.queueKey = queueKey;
        folder = new File(System.getProperty("user.dir"),queueKey);

        if (!folder.exists()){
            if (!folder.mkdirs()){
                throw new RuntimeException("延迟队列文件缓存文件夹创建失败: "+ folder);
            }
        }

        loadFileData();
    }

    public FileDelayedHandler(String queueKey,IDelayedObjectCallback<T> callback) {
        this(queueKey,365, DelayedTimeType.DAY,callback);
    }



    private void loadFileData() {

        File [] valFiles = folder.listFiles();
        if (valFiles == null) return;

        for (File valFile : valFiles) {
            try {
                byte[] bytes = Files.readAllBytes(valFile.toPath());
                String content = new String(bytes, StandardCharsets.UTF_8);
                T bean = GoogleGsonUtil.jsonToJavaBean(content,new TypeToken<T>(){}.getType());
                add(bean);
            } catch (IOException e) {
                Log4j.error(e);
            }

        }

    }


    @Override
    protected void addSuccess(DelayedObjectWarp<T> delayed) {
        try {
            if (!folder.exists()){
                if (!folder.mkdirs()) return;
            }

            File file = new File(folder,delayed.getObj().getUnqKey());

            try(FileOutputStream fos = new FileOutputStream(file, false)){
                try(FileChannel channel = fos.getChannel();){
                    String content = GoogleGsonUtil.javaBeanToJson(delayed.getObj());
                    if (content == null) return;
                    byte[] data = content.getBytes(StandardCharsets.UTF_8);
                    ByteBuffer buf = ByteBuffer.wrap(data);
                    buf.put(data);
                    buf.flip();
                    channel.write(buf);
                }
            }
        } catch (Exception e) {
            Log4j.error(e);
        }
    }

    @Override
    protected void removeSuccess(DelayedObjectWarp<T> delayed) {
        try {
            File file = new File(folder,delayed.getObj().getUnqKey());
            if (file.exists()) file.delete();
        } catch (Exception e) {
            Log4j.error(e);
        }
    }



}
