import bottle.tcps.ftc.client.FtcBackupClient;

import java.io.File;
import java.net.InetSocketAddress;

public class FtcClientTest {
    public static void main(String[] args) {
        FtcBackupClient client  = new FtcBackupClient("D:\\ftcServer\\c",
                64,
                2000);
        client.addServerAddress(new InetSocketAddress("47.106.100.121",18001));
//        client.ergodicDirectory();
//        client.addBackupFile(new File("D:\\ftcServer\\c\\DiskGenius\\DiskGenius.exe"));
//        client.addBackupFile(new File("D:\\ftcServer\\c\\DiskGenius\\avcodec-54.dll"));
        File file = new File("D:\\ftcServer\\c\\media\\1.docx");
        client.addBackupFile(file);
        try {
            Thread.sleep(10000 * 60 * 60 * 24);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
