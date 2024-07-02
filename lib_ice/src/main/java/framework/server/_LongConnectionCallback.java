package framework.server;

import Ice.*;
import bottle.util.Log4j;
import bottle.util.TimeTool;
import com.onek.server.inf.PushMessageClientPrx;

import java.lang.Exception;

import static framework.server.IMServerImps.FLAG;

class _LongConnectionCallback implements ConnectionCallback {

    private final String clientType;
    private final String identityName;
    final PushMessageClientPrx clientPrx;//客户端
    private final String connectionInfo;//连接信息
    private final long connectStartTime; //开始时间
    private final ClosedCallback callback;

    private volatile boolean isClosed = false;

    _LongConnectionCallback(String clientType, String identityName, PushMessageClientPrx clientPrx, Connection connection,ClosedCallback callback) {
        this.connectStartTime = System.currentTimeMillis();
        this.clientType = clientType;
        this.identityName = identityName;
        this.clientPrx = clientPrx;
        this.connectionInfo = connection.toString().split("\n")[1];
        connection.setCallback(this);
        //ACM心跳机制

//        CloseOff(0) 防止服务器关闭传入连接，而不管连接空闲了多长时间
//        CloseOnIdle(1)
//        CloseOnInvocation(2)
//        CloseOnInvocationAndIdle(3)
//        CloseOnIdleForceful(4)
//
//        HeartbeatOff(0) 关闭心跳
//        HeartbeatOnInvocation(1)
//        HeartbeatOnIdle(2)
//        HeartbeatAlways(3)

        connection.setACM(
                new Ice.IntOptional(10),
                new  Ice.Optional<>(ACMClose.CloseOff),
                new  Ice.Optional<>(ACMHeartbeat.HeartbeatAlways)
        );


        this.callback = callback;
    }
    @Override
    public void heartbeat(Connection con) {
//        Log4j.info(FLAG+ "[heartbeat]\t"  + clientType+"," + identityName + "," +  con.toString().replace("\n","<->")
//                + " ,"+ TimeTool.getConnectedDurationHumStr(System.currentTimeMillis() - connectStartTime));
    }
    @Override
    public void closed(Connection con) {
        Log4j.info(FLAG+ "[closed]\t"  + clientType+" , " + identityName + " , " +  con.toString().replace("\n","<->")
                + " ,"+ TimeTool.getConnectedDurationHumStr(System.currentTimeMillis() - connectStartTime));
        closeConnected();
    }

    private void closeConnected() {
        if (!isClosed){
            this.isClosed = true;
            if (callback!=null) callback.closed(this,clientType,identityName,clientPrx,connectionInfo,connectStartTime);
        }
    }

    boolean sendMessage(String _message){
        try {
            if (isClosed) Log4j.info(FLAG+" 连接已关闭,发送失败, "+ clientType+"/"+ identityName + "\t" + _message);

            if (!isClosed){
                clientPrx.ice_ping();
                clientPrx.receive(_message);
                return true;
            }
        } catch (Exception e) {
            closeConnected();
            String info = FLAG + "发送失败," + clientType+"/"+ identityName + "\t" + _message;
            Log4j.info(info);
            if (e instanceof RequestFailedException){
                Log4j.info(info);
            }else{
                Log4j.error(info,e);
            }
        }
        return false;
    }

    public interface ClosedCallback {
        void closed(_LongConnectionCallback callback,String clientType,String identityName,PushMessageClientPrx clientPrx,String connectionInfo,long connectStartTime);
    }

}
