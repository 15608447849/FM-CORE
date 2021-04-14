package framework.server;

import Ice.*;
import bottle.threadpool.IOThreadPool;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import com.onek.server.inf.*;


import java.lang.Exception;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import static framework.server.IMServerImps.ClientAttribute.*;

/**
 * @Author: leeping
 * @Date: 2019/4/9 17:57
 * 消息推送 服务端实现
 */
public class IMServerImps extends _InterfacesDisp implements IPersistentMessage {

    private static final String FLAG = "【长连接服务】 ";

//    public static boolean isAllowKillClientConnected = true;

    /* 客户端属性 */
    static final class ClientAttribute implements ConnectionCallback{

        /* 客户端连接,连接属性 */
        private final static ConcurrentHashMap<PushMessageClientPrx,ClientAttribute> clientAttrMap = new ConcurrentHashMap<>();

        synchronized static void addAttr(PushMessageClientPrx prx){
            if (prx!=null){
                ClientAttribute attribute = new ClientAttribute(prx);
                clientAttrMap.put(prx,attribute);
            }

        }

        synchronized static void removeAttr(PushMessageClientPrx prx){
           if (prx!=null){
               ClientAttribute attr = clientAttrMap.remove(prx);
               if (attr!=null){
                   attr.clientPrxRef = null;
               }
           }
        }

        synchronized static ClientAttribute getAttr(PushMessageClientPrx prx){
            if (prx!=null) return clientAttrMap.get(prx);
            return null;
        }

        // 时间格式化
        private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        private SoftReference<PushMessageClientPrx> clientPrxRef;

        private final long connectStartTime; //开始时间

        private String addressInfo; //地址信息

        private long lastHeartbeatTimestamp;// 最后心跳时间

        private volatile boolean invalid = false;// 连接无效

        private ClientAttribute(PushMessageClientPrx prx) {
            if (prx.ice_getConnection() != null) {
                try {
                    addressInfo = prx.ice_getConnection().toString().split("\n")[1].replace("remote address =","").trim();
                } catch (Exception e) {
                    addressInfo = "获取信息失败";
                }
            }

            //设置心跳监听
            prx.ice_getConnection().setCallback(this);

            //启动心跳
            prx.ice_getConnection().setACM(
                    new Ice.IntOptional( 10 ),
                    new  Ice.Optional<>(ACMClose.CloseOnIdleForceful),
                    new  Ice.Optional<>(ACMHeartbeat.HeartbeatAlways)
            );

            this.clientPrxRef = new SoftReference<>(prx);
            this.connectStartTime = System.currentTimeMillis();
        }


        @Override
        public void heartbeat(Connection con) {
            System.out.println(FLAG+ "ice connection heartbeat : "+ con.toString().replace("\n"," <-> "));
            lastHeartbeatTimestamp = System.currentTimeMillis();
            this.invalid = false;
        }

        @Override
        public void closed(Connection con) {
            System.out.println(FLAG+ "ice connection closed : "+ con.toString().replace("\n"," <-> "));
            this.invalid = true;
            if (clientPrxRef!=null && clientPrxRef.get()!=null){
                closeClient(clientPrxRef.get());
            }
        }

        // 获取连接时间
        public String getConnectStartTime(){
            return format.format(new Date(this.connectStartTime));
        }

        // 获取连接时长
        public long getConnectedDuration(){
            return System.currentTimeMillis() - connectStartTime;
        }

        // 连接时间格式化显示
        public String getConnectedDurationHumStr(){
            long second = getConnectedDuration()/1000L;
            long days = second /86400;//转换天数
            second = second %86400;//剩余秒数
            long hours = second /3600;//转换小时数
            second = second %3600;//剩余秒数
            long minutes = second /60;//转换分钟
            second = second %60;//剩余秒数
            if (0 < days){
                return days +":"+hours+":"+minutes+":"+second;
            }else{
                return hours+":"+minutes+":"+second;
            }
        }

    }

    //超时时间毫秒数
    private final static int PING_TIMEOUT_MAX = 30 * 1000;

    //可重入锁
    private final ReentrantLock lock = new ReentrantLock();

    //当前在线的所有客户端 <客户端类型<客户端标识,相同客户端列表>>
    private final HashMap<String,HashMap<String, ArrayList<PushMessageClientPrx>>> onlineClientMaps = new HashMap<>();

    //待发送消息存储的队列
    private final LinkedBlockingQueue<IPMessage> messageQueue = new LinkedBlockingQueue<>();

    //消息发送线程池
    private IOThreadPool sendMessageThreadPool;

    //通讯持有
    private Communicator communicator;

    //消息持久化
    private IPersistentMessage iPushMessageStore;

    IMServerImps() {}

    //启动入口
    void launchService(Communicator communicator, String serverName,boolean isPushServer,String packagePath) {
        this.communicator = communicator;
        if (isPushServer) startPushMessageServer();
    }

    //消息发送线程
    private Runnable pushRunnable() {
        return () -> {
            while (!communicator.isShutdown()){
                try {
                    IPMessage message = messageQueue.take();
                    sendMessagePrepare(message);
                } catch (Exception ignored) {

                }
            }
        };
    }

    //清理无效连接
    private Runnable clearRunnable(){
        return () ->{
            //循环检测 -保活
            while (!communicator.isShutdown()){
                try {
                    Thread.sleep(PING_TIMEOUT_MAX * 2);
                    clearInvalidClientConnect(); //监测
                } catch (Exception ignored) { }
            }
        };
    }

    //开始推送服务
    private void startPushMessageServer(){
        sendMessageThreadPool = new IOThreadPool();

        //检测清理客户端
        Thread clearThread = new Thread(clearRunnable());
        clearThread.setName("im_clear_t_"+clearThread.getId());
        clearThread.start();

        //消息发送
        Thread sendMessageThread = new Thread(pushRunnable());
        sendMessageThread.setName("im_send_message_t_"+sendMessageThread.getId());
        sendMessageThread.start();
    }

    //创建消息存储实例
    void findJarAllClass(Class<?> classType) {
        try {
            if (iPushMessageStore!=null) return;

            if (!classType.equals(IPersistentMessage.class) && IPersistentMessage.class.isAssignableFrom(classType)){
                //消息存储实体
                iPushMessageStore = (IPersistentMessage) classType.newInstance();
                Log4j.info(FLAG+"消息数据存储实例:"+ iPushMessageStore.getClass());
            }
        } catch (Exception e) {
            Log4j.error(FLAG+"消息存储实例错误",e);
        }
    }

    @Override
    public String accessService(IRequest request, Current __current) {
        return null;
    }

    /* 客户端请求上线 */
    @Override
    public void online(Identity identity, Current __current)  {
        try {
            String identityName = identity.name;
            String clientType =  identity.category;
            if ( StringUtil.isEmpty(identityName,clientType) ) throw new IllegalArgumentException("客户端信息不完整");
            Ice.ObjectPrx base = __current.con.createProxy(identity);
            PushMessageClientPrx client = PushMessageClientPrxHelper.uncheckedCast(base);
            // 添加到队列
            addClient(clientType,identityName,client);

        } catch (java.lang.Exception e) {
            throw new Ice.Exception(e.getCause()){
                @Override
                public String ice_name() {
                    return FLAG+"拒绝连接";
                }
            };
        }
    }

    /* 添加客户端到队列 */
    private void addClient(String clientType,String identityName, PushMessageClientPrx clientPrx){
        try{
            lock.lock();
            //0.检测连接可达
            clientPrx.ice_ping();
            //1.创建属性,设置心跳监听,启动心跳
            addAttr(clientPrx);

            //2.根据种类判断是否存在,不存在创建并存入
            HashMap<String, ArrayList<PushMessageClientPrx>> map = onlineClientMaps.computeIfAbsent(clientType, k -> new HashMap<>());
            //3.根据标识查询客户端列表,不存在列表,创建并存入
            ArrayList<PushMessageClientPrx> list = map.computeIfAbsent(identityName,k -> new ArrayList<>());
            //4.加入列表
            list.add(clientPrx);

            Log4j.info(FLAG+"添加客户端("+clientType+" , "+identityName+") , "+
                    clientPrx.ice_getConnection().toString().replace("\n"," <-> "));
//                    + ",同类型同标识连接数量:"+ list.size());

            //5.检测是否存在可推送的消息
            getOfflineMessageFromIdentityName(identityName);

        }catch (Exception e){
            Log4j.error(FLAG+"添加客户端("+clientType+" , "+identityName+")失败",e);
        }finally {
            lock.unlock();
        }
    }

    /* 获取此标识的全部客户端列表 */
    private List<PushMessageClientPrx> getClientPrxAllList(String identityName) {
        final List<PushMessageClientPrx> list = new ArrayList<>();

        // 客户端类型 <-> <客户端标识,客户端列表>
        for (Map.Entry<String, HashMap<String, ArrayList<PushMessageClientPrx>>> type_identity_clientList_entity : onlineClientMaps.entrySet()) {
            // 客户端标识 <-> 客户端列表
            HashMap<String, ArrayList<PushMessageClientPrx>> identity_clientList_maps = type_identity_clientList_entity.getValue();
            List<PushMessageClientPrx> clientList = identity_clientList_maps.get(identityName);
            if (clientList!=null && clientList.size()>0){
                list.addAll(clientList);
            }
        }

        return list;
    }

    //发送消息到客户端
    @Override
    public void sendMessageToClient(String identityName, String message, Current __current) {
        //放入消息队列
        addMessageQueue(IPMessage.create(identityName, message));
    }

    //发送消息到客户端
    private void sendMessagePrepare(IPMessage message) {
            try {
                final String _message = convertMessage(message);
                if (_message == null) return;
                //获取客户端列表
                List<PushMessageClientPrx> clientPrxList = getClientPrxAllList(message.identityName);
                //System.out.println(FLAG+ "发送消息,可接收客户端数量("+clientPrxList.size()+"): "+ message);
                if (clientPrxList.size()==0){
                    Log4j.info(FLAG+"没有接收的客户端,丢弃:"+message);
                    return;
                }
                for (final PushMessageClientPrx clientPrx : clientPrxList) {
                    asyncSendMessageExecute(clientPrx,message,_message);
                }
            } catch (Exception e) {
                Log4j.error(FLAG+"发送消息失败"+message,e);
            }
    }

    // 异步发送消息到客户端
    private void asyncSendMessageExecute(final PushMessageClientPrx clientPrx, IPMessage message, final String _message) {
        sendMessageThreadPool.post(() -> {
            ClientAttribute attribute = getAttr(clientPrx);
//            try {
//                System.out.println(FLAG+ "发送消息:"+clientPrx.ice_getConnection()+" >> " + _message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            if (attribute == null || attribute.invalid) return;

            try {
                clientPrx.receive(_message);
                sendMessageSuccess(message);
                attribute.invalid = false;
            } catch (Exception e) {
                Log4j.error(FLAG + "发送失败\t客户端:" + attribute.addressInfo + "\n\t消息体:" + message, e);
                attribute.invalid = true;
            }
        });
    }

    //存储消息
    @Override
    public long waitSendMessage(IPMessage message) {
        if (iPushMessageStore!=null){
            return iPushMessageStore.waitSendMessage(message);
        }
        return 0;
    }

    //改变消息状态
    @Override
    public void sendMessageSuccess(IPMessage message) {
        if (iPushMessageStore!=null){
            iPushMessageStore.sendMessageSuccess(message);
        }
    }

    //加入消息队列
    private void addMessageQueue(IPMessage message){
        if (message.id == 0) {
            //存入数据库
            message.id = waitSendMessage(message);
        }

        //放入消息队列 队列已满无法添加返回false
        boolean isSuccess = messageQueue.offer(message);
        if (!isSuccess) Log4j.info(FLAG+"发送队列已满,丢弃: "+ message);
        //else System.out.println(FLAG+ "添加发送消息,队列大小("+messageQueue.size()+"): "+ message);

    }

    //检测是否存在离线消息
    @Override
    public List<IPMessage> getOfflineMessageFromIdentityName(String identityName) {
        if (iPushMessageStore!=null){
            List<IPMessage> messageList = iPushMessageStore.getOfflineMessageFromIdentityName(identityName);
            if (messageList!=null){
                for (IPMessage message : messageList){
                    addMessageQueue(message);
                }
            }
        }
        return null;
    }

    //消息转换
    @Override
    public String convertMessage(IPMessage message) {
        if (iPushMessageStore!=null) {
            return iPushMessageStore.convertMessage(message);
        }
        return message.content;
    }

    //检测连接
    private void clearInvalidClientConnect() {
        // 类型-<客户端标识,连接对象列表>
        for (Map.Entry<String, HashMap<String, ArrayList<PushMessageClientPrx>>> entry : onlineClientMaps.entrySet()) {
            // 客户端标识-连接对象列表
            Iterator<Map.Entry<String, ArrayList<PushMessageClientPrx>>> identity_clientList_it = entry.getValue().entrySet().iterator();
            while (identity_clientList_it.hasNext()) {

                ArrayList<PushMessageClientPrx> clientList = identity_clientList_it.next().getValue();
                Iterator<PushMessageClientPrx> client_it = clientList.iterator();

                while (client_it.hasNext()) {
                    PushMessageClientPrx clientPrx = client_it.next();
                    ClientAttribute attribute = getAttr(clientPrx);
                    if (attribute == null){
                        client_it.remove();
                    }else{
                        boolean close = false;
                        if (attribute.invalid) {
                            //连接无效
                            close = true;
                        }else if (attribute.lastHeartbeatTimestamp > 0){
                            long diff = System.currentTimeMillis() - attribute.lastHeartbeatTimestamp;
                            if (diff > PING_TIMEOUT_MAX){
                                close = true;
                            }
                        }else{
                            try {
                                clientPrx.receive("heartbeat:" + System.currentTimeMillis());
                            } catch (Exception ignored) {
                                close = true;
                            }
                        }
                        if (close){
                            closeClient(clientPrx);
                        }
                    }


                }
                if (clientList.isEmpty()) identity_clientList_it.remove();
            }
        }
    }


    /* 获取当前在线的客户端标识 */
    public List<String> currentOnlineClientIdentity(){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, ArrayList<PushMessageClientPrx>>> entry : onlineClientMaps.entrySet()) {
            HashMap<String, ArrayList<PushMessageClientPrx>> map = entry.getValue();
            for (Map.Entry<String, ArrayList<PushMessageClientPrx>> stringArrayListEntry : map.entrySet()) {
                list.add(stringArrayListEntry.getKey());
            }
        }
        return list;
    }

    public void stopService(){
        messageQueue.clear();
        closeAllClient();
        if (sendMessageThreadPool !=null) {
            sendMessageThreadPool.close();
            sendMessageThreadPool = null;
        }
    }

    private static void closeClient(PushMessageClientPrx clientPrx){
        try {

            if (clientPrx==null) return;

            String clientType = null;
            String identityName = null;
            String connectInfo = null;

            Identity identity = clientPrx.ice_getIdentity();
            if (identity!=null){
                clientType = identity.category;
                identityName =  identity.name;
            }

            Connection connection = clientPrx.ice_getConnection();

            if (connection != null){
                connectInfo = clientPrx.ice_getConnection().toString().replace("\n"," <-> ");
                //强制关闭
                connection.close(true);
            }

            Log4j.info(FLAG+"关闭客户端("+clientType+" , "+identityName+")  "+connectInfo);

        } catch (Exception e) {
            Log4j.error(FLAG+"关闭连接失败",e);
        }finally {
            removeAttr(clientPrx);
        }
    }

    /* 杀死全部客户端 */
    private void closeAllClient() {
        Iterator<Map.Entry<String,HashMap<String, ArrayList<PushMessageClientPrx>>>> it = onlineClientMaps.entrySet().iterator();
        while (it.hasNext()){
            HashMap<String,ArrayList<PushMessageClientPrx>> map = it.next().getValue();
            Iterator<Map.Entry<String,ArrayList<PushMessageClientPrx>>> it2 = map.entrySet().iterator();
            while (it2.hasNext()){
                ArrayList<PushMessageClientPrx> list = it2.next().getValue();
                Iterator<PushMessageClientPrx> it3 = list.iterator();
                while (it3.hasNext()){
                    PushMessageClientPrx clientPrx = it3.next();
                    closeClient(clientPrx);
                    it3.remove();
                }
                it2.remove();
            }
            it.remove();
        }
    }
}
