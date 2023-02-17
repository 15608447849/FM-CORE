package bottle.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServerUtil {

    // 获取外网主机地址
    public static String localHostPublicNetIpAddr(){
        String resp = HttpUtil.contentToHttpBody("https://ip.chinaz.com/","GET",null);
        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
        Matcher m = p.matcher(resp);
        if (m.find()) {
            return m.group(1);
        }
        return "0.0.0.0";
    }

    // 获取空闲端口
    public static int getFreePort()  {
        try (Socket socket = new Socket()) {
            InetSocketAddress inetAddress = new InetSocketAddress(0);
            socket.bind(inetAddress);
            return socket.getLocalPort();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
