package bottle.util;

import java.net.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServerUtil {

    // 获取外网主机地址
    public static String localHostPublicNetIpAddr(){
        try {
            String resp = HttpUtil.contentToHttpBody("https://ip.chinaz.com/","GET",null);
            Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
            Matcher m = p.matcher(resp);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception ignored) {}
        return "0.0.0.0";
    }

    // 获取空闲端口
    public static int getFreePort()  {
        try (Socket socket = new Socket()) {
            InetSocketAddress inetAddress = new InetSocketAddress(0);
            socket.bind(inetAddress);
            return socket.getLocalPort();
        } catch (Exception ignored) {}
        return 0;
    }

    public static String getLocalAddressBySocket()  {
        try {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), getFreePort());
                if (socket.getLocalAddress() instanceof Inet4Address) {
                    Optional<Inet4Address> localAddress = Optional.of((Inet4Address) socket.getLocalAddress());
                    return localAddress.get().getHostAddress();
                }
            }

        } catch (Exception ignored) {}
        return "127.0.0.1";
    }

    public static void main(String[] args) {
        System.out.println(getLocalAddressBySocket());
    }

}
