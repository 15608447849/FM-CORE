package bottle.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**字符串为空*/
    public static boolean isEmpty(String str){
        return str == null || str.trim().length() == 0 ;
    }

    /**判断数组为空*/
    public static boolean isEmptyArrays(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    /**字符串不为空*/
    public static boolean isNotEmpty(String str) {
        return !StringUtil.isEmpty(str);
    }

    /**判断一组字符串有一个为空 true*/
    public static boolean isEmpty(String... arr){
        for (String str : arr){
            if (isEmpty(str)) return true;
        }
        return false;
    }

    /** 去除空格 */
    public static String trim(String text) {
        if(text == null || "".equals(text)) {
            return text;
        }
        return text.trim();
    }

    /**
     * 比较两个字符串（大小写敏感）。
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }


    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtil.isBlank(cs);
    }

    /**
     * @param content 字符串
     * @param charset 字符集
     */
    public static byte[] getContentBytes(String content, String charset) {
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
        }
        return null;
    }

    //错误输出
    public static String printExceptInfo(Throwable ex){
        Writer writer = new StringWriter();
        try(PrintWriter printWriter = new PrintWriter(writer)){
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
        }
        return writer.toString();
    }

    //获取本机所有IP地址
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }

    /**
     * 字符串是否包含中文
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\(|\\)|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】|\\（|\\）|\\—|\\+|\\…|\\‘|\\{|\\}]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean isEmail(String str) {
        return !isEmpty(str) && Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", str);
    }


}
