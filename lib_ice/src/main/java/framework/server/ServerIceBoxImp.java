package framework.server;

import Ice.*;
import IceBox.Service;
import bottle.util.Log4j;
import framework.client.IceClient;

import java.io.File;
import java.lang.Exception;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 接入ice框架的服务
 */
public final class ServerIceBoxImp implements Service {
    public static String[] skipInitializeScanArr = new String[]{ "Util","Utils","Tool","Tools" };

    private final ApiServerImps service = new ApiServerImps();
    private ObjectAdapter _adapter;
    private String rpcGroupName;
    private String serverName;
    private IceClient insideClient;

    private static ServerIceBoxImp INSTANCE = null;
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

    // 服务加载初始化列表
    private final List<Initializer> initializerList = new ArrayList<>();

    @Override
    public void start(String name, Communicator communicator, String[] args) {
        Log4j.info(name+" args = " + Arrays.toString(args));
        _adapter = communicator.createObjectAdapter(name);
        serverName = name;
        rpcGroupName = args.length >=1 ? args[0] : null;

        boolean isPushServer = args.length >= 2 && Boolean.parseBoolean(args[1]);
        String packagePath = args.length >= 3 ? args[2] : "";

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
        //启动服务
        startService(communicator,isPushServer,packagePath);
        //激活适配器
        _adapter.activate();
    }

    //初始化服务
    private void initServer(Communicator communicator,String packagePath) {
        try {
            long time = System.currentTimeMillis();
            scanJarAllClass(packagePath,skipInitializeScanArr);
            initialization(communicator);
            Log4j.info("服务初始化耗时:"+ (System.currentTimeMillis() - time)+"ms");
        } catch (Exception e) {
            Log4j.error("服务初始化错误",e);
        }
    }

    //扫描jar包内的所有class文件
    private void scanJarAllClass(String packagePath,String... skipEndingArr){
        try {
            File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName();
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    try {
                        String classFullName = name.replace("/",".").replace(".class","");
                        if (classFullName.startsWith(packagePath)){
                            if (skipEndingArr != null && skipEndingArr.length>0){
                                boolean skip = false;
                                for (String ending : skipEndingArr){
                                    if (classFullName.endsWith(ending)){
                                        skip = true;
                                        //System.out.println("跳过扫描: " + classFullName);
                                        break;
                                    }
                                }
                                if (skip) continue;
                            }
                            Class<?> classType = Class.forName(classFullName);
                            findJarAllClass(classType);
                        }
                    } catch (Exception e) {
                        Log4j.error("扫描类文件(" + name+ ")错误",e);
                    }
                }
            }
        } catch (Exception e) {
//            Log4j.error("扫描所有类文件错误",e);
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

    private void findJarAllClass(Class<?> classType) throws Exception {
        if ( !classType.equals(Initializer.class) && Initializer.class.isAssignableFrom(classType)){
            initializerList.add(((Initializer) classType.newInstance()));
        }
        service.findJarAllClass(classType);
    }

    protected void initialization(Communicator communicator) {
        initializerList.sort(Comparator.comparingInt(Initializer::priority));
        for (Initializer o : initializerList){
            try {
                o.initialization(serverName,rpcGroupName,communicator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startService(Communicator communicator,boolean isPushMessage, String packagePath) {
        service.launchService(communicator,serverName,isPushMessage,packagePath);
    }

    @Override
    public void stop() {
        for (Initializer it : initializerList){
            it.onDestroy();
        }
        service.stopService();
        _adapter.destroy();
        Log4j.info(serverName + " 服务销毁");
    }

}
