package bottle.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServerUtil {




    private static String openurl(String urlstr) throws Exception {
        URL url = new URL(urlstr);
        URLConnection connection = url.openConnection();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                return inputLine;
            }
        }
        return null;
    }

    // 获取外网主机地址
    private static String localHostPublicNetIpAddr(int loop){
        String resp = "0.0.0.0";
        try {
//           String str = openurl("https://api.ipify.org");
           String str = openurl("http://checkip.amazonaws.com");
           if (str!=null) resp = str;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("获取外网主机地址错误:"+ e);
            if (e.getMessage().contains("Connection reset")){
                if (loop<3){
                    ThreadTool.threadSleep(10000);
                    return localHostPublicNetIpAddr(++loop);
                }
            }
        }
        return resp;
    }

    public static String localHostPublicNetIpAddr(){
        return localHostPublicNetIpAddr(0);
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
//        System.out.println(getLocalAddressBySocket());
        for (int i=0;i<100;i++){
            System.out.println(i+" " +localHostPublicNetIpAddr());
            ThreadTool.threadSleep(3000);
        }

    }

}
