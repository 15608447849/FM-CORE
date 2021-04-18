import Ice.Connection;
import Ice.ConnectionCallback;
import Ice.Current;
import bottle.util.TimeTool;
import com.onek.server.inf.InterfacesPrx;
import com.onek.server.inf._PushMessageClientDisp;
import framework.client.IceClient;
import java.util.Date;

public class LongConnectTest {

    public static void main(String[] args) throws Exception {
        for(int i = 0; i< 3000; i++){
            final String _id = "100000"+i;
            new Thread(() -> exe(_id)).start();
//            Thread.sleep(10);
        }

        synchronized (Thread.currentThread()){
            Thread.currentThread().wait();
        }
        System.out.println("****");

//        exe("100091111");
    }

    private static void exe(String name){
        IceClient client = new IceClient("DRUG","tcp:reg.onekdrug.com:4161;tcp:ice.onekdrug.com:4161",
                "idleTimeOutSeconds=300,--Ice.MessageSizeMax=8192");
        client.startCommunication();
        try {
            startLongConnect(client,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.stopCommunication();
    }
    private static void startLongConnect(IceClient client,String connectName){

        String fn = "order2Service0_1";

        InterfacesPrx prx = client.getProxy(fn);
        System.out.println(Thread.currentThread() + " > "+ connectName);

        Ice.Identity identity = new Ice.Identity(connectName,"TEST-"+connectName);

        client.getLocalAdapter().add(new _PushMessageClientDisp() {
            @Override
            public void receive(String message, Current __current) {
                System.out.println(TimeTool.date_yMd_Hms_2Str(new Date())+" "+connectName+" 接收: "+ message);
            }
        }, identity);
        prx.ice_getConnection().setAdapter(client.getLocalAdapter());
        prx.ice_getConnection().setCallback(new ConnectionCallback() {
            @Override
            public void heartbeat(Connection con) {
                System.out.println(TimeTool.date_yMd_Hms_2Str(new Date())+" "+connectName+" heartbeat:"+ con);
            }
            @Override
            public void closed(Connection con) {
                System.out.println(TimeTool.date_yMd_Hms_2Str(new Date())+" "+connectName+ " closed:"+ con);
            }
        });
        prx.online( identity );
        System.out.println(TimeTool.date_yMd_Hms_2Str(new Date())+" CONNECT SUCCESS");

        while (true){
            try {
                prx.ice_ping();
                Thread.sleep(30000);
                System.out.println(TimeTool.date_yMd_Hms_2Str(new Date())+" "+connectName+ " ping ok");
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        client.getLocalAdapter().remove(identity);

    }
}
