
import bottle.ftp.FtpClientI;
import bottle.ftp.FtpInfo;
import bottle.ftp.LeeFTPManager;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ClientInfoStatus;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2020/5/25 18:15
 */
public class ftptest {
    public static void main(String[] args) throws Exception{

        List<String> list = LeeFTPManager.query("10.12.0.241",21,"lzp","123456", "");
        System.out.println(list);
//
//        try(FileOutputStream fout = new FileOutputStream("C:\\Users\\user\\Desktop\\7119_P_20200604000000_20200603.xls")){
//            // 下载文件
//            LeeFTPManager.download("10.12.0.241",21,"lzp","123456", "/7119_P_20200604000000_20200603.xls", fout);
//        }

        LeeFTPManager.stop();
    }
}
