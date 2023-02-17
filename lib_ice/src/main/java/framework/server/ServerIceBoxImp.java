package framework.server;

import Ice.*;
import IceBox.Service;
import bottle.objectref.ClassUtil;
import bottle.util.Log4j;
import framework.client.IceClient;
import java.io.File;
import java.lang.Exception;
import java.util.*;


/**
 * 接入ice框架的服务
 */
public final class ServerIceBoxImp implements Service {
    private final ApiServerImps service = new ApiServerImps();
    private ObjectAdapter _adapter;
    private String rpcGroupName;
    protected String serverName;
    private IceClient insideClient;
    private static ServerIceBoxImp INSTANCE = null;
    private final ScanInitializerClass scanInitializerClass = new ScanInitializerClass(){
        @Override
        public ScanApplicationClassCallback next() {
            return service.getExpandScanCallback();
        }
    };
    public static ServerIceBoxImp get() {
        return INSTANCE;
    }

    public IceClient getInsideClient() {
        return insideClient;
    }

    public ServerIceBoxImp(Communicator communicator){
        if (INSTANCE != null) throw new RuntimeException("IceBox服务实例已存在");
        INSTANCE = this;
        //创建服务间通讯的Ice客户端
        insideClient = IceClient.Inside(communicator);
    }

    //ice 客户端
    public static IceClient _ic(){
        ServerIceBoxImp serverIceBoxImp =  ServerIceBoxImp.get();
        if (serverIceBoxImp==null) throw new RuntimeException("服务未启动!");
        return serverIceBoxImp.getInsideClient();
    }



    @Override
    public void start(String name, Communicator communicator, String[] args) {
        /*
        *
        * 0 分布式复制均衡服务组名
        * 1 是否启用长连接IM通讯
        * 2 API接口所在包目录
        * 3 http节点文件服务端口: -1默认,不启用 0随机端口, >0指定端口
        * 4 http节点文件存放路径
        * 5 http节点文件存在最小时间,秒
        * */
        Log4j.info(name+" args = " + Arrays.toString(args));

        _adapter = communicator.createObjectAdapter(name);
        serverName = name;
        rpcGroupName = args.length >=1 ? args[0] : null;

        boolean isPushServer = args.length >= 2 && Boolean.parseBoolean(args[1]);
        String packagePath = args.length >= 3 ? args[2] : "";

        int httpPort = args.length>=4 && args[3].length()>0? Integer.parseInt(args[3]) : -1;
        String httpFilePath = args.length>=5 && args[4].length()>0? args[4] : null;
        long httpFileTime = args.length>=6 && args[5].length()>0? Long.parseLong(args[5]) : 0;

        //关联servant
        Ice.Object object = getService();
        _adapter.add(object,communicator.stringToIdentity(serverName));
        //配置rpc组信息
        if (rpcGroupName != null && rpcGroupName.length()>0) {
            _adapter.add(object,communicator.stringToIdentity(rpcGroupName));
        }
        Log4j.info("-@- 服务: "+serverName +" ,加入组: " + rpcGroupName);

        //初始化服务
        initServer(communicator,packagePath);
        //启动web文件服务
        WebServerProxy.startWebServer(httpPort,httpFilePath,httpFileTime);
        //启动node服务
        startService(communicator,isPushServer,packagePath);
        //激活适配器
        _adapter.activate();
    }

    //初始化服务
    private void initServer(Communicator communicator,String packagePath) {
        try {
            long time = System.currentTimeMillis();
            /* 如果直接执行.class文件那么会得到当前class的绝对路径。如果封装在jar包里面执行jar包那么会得到当前jar包的绝对路径*/
            File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            String jarUrl = file.toURI().toASCIIString();
            Set<String> classFullNameSet = ClassUtil.scanJarFileOnGetClassPaths(jarUrl,packagePath);
            Log4j.info("扫描jar 耗时:"+ (System.currentTimeMillis() - time)+"ms   "+ packagePath+".*.class 数量: "+classFullNameSet.size());
            time = System.currentTimeMillis();
            for (String classFullName : classFullNameSet) {
                ScanApplicationClassExecute.scanRunClass(classFullName,scanInitializerClass);
            }
            ScanApplicationClassExecute.startRunClass(scanInitializerClass,serverName,rpcGroupName,communicator);
            Log4j.info("初始化class 耗时:"+ (System.currentTimeMillis() - time)+"ms");

//            scanJarAllClass(packagePath);
//            Log4j.info("扫描jar 耗时:"+ (System.currentTimeMillis() - time)+"ms");
//            initialization(communicator);
//            Log4j.info("服务初始化耗时:"+ (System.currentTimeMillis() - time)+"ms");
        } catch (Exception e) {
            Log4j.error("服务初始化错误",e);
        }
    }

    public ApiServerImps getService() {
        return service;
    }

    public String getRpcGroupName() {
        return rpcGroupName;
    }

    public String getServerName() {
        return serverName;
    }


    private void startService(Communicator communicator,boolean isPushMessage, String packagePath) {
        try {
            service.launchService(communicator, serverName, isPushMessage, packagePath);
        }catch (Exception e){
            Log4j.error("服务扫描错误",e);
        }
    }

    @Override
    public void stop() {
        ScanApplicationClassExecute.stopRunClass(scanInitializerClass);
        WebServerProxy.stopWebServer();
        service.stopService();
        _adapter.destroy();
        Log4j.info(serverName + " 服务销毁");
    }

}
