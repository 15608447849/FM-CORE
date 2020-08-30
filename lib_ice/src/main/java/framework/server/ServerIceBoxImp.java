package framework.server;

import Ice.Communicator;
import Ice.Object;
import bottle.objectref.ObjectRefUtil;
import framework.client.IceClient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * 应用入口
 */
public final class ServerIceBoxImp extends IceBoxServerAbs {

    //ice 客户端
    public static IceClient _ic(){
        ServerIceBoxImp serverIceBoxImp =  ServerIceBoxImp.get();
        if (serverIceBoxImp==null) throw new RuntimeException("服务未启动!");
        return serverIceBoxImp.getInsideClient();
    }

    private static ServerIceBoxImp INSTANCE = null;

    public ServerIceBoxImp(){
        if (INSTANCE!=null) throw new RuntimeException("IceBox服务实例已存在");
        INSTANCE = this;
    }

    public static ServerIceBoxImp get() {
        return INSTANCE;
    }

    private String rpcGroupName;
    private String serverName;

    private IceClient insideClient;

    public IceClient getInsideClient() {
        return insideClient;
    }

    @Override
    public void start(String name, Communicator communicator, String[] args) {
        insideClient = IceClient.Inside(communicator);
        super.start(name, communicator, args);
    }

    private final ApiServerImps service = new ApiServerImps();

    private final List<Initializer> list = new ArrayList<>();

    public ApiServerImps getService() {
        return service;
    }

    public String getRpcGroupName() {
        return rpcGroupName;
    }

    public String getServerName() {
        return serverName;
    }


    @Override
    protected Object specificServices(String serviceName) {
        serverName = serviceName;
        return service;
    }

    @Override
    protected void addRpcGroup(String rpcName) {
        rpcGroupName = rpcName;
    }

    @Override
    protected void findJarAllClass(String classPath) throws Exception {
        Class<?> cls = Class.forName(classPath);
        if ( !cls.equals(Initializer.class) && Initializer.class.isAssignableFrom(cls)){
            list.add(((Initializer) ObjectRefUtil.createObject(cls,null)));
        }
        service.callback(classPath);
    }

    @Override
    protected void initialization() {

        list.sort(Comparator.comparingInt(Initializer::priority));
        for (Initializer o : list){
            try {
                o.initialization(serverName,rpcGroupName,_communicator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void startService(boolean isPushMessage, String packagePath) {
        service.launchService(_communicator,serverName,isPushMessage,packagePath);
    }


    @Override
    public void stop() {
        for (Initializer it : list){
            it.onDestroy();
        }
        service.stopService();
        super.stop();
    }
}
