package framework.server;

import Ice.Communicator;
import Ice.Current;
import Ice.Identity;
import bottle.objectref.ObjectRefUtil;
import bottle.threadpool.IOThreadPool;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import com.onek.server.inf.*;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import static framework.server.IMServerImps.ClientAttribute.*;

/**
 * @Author: leeping
 * @Date: 2019/4/9 17:57
 * 消息推送 服务端实现
 */
public class IMServerImps extends _InterfacesDisp implements IPersistentMessage {
    /* 客户端属性 */
    static final class ClientAttribute{
        final static HashMap<PushMessageClientPrx,ClientAttribute> clientAttrMap = new HashMap<>();
        static void addAttr(PushMessageClientPrx prx){
            clientAttrMap.put(prx,new ClientAttribute(prx));
        }
        static void removeAttr(PushMessageClientPrx prx){
            clientAttrMap.remove(prx);
        }
        private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private final long connectStartTime; //开始时间
        private String addressInfo;
        private int sendMessageCount = 0;//发送消息次数

        private ClientAttribute(PushMessageClientPrx prx) {
//            prx.get
            if (prx.ice_getConnection() != null) {
                try {
                    addressInfo = prx.ice_getConnection().toString().split("\n")[1].replace("remote address =","").trim();
                } catch (Exception e) {
                    addressInfo = "获取信息失败";
                }
            }
            this.connectStartTime = System.currentTimeMillis();
        }

        public String getConnectStartTime(){
            return format.format(new Date(this.connectStartTime));
        }


        public  long getConnectedDuration(){
            return System.currentTimeMillis() - connectStartTime;
        }

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
    private final ConcurrentLinkedQueue<IPMessage> messageQueue = new ConcurrentLinkedQueue<>();

    //线程池
    private IOThreadPool threadPool;
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
    

    //开始推送服务
    private void startPushMessageServer(){
        threadPool = new IOThreadPool();
        threadPool.post(heartRunnable());//心跳线程
        threadPool.post(pushRunnable());//消息发送
        Log4j.info("推送服务已启动");
    }

    //创建消息存储实例
    void findJarAllClass(Class<?> classType) {
        try {
            if (iPushMessageStore!=null) return;

            if (!classType.equals(IPersistentMessage.class) && IPersistentMessage.class.isAssignableFrom(classType)){
                //消息存储实体
                iPushMessageStore = (IPersistentMessage) classType.newInstance();
                Log4j.info("注入推送消息数据存储实现:"+ iPushMessageStore.getClass());
            }
        } catch (Exception e) {
            Log4j.error("IM 创建消息存储实例错误",e);
        }
    }

    @Override
    public String accessService(IRequest request, Current __current) {
        return null;
    }

    //客户端上线
    @Override
    public void online(Identity identity, Current __current)  {
        try {
            final String identityName = identity.name;
            final String clientType =  identity.category;
            if ( StringUtil.isEmpty(identityName,clientType) ) throw new Exception("客户端信息不完整");
            Ice.ObjectPrx base = __current.con.createProxy(identity);
            final PushMessageClientPrx client = PushMessageClientPrxHelper.uncheckedCast(base);
            //添加到队列
            addClient(clientType,identityName,client);
            //检测是否存在可推送的消息
            getOfflineMessageFromIdentityName(identityName);
        } catch (java.lang.Exception e) {
            throw new Ice.Exception(e.getCause()){
                @Override
                public String ice_name() {
                    return "推送服务拒绝连接";
                }
            };
        }
    }

    //添加客户端到队列
    private void addClient(String clientType,String identityName, PushMessageClientPrx clientPrx){
        try{
            lock.lock();
            //1.根据种类判断是否存在,不存在创建并存入
            HashMap<String, ArrayList<PushMessageClientPrx>> map = onlineClientMaps.computeIfAbsent(clientType, k -> new HashMap<>());
            //2.根据标识查询客户端列表,不存在列表,创建并存入
            ArrayList<PushMessageClientPrx> list = map.computeIfAbsent(identityName,k -> new ArrayList<>());

            //3.加入列表
            list.add(clientPrx);
            addAttr(clientPrx);

            Iterator<PushMessageClientPrx> it = list.iterator();
            while (it.hasNext()){
                PushMessageClientPrx prx = it.next();
                if (!pingConnection(prx)){
                    it.remove();
                    removeAttr(clientPrx);
                    Log4j.info(
                            "Ping测试-ADD-移除客户端: "+communicator.identityToString(clientPrx.ice_getIdentity())
                    );

                }
            }
            Log4j.info("【推送服务】添加客户端,类型 = "+clientType+" ,标识 = "+ identityName +" ,相同连接数量:"+ list.size());

        }finally {
            lock.unlock();
        }
    }

    //获取此标识的全部客户端列表
    private List<ArrayList<PushMessageClientPrx>> getClientPrxList(String identityName) {

        List<ArrayList<PushMessageClientPrx>> list = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, ArrayList<PushMessageClientPrx>>> entry1 : onlineClientMaps.entrySet()) {
            HashMap<String, ArrayList<PushMessageClientPrx>> map = entry1.getValue();
            for (Map.Entry<String, ArrayList<PushMessageClientPrx>> entry : map.entrySet()) {
                String key = entry.getKey();
                if (identityName.equals(key)) {
                    list.add(new ArrayList<>(entry.getValue())); //复制客户端出来
                }
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
    private void sendMessage(final IPMessage message) {
        try {
                //获取客户端列表
                List<ArrayList<PushMessageClientPrx>> clientPrxList = getClientPrxList(message.identityName);

                for (ArrayList<PushMessageClientPrx> list : clientPrxList){
                    for (final PushMessageClientPrx clientPrx : list) {

                        threadPool.post(() -> {
                            try {
                                String _message = convertMessage(message);
                                if (_message == null) return;

                                clientPrx.receive(_message);
                                ClientAttribute attribute = clientAttrMap.get(clientPrx);
                                if (attribute!=null){ attribute.sendMessageCount++; }
                                sendMessageSuccess(message);
                            } catch (Exception e) {
                                Log4j.info("发送失败\t客户端:" + communicator.identityToString(clientPrx.ice_getIdentity()) +
                                       "\n\t消息体:" + message +
                                       "\n\t错误原因:" + e);
                            }
                        });

                    }
                }

        } catch (Exception e) {
            Log4j.error("发送消息错误",e);
        }

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
        //放入消息队列
        messageQueue.offer(message);
        synchronized (messageQueue){
            messageQueue.notify();
        }
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

    //消息发送线程
    private Runnable pushRunnable() {
        return () -> {
            while (!communicator.isShutdown()){
                try {
                    IPMessage message = messageQueue.poll();
                    if (message == null){
                        synchronized (messageQueue){
                            try {
                                messageQueue.wait();
                            } catch (InterruptedException ignored) {
                            }
                        }
                        continue;
                    }
                    sendMessage(message);
                } catch (Exception ignored) {
                }
            }
        };
    }

    private Runnable heartRunnable(){
        return () ->{
            //循环检测 -保活
            while (!communicator.isShutdown()){
                try {
                    Thread.sleep( 30 * 1000);
                    checkConnect(); //监测
                } catch (Exception ignored) { }
            }
        };
    }

    //检测连接
    private void checkConnect() {
        Iterator<Map.Entry<String,HashMap<String, ArrayList<PushMessageClientPrx>>>> it = onlineClientMaps.entrySet().iterator();
        while (it.hasNext()){
            HashMap<String,ArrayList<PushMessageClientPrx>> map = it.next().getValue();
            Iterator<Map.Entry<String,ArrayList<PushMessageClientPrx>>> it2 = map.entrySet().iterator();
            while (it2.hasNext()){
                ArrayList<PushMessageClientPrx> list = it2.next().getValue();
                Iterator<PushMessageClientPrx> it3 = list.iterator();
                while (it3.hasNext()){
                    PushMessageClientPrx clientPrx = it3.next();
                    if (!pingConnection(clientPrx)){
                        it3.remove();
                        removeAttr(clientPrx);
                        Log4j.info(
                                "Ping测试-AUTO-移除客户端: "+communicator.identityToString(clientPrx.ice_getIdentity())
                        );
                    }
                    /*
                    else{
                        ClientAttribute attribute = clientAttrMap.get(clientPrx);

                        if (attribute!=null){
                            Log4j.info("在线客户端信息: "+communicator.identityToString(clientPrx.ice_getIdentity()) + " ADDRESS="+attribute.addressInfo+
                                    ", 开始连接时间:"+ attribute.getConnectStartTime()+" , 连接时长: "+ attribute.getConnectedDurationHumStr());
                        }

                    }
                    */
                }
                if (list.size() == 0) it2.remove();
            }
            if (map.size() == 0) it.remove();
        }
    }

    private boolean pingConnection(PushMessageClientPrx clientPrx) {
        try {
            clientPrx.ice_invocationTimeout(PING_TIMEOUT_MAX).ice_ping();
            return true;
        } catch (Exception e) {
//            e.Log4j.infoStackTrace();
            return false;
        }
    }

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
        closeAllUser();
        if (threadPool !=null) threadPool.close();
    }

    private void closeAllUser() {
        Iterator<Map.Entry<String,HashMap<String, ArrayList<PushMessageClientPrx>>>> it = onlineClientMaps.entrySet().iterator();
        while (it.hasNext()){
            HashMap<String,ArrayList<PushMessageClientPrx>> map = it.next().getValue();
            Iterator<Map.Entry<String,ArrayList<PushMessageClientPrx>>> it2 = map.entrySet().iterator();
            while (it2.hasNext()){
                ArrayList<PushMessageClientPrx> list = it2.next().getValue();
                Iterator<PushMessageClientPrx> it3 = list.iterator();
                while (it3.hasNext()){
                    PushMessageClientPrx clientPrx = it3.next();
                    try {

                        HashMap<String,String> m = new HashMap<>();
                        m.put("close","1");
                        clientPrx.ice_ping(m);

                    } catch (Exception e) {
                        it3.remove();
                    }
                }
                if (list.size() == 0) it2.remove();
            }
            if (map.size() == 0) it.remove();
        }
    }
}
