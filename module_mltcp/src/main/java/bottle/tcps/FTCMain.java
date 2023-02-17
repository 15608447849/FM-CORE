package bottle.tcps;

import bottle.tcps.ftc.client.FtcBackupClient;
import bottle.tcps.ftc.server.FtcBackupServer;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * @Author: leeping
 * @Date: 2020/10/12 11:44
 */
public class FTCMain {
    public static void main(String[] args) {
        try{
            String type = args[0].toLowerCase();
            String fileDictPath = args[1];
            if (type.equals("client")){
                String toAddress = args[2];
                int toPoint = Integer.parseInt(args[3]);
                int max = 0;
                if (args.length>=5){
                    max = Integer.parseInt(args[4]);
                }


                FtcBackupClient client  = new FtcBackupClient(fileDictPath, max);
                client.addServerAddress(new InetSocketAddress(toAddress,toPoint));

                if (args.length>=6){
                    client.addBackupFile(new File(args[5]));
                }else{
                    client.ergodicDirectory();
                }

            }
            if (type.equals("server")){
                String locAddress = args[2];

                int loclPoint = Integer.parseInt(args[3]);
                new FtcBackupServer(fileDictPath, locAddress,loclPoint);

            }

        synchronized (args){
            args.wait();
        }
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
}
