package framework.server;
import bottle.util.GoogleGsonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: leeping
 * @Date: 2019/12/30 11:33
 */
public class IPMessage {
    private static  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long id;
    String identityName;
    String content;
    String time;

    private IPMessage(){ }

    static IPMessage create(String identityName,String message){
        IPMessage msg = new IPMessage();
        msg.id = 0;
        msg.identityName = identityName;
        msg.content = message;
        msg.time = simpleDateFormat.format(new Date());
        return msg;
    }

    public static IPMessage create(long id,String identityName,String message,String time){
        IPMessage msg = new IPMessage();
        msg.id = id;
        msg.identityName = identityName;
        msg.content = message;
        msg.time = time;
        return msg;
    }

    public long getId() {
        return id;
    }

    public String getIdentityName() {
        return identityName;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return GoogleGsonUtil.javaBeanToJson(this);
    }
}
