package framework.server;

import Ice.Object;
import Ice.*;
import IceBox.Service;
import bottle.objectref.ObjectRefUtil;
import bottle.util.Log4j;

import java.lang.Exception;
import java.util.Arrays;

import static Ice.Application.communicator;

public abstract class IceBoxServerAbs implements Service {

    private ObjectAdapter _adapter;
    Communicator _communicator;

    @Override
    public void start(String name, Communicator communicator, String[] args) {

        Log4j.info("服务组: " + Arrays.toString(args));
        String repGroup = args.length >=1 ? args[0] : null;
        boolean isPushServer = args.length >= 2 && Boolean.parseBoolean(args[1]);
        String packagePath = args.length >= 3 ? args[2] : "";
        initIceLogger(name,(CommunicatorI) communicator);
        _communicator = communicator;
        _adapter = _communicator.createObjectAdapter(name);
        //关联servant
        relationID(name,communicator,repGroup);
        //初始化应用
        initApplication(packagePath);
        //启动服务
        startService(isPushServer,packagePath);
        //激活适配器
        _adapter.activate();
    }

    //初始化 系统应用
    private void initApplication(String packagePath) {
        try {
            long time = System.currentTimeMillis();
            ObjectRefUtil.scanJarAllClass(classPath -> {
                try {
                    if (classPath.startsWith(packagePath)){
                        findJarAllClass(classPath);
                    }
                } catch (Exception ignored) { }

            });
            initialization();
            _communicator.getLogger().print("应用初始化耗时:"+ (System.currentTimeMillis() - time)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initIceLogger(String name,CommunicatorI ic) {
        Logger logger = ic.getInstance().initializationData().logger;
        if (!(logger instanceof IceLog4jLogger)){
            ic.getInstance().initializationData().logger = new IceLog4jLogger(name);
            initIceLogger(name, (CommunicatorI) communicator());
        }
    }

    private void relationID(String serverName,Communicator communicator,String groupName) {
        //创建servant
        Ice.Object object = specificServices(serverName);
        Identity identity = communicator.stringToIdentity(serverName);
        _adapter.add(object,identity);
        //配置rpc组信息
        if (groupName == null || groupName.length()==0) return ;
        identity = communicator.stringToIdentity(groupName);
        _adapter.add(object,identity);
        addRpcGroup(groupName);
        _communicator.getLogger().print("服务: "+serverName +" ,加入组: " + groupName);
    }

    protected abstract Object specificServices(String serviceName);

    //sub imps
    protected abstract void addRpcGroup(String rpcName);

    protected abstract void findJarAllClass(String classPath) throws Exception;

    protected abstract void initialization();

    protected abstract void startService(boolean isPushMessage, String packagePath);

    @Override
    public void stop() {
        _adapter.destroy();
        _communicator.getLogger().print("服务销毁");
    }

}
