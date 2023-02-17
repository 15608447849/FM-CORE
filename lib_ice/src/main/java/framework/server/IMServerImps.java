package framework.server;

import Ice.*;
import bottle.tuples.Tuple2;
import bottle.util.*;
import com.onek.server.inf.*;


import java.lang.Exception;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * @Author: leeping
 * @Date: 2019/4/9 17:57
 * 消息推送 服务端实现
 */
public class IMServerImps extends _InterfacesDisp implements IPersistentMessage {
    static final String FLAG = "【长连接服务】 ";
    // 当前总连接数
    private static AtomicInteger currentConnectNum = new AtomicInteger();
    //当前在线的所有客户端 <客户端类型<客户端标识,相同客户端列表>>
    private final static ConcurrentHashMap<String,ConcurrentHashMap<String, List<_LongConnectionCallback>>> onlineClientMaps = new ConcurrentHashMap<>();
    //等待上线客户端标识
    private final static LinkedBlockingQueue<Tuple2<Ice.Identity,Current>> waitOnlineClientIdentityQueue = new LinkedBlockingQueue<>();
    //上线客户端获取离线消息
    private final static LinkedBlockingQueue<String> offlineClientOnlineQueue = new LinkedBlockingQueue<>();
    //待发送消息存储的队列
    private final static LinkedBlockingQueue<IPMessage> messageQueue = new LinkedBlockingQueue<>();
    //通讯持有
    private Communicator communicator;
    //消息持久化
    private final List<IPersistentMessage> iPushMessageStoreList  = new ArrayList<>();

    private ScanApplicationClassCallback.Adapter scanApplicationClassCallback = new ScanApplicationClassCallback.Adapter(){
        @Override
        public void findClass(Class<?> classType) throws Exception {
            //创建消息存储实例
            try {

                if (!classType.equals(IPersistentMessage.class) && IPersistentMessage.class.isAssignableFrom(classType)){
                    //消息存储实体
                    IPersistentMessage iPushMessageStore = ClassInstanceStorage.getInstance(classType);
                    iPushMessageStoreList.add(iPushMessageStore);
//                  System.out.println("ICE 初始化,推送消息持久化处理器: " + classType + " -> " + iPushMessageStore);
//                  Log4j.info(FLAG+"消息数据存储实例:"+ iPushMessageStore.getClass());
                    Log4j.info("加入 消息数据存储实例: "+ iPushMessageStore );

                }

            } catch (Exception e) {
                Log4j.error(FLAG+"消息存储实例错误",e);
            }
        }
    };



    private long sendCount = 0;

    IMServerImps() {}

    //启动入口
    void launchService(Communicator communicator, String serverName,boolean isPushServer,String packagePath) {
        this.communicator = communicator;
        if (isPushServer) startPushMessageServer();
    }

    //客户端上线
    private Runnable identityOnline() {
        return () -> {
            while (!communicator.isShutdown()){
                try {
                    Tuple2<Identity,Current> tuple2 = waitOnlineClientIdentityQueue.take();
                    onlineIdentity(tuple2.getValue0(),tuple2.getValue1());
                } catch (Exception ignored) { }
            }
        };
    }

    //消息发送线程
    private Runnable pushRunnable() {
        return () -> {
            while (!communicator.isShutdown()){
                try {
                    IPMessage message = messageQueue.take();
                    sendMessagePrepare(message);
                } catch (Exception ignored) { }
            }
        };
    }

    //客户端上线发送离线消息
    private Runnable offlineMessage() {
        return () ->{
            //循环检测 -保活
            while (!communicator.isShutdown()){
                try {
                   String identityName = offlineClientOnlineQueue.take();
                    //检测是否存在可推送的离线消息
                    List<IPMessage> messageList = getOfflineMessageFromIdentityName(identityName);

                    if (messageList!=null && messageList.size()>0){
                        System.out.println("目标: "+ identityName+ " , 离线消息数量: "+ messageList.size());
                        for (IPMessage message : messageList){
                            //System.out.println("添加离线消息: "+ message.id+" -> "+ message.identityName+" "+ message.content );

                            addMessageQueue(message);
                        }
                    }
                } catch (Exception ignored) { }
            }
        };
    }

    //开始推送服务
    private void startPushMessageServer(){
        //sendMessageThreadPool = new IOThreadPool();
        //上线接入
        Thread identityOnline = new Thread(identityOnline());
        identityOnline.setName("im_identity_online_t_"+identityOnline.getId());
        identityOnline.start();
        //离线消息
        Thread offlineMessage = new Thread(offlineMessage());
        offlineMessage.setName("im_offline_message_t_"+offlineMessage.getId());
        offlineMessage.start();

        //消息发送
        for(int i = 0; i<8;i++){
            Thread sendMessageThread = new Thread(pushRunnable());
            sendMessageThread.setName("im_send("+i+")_message_t_"+sendMessageThread.getId());
            sendMessageThread.start();
        }
    }



    protected ScanApplicationClassCallback getExpandScanCallback() {
        return scanApplicationClassCallback;
    }

    @Override
    public String accessService(IRequest request, Current __current) {
        return null;
    }

    /* 客户端请求上线 */
    @Override
    public void online(Identity identity, Current __current)  {
        boolean isAddSuccess = waitOnlineClientIdentityQueue.offer(new Tuple2<>(identity,__current));
        if (!isAddSuccess) {
            closeConnect(__current.con);
            Log4j.info(FLAG+"[上线失败] 客户端: " + identity+"("+identity.category+"/"+identity.name+")");
        }
    }

    private static void onlineIdentity(Identity identity,Current __current){
        try{
            String identityName = identity.name;
            String clientType =  identity.category;
            if ( StringUtil.isEmpty(identityName,clientType) ) throw new IllegalArgumentException("客户端信息不完整");

            Ice.ObjectPrx base = __current.con.createProxy(identity).ice_invocationTimeout(15*1000);

            PushMessageClientPrx client = PushMessageClientPrxHelper.uncheckedCast(base);
//                System.out.println(FLAG+"[预上线] 当前客户端: "+ identity+"("+identity.category+"/"+identity.name+") "
//                        +" \nice_getConnectionId: "+ client.ice_getConnectionId()
//                        +" \nice_isConnectionCached: "+ client.ice_isConnectionCached()
//                        +" \nice_isTwoway: "+ client.ice_isTwoway()
//                        +" \nice_isCollocationOptimized: "+ client.ice_isCollocationOptimized()
//                        +" \nice_getInvocationTimeout: "+ client.ice_getInvocationTimeout()
//                );
            client.ice_ping();
            // 添加到队列
            addClient(clientType,identityName,__current.con,client);

        }catch (Exception e){
            //关闭连接
            closeConnect(__current.con);
            Log4j.info(FLAG+"[上线失败] 客户端: "+ identity + "("+identity.category+"/"+identity.name+")");
            e.printStackTrace();
        }
    }

    /* 添加客户端到队列 */
    private static synchronized void addClient(String clientType, String identityName, Connection connection,PushMessageClientPrx clientPrx){

        try{
            if (clientType.contains("-ice_heartbeat:")){
                String[] arr = clientType.split("-ice_heartbeat:");
                clientType = arr[0];
            }
            //1.接收客户端心跳,启动服务端心跳
            _LongConnectionCallback callback = new _LongConnectionCallback(clientType, identityName, clientPrx, connection, new _LongConnectionCallback.ClosedCallback() {
                @Override
                public void closed(_LongConnectionCallback callback,String clientType, String identityName, PushMessageClientPrx clientPrx, String connectionInfo, long connectStartTime) {
                    //移除客户端连接
                    removeClient(callback,clientType,identityName,clientPrx,connectionInfo,connectStartTime);
                }
            });

            //2.根据种类判断是否存在,不存在创建并存入
            ConcurrentHashMap<String, List<_LongConnectionCallback>> map = onlineClientMaps.computeIfAbsent(clientType, k -> new ConcurrentHashMap<>());

            //3.根据标识查询客户端列表,不存在列表,创建并存入
            List<_LongConnectionCallback> list = map.computeIfAbsent(identityName,k -> new ArrayList<>());

            //4.加入列表
            list.add(callback);

            //5.查询离线消息
            offlineClientOnlineQueue.offer(identityName);

            //6.连接数+1
            int number = currentConnectNum.incrementAndGet();

            try {
                Log4j.info(FLAG + "[添加] "+ clientPrx.ice_getIdentity()+" ("+clientType+" , "+identityName+")\t"
                        + clientPrx.ice_getConnection().toString().replace("\n","<"+connection.type()+">")
                        + " ,同标识在线数:"+ list.size()
                        + " ,当前总在线数:"+number);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            Log4j.error(FLAG+"添加客户端("+clientType+" , "+identityName+")失败",e);
            throw e;
        }
    }

    // 关闭连接
    private static void closeConnect(Connection connection) {
        try{
            if (connection!=null){
                connection.close(false);
            }
        }catch (Exception e){
            Log4j.error(FLAG+"关闭连接失败: "+ connection,e);
        }
    }

    private static void closeConnect(PushMessageClientPrx clientPrx) {
        if (clientPrx!=null){

            Connection connectionCache = null;
            try{
                if (clientPrx.ice_isConnectionCached()){
                    connectionCache = clientPrx.ice_getCachedConnection();
                    if (connectionCache!=null){
                        connectionCache.close(true);
                    }
                }
            }catch (Exception e){
                Log4j.error(FLAG+"关闭代理连接(缓存)失败: "+ connectionCache,e);
            }

            Connection connection = null;
            try{
                connection = clientPrx.ice_getConnection();
                if (connection!=null){
                    connection.close(true);
                }
            }catch (Exception e){
                Log4j.error(FLAG+"关闭代理连接失败: "+ connection,e);
            }
        }
    }

    /* 移除客户端从队列 */
    private static synchronized void removeClient(_LongConnectionCallback callback, String clientType,String identityName, PushMessageClientPrx clientPrx, String connectionInfo,long startTimestamp){
        try{
            closeConnect(clientPrx);
            ConcurrentHashMap<String, List<_LongConnectionCallback>> map = onlineClientMaps.get(clientType);
            if(map!=null){
                List<_LongConnectionCallback> list = map.get(identityName);
                if (list!=null){
                    list.remove(callback);
                    int number = currentConnectNum.decrementAndGet();

                    try {
                        Log4j.info(FLAG + "[移除] " + clientPrx.ice_getIdentity() + " ("+clientType+" , "+identityName+")\t" + connectionInfo
                                        + " ,在线时长:"+ TimeTool.getConnectedDurationHumStr(System.currentTimeMillis() - startTimestamp)
                                        + " ,同标识在线数:"+ list.size()
                                        + " ,当前总在线数:" + number);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (list.isEmpty()){
                        map.remove(identityName);
                    }
                }
            }
        }catch (Exception e){
            Log4j.error(FLAG+"移除客户端("+clientType+" , "+identityName+")\t"+connectionInfo+" 失败",e);
        }
    }

    /* 获取此标识的全部客户端列表 */
    private List<_LongConnectionCallback> getClientPrxAllList(String identityName) {
        final List<_LongConnectionCallback> list = new ArrayList<>();

        // 客户端类型 <-> <客户端标识,客户端列表>
        for (Map.Entry<String, ConcurrentHashMap<String, List<_LongConnectionCallback>>> type_identity_clientList_entity : onlineClientMaps.entrySet()) {
            // 客户端标识 <-> 客户端列表
            ConcurrentHashMap<String, List<_LongConnectionCallback>> identity_clientList_maps = type_identity_clientList_entity.getValue();
            List<_LongConnectionCallback> clientList = identity_clientList_maps.get(identityName);
            if (clientList!=null && clientList.size()>0){
                list.addAll(clientList);
            }
        }
        return list;
    }

    // 发送消息到客户端
    @Override
    public void sendMessageToClient(String identityName, String message, Current __current) {
        //放入消息队列
        addMessageQueue(IPMessage.create(identityName, message));
    }

    // 发送消息到客户端
    private void sendMessagePrepare(IPMessage message) {
            try {
                message.id = waitSendMessage(message);
                //获取客户端列表
                final List<_LongConnectionCallback> clientPrxList = getClientPrxAllList(message.identityName);
                final String _message = convertMessage(message);
                if (_message == null) return;

                if (clientPrxList.size()==0 && message.id == 0){
                    Log4j.info(FLAG+"[丢弃] 客户端不在线\t"+ message.identityName +" > "+ _message);
                    return;
                }

                //异步发送
//                sendMessageThreadPool.post(() -> {
                    boolean isSendSuccess = false ;
                    try {
                        for (final _LongConnectionCallback clientPrx : clientPrxList) {

                            if (clientPrx.sendMessage(_message) && !isSendSuccess){
                                isSendSuccess = true;
                            }
                        }
                        if(isSendSuccess){
                            sendMessageSuccess(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        sendCount++;
//                        if (message.id == 0){
                           // System.out.println("发送消息次数统计: " + sendCount+"客户端数("+clientPrxList.size()+") 是否成功("+isSendSuccess+") MsgID("+message.identityName+")>> "+ _message);
//                        }
                    }
//                });

            } catch (Exception e) {
                Log4j.error(FLAG+"预处理发送消息失败: "+message,e);
            }
    }


    // 存储等待发送的消息未发送将变成离线消息 返回持久化存储的ID
    @Override
    public long waitSendMessage(IPMessage message) {
        if (message!=null && message.id==0){
            for (IPersistentMessage iPushMessageStore : iPushMessageStoreList) {
                try {
                    long id = iPushMessageStore.waitSendMessage(message);
                    if (id >0 ) return id;
                } catch (Exception ignored) {}
            }
        }
        return message!=null ? message.id : 0;
    }

    // 消息转换
    @Override
    public String convertMessage(IPMessage message) {
        if (message!=null) {
            for (IPersistentMessage iPushMessageStore : iPushMessageStoreList) {
                try {
                    String temp_message = iPushMessageStore.convertMessage(message);
                    if (temp_message!=null) return temp_message;
                } catch (Exception ignored) {
                }
            }
            return message.content;

        }
        return null;
    }

    // 改变消息状态
    @Override
    public void sendMessageSuccess(IPMessage message) {
        if (message!=null && message.id > 0){
            for (IPersistentMessage iPushMessageStore : iPushMessageStoreList) {
                try {
                    iPushMessageStore.sendMessageSuccess(message);
                } catch (Exception ignored) { }
            }

        }
    }

    // 检测是否存在离线消息
    @Override
    public List<IPMessage> getOfflineMessageFromIdentityName(String identityName) {
        List<IPMessage> all = new ArrayList<>();
        if (identityName!=null && identityName.length()>0){
                for (IPersistentMessage iPushMessageStore : iPushMessageStoreList) {
                    try {
                        List<IPMessage> temp = iPushMessageStore.getOfflineMessageFromIdentityName(identityName);
                        if (temp!=null)  all.addAll(temp);
                    }catch (Exception ignored){ }
                }
        }
        return all;
    }

    // 加入消息队列
    private void addMessageQueue(IPMessage message){
        // 放入消息队列 队列已满将丢弃消息
        boolean isSuccess = messageQueue.offer(message);
        if (!isSuccess) {
            if (waitSendMessage(message) == 0) {
                Log4j.info(FLAG+"[丢弃] 消息队列加入失败\t"+ message.identityName +" > "+ message.content);
            }
        }
    }

    /* 获取当前在线的客户端标识 */
    public List<String> currentOnlineClientIdentity(){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, ConcurrentHashMap<String, List<_LongConnectionCallback>>> entry : onlineClientMaps.entrySet()) {
            ConcurrentHashMap<String, List<_LongConnectionCallback>> map = entry.getValue();
            for (Map.Entry<String, List<_LongConnectionCallback>> stringArrayListEntry : map.entrySet()) {
                list.add(stringArrayListEntry.getKey());
            }
        }
        return list;
    }

    /* 获取当前在线的客户端标识,类型 */
    public List<Tuple2<String,String>> currentOnlineClientIdentityAll(){
        List<Tuple2<String,String>> list = new ArrayList<>();

        // 类型<>标识
        for (Map.Entry<String, ConcurrentHashMap<String, List<_LongConnectionCallback>>> entry : onlineClientMaps.entrySet()) {
            System.out.println("在线标识: "+ entry.getKey());
            // 标识-客户端列表
            ConcurrentHashMap<String, List<_LongConnectionCallback>> map = entry.getValue();
//            System.out.println("在线标识 MAP SIZE : "+  map.size() );

            for (Map.Entry<String, List<_LongConnectionCallback>> stringArrayListEntry : map.entrySet()) {
                System.out.println("\t在线标识 K = "+  entry.getKey() + " V = "+ stringArrayListEntry.getKey() );
                list.add(new Tuple2<>(entry.getKey(),stringArrayListEntry.getKey()));
            }
        }

        return list;
    }



    /* 杀死全部客户端 */
    private static synchronized void closeAllClient() {
        Iterator<Map.Entry<String,ConcurrentHashMap<String, List<_LongConnectionCallback>>>> it = onlineClientMaps.entrySet().iterator();
        while (it.hasNext()){
            ConcurrentHashMap<String,List<_LongConnectionCallback>> map = it.next().getValue();
            Iterator<Map.Entry<String,List<_LongConnectionCallback>>> it2 = map.entrySet().iterator();
            while (it2.hasNext()){
                List<_LongConnectionCallback> list = it2.next().getValue();
                Iterator<_LongConnectionCallback> it3 = list.iterator();
                while (it3.hasNext()){
                    _LongConnectionCallback connectionCallback = it3.next();
                    closeConnect(connectionCallback.clientPrx);
                    currentConnectNum.decrementAndGet();
                    it3.remove();
                }
                it2.remove();
            }
            it.remove();
        }
    }

    public void stopService(){
        messageQueue.clear();
        offlineClientOnlineQueue.clear();
        waitOnlineClientIdentityQueue.clear();
        closeAllClient();
    }
}
