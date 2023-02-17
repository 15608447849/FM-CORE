package bottle.util;


import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@PropertiesFilePath("/mail.properties")
public class EmailUtils {
    //发件人地址
    @PropertiesName("mail.senderAddress")
    public static String senderAddress;
    //发件人账户名
    @PropertiesName("mail.senderAccount")
    public static String senderAccount;
    //发件人授权码
    @PropertiesName("mail.senderPassword")
    public static String senderPassword;
    //发件人姓名
    @PropertiesName("mail.senderName")
    public static String senderName;
    //用户认证方式
    @PropertiesName("mail.smtp.auth")
    public static String smtp_auth;
    //传输协议
    @PropertiesName("mail.transport.protocol")
    public static String transport_protocol;
    //发件人的SMTP服务器地址
    @PropertiesName("mail.smtp.host")
    public static String smtp_host;
    //发件人的SMTP服务器地址
    @PropertiesName("mail.smtp.port")
    public static int smtp_port = 465;
    //打印发送日志
    @PropertiesName("mail.debug_mode")
    public static boolean debug_mode;

    static {
        ApplicationPropertiesBase.initStaticFields(EmailUtils.class);
    }

    public interface Attach {
        DataSource getDataSource();
        String getName();
    }

    public static final class ByteStreamAttach implements Attach {
        private final byte[] streamSource;
        private final String streamType;
        private final String name;

        public ByteStreamAttach(byte[] streamSource, String streamType, String name) {
            this.streamSource = streamSource;
            this.streamType = streamType;
            this.name = name;
        }

        @Override
        public DataSource getDataSource() {
            return new ByteArrayDataSource(streamSource,streamType);
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static final class UrlAttach implements Attach {
        private final URL url;
        private final String name;

        public UrlAttach(String name,String url) throws MalformedURLException {
            this.name = name;
            this.url = URI.create(url).toURL();
        }

        @Override
        public DataSource getDataSource() {
            return new URLDataSource(this.url);
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static boolean send(String subject, String content,List<? extends Attach> attaches,String... targetMails) {
        if (StringUtil.isEmptyArrays(targetMails)) {
            return false;
        }
        // 处理收件人地址
        List<InternetAddress> toAddressList = new ArrayList<>();
        for (String targetMail : targetMails) {
            if (!StringUtil.isEmail(targetMail)) continue;
            try {
                toAddressList.add(new InternetAddress(targetMail));//收件人
            } catch (AddressException e) {
                Log4j.error("邮件 地址("+targetMail+")错误", e);
            }
        }

        if (toAddressList.isEmpty()) return false;

        try {
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", smtp_auth);
            properties.setProperty("mail.transport.protocol", transport_protocol);
            properties.setProperty("mail.smtp.host", smtp_host);
            properties.setProperty("mail.smtp.port", String.valueOf(smtp_port));
            properties.setProperty("mail.smtp.socketFactory.port", String.valueOf(smtp_port));
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.ssl.enable", "true");

            Session session= Session.getInstance(properties);
            session.setDebug(debug_mode);

            //设置内容
            MimeMessage msg = new MimeMessage(session);
            //发件人
            msg.setFrom(new InternetAddress(senderAddress,senderName));

            //收件人
            InternetAddress[] internetAddresses = new InternetAddress[toAddressList.size()];
            for (int i=0;i<toAddressList.size();i++){
                internetAddresses[i] = toAddressList.get(i);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, internetAddresses);
            //主题
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());

            Multipart multipart = new MimeMultipart();

            if (content!=null && content.length()>0){
                //添加文本
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(content, "text/html;charset=utf-8");
                multipart.addBodyPart(messageBodyPart);
            }

            if (attaches!=null && attaches.size()>0) {
               // 添加附件
                for (Attach attach : attaches) {
                    try {
                        DataSource dataSource = attach.getDataSource();
                        String name = attach.getName() != null ? attach.getName() : dataSource.getName();
                        MimeBodyPart mailArchive = new MimeBodyPart();
                        mailArchive.setDataHandler(new DataHandler(dataSource));
                        mailArchive.setFileName(MimeUtility.encodeText(name, "UTF-8", null));
                        multipart.addBodyPart(mailArchive);
                    } catch (Exception e) {
                        Log4j.error("邮件 附件错误", e);
                    }
                }
            }

            msg.setContent(multipart);

            // 发送
            try(Transport transport = session.getTransport()){
                transport.connect(smtp_host,senderAccount, senderPassword);
                transport.sendMessage(msg, msg.getAllRecipients());
            }

        } catch (Exception e) {
            Log4j.error("邮件 发送错误", e);
            return false;
        }

        return true;
    }

    /** 无附件 */
    public static boolean sendNoAttach(String subject, String content,String... targetMails) {
        return send(subject,content,null,targetMails);
    }

    /** 无附件并且发送内容给自己邮箱 */
    public static boolean sendNoAttachToSelf(String subject, String content) {
        return sendNoAttach(subject,content,senderAddress);
    }

    public static void main(String[] args) throws MalformedURLException {
        List<Attach> attaches = new ArrayList<>();
        attaches.add(new UrlAttach("附件1","https://file.onek56.com/static/pc_login.mp4"));
//        System.out.println(sendNoAttach("科技部测试邮箱是否可用",".李世平大帅哥156","793065165@qq.com"));
        System.out.println(send("科技部测试邮箱是否可用","<html><body><image>https://www.onekdrug.com/_nuxt/img/3d934c7.jpg</image></body></htmp> ",attaches,"793065165@qq.com"));
//        System.out.println(sendSelf("科技部测试邮箱是否可用",".李世平大帅哥"));
    }

}
