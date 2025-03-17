package framework.server;

import Ice.Communicator;
import bottle.util.ClassInstanceStorage;
import bottle.util.Log4j;
import bottle.util.TimeTool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ScanInitializerClass implements ScanApplicationClassCallback{


    // 加载初始化服务列表
    private final List<Initializer> initializerList = new ArrayList<>();

    @Override
    public void findClass(Class<?> classType) throws Exception {
        if ( !classType.equals(Initializer.class)
                && Initializer.class.isAssignableFrom(classType)){
            Initializer initializer = ClassInstanceStorage.getInstance(classType);
            Log4j.info("加入 服务初始化加载器: "+ initializer +" 排序: " + initializer.priority());
            initializerList.add(initializer);
        }
        if ( !classType.equals(IceCallerObserver.IceCallerSubscribeI.class)
                && IceCallerObserver.IceCallerSubscribeI.class.isAssignableFrom(classType)){
            IceCallerObserver.IceCallerSubscribeI subscribe = ClassInstanceStorage.getInstance(classType);
            Log4j.info("加入 服务方法调用订阅器: "+ subscribe );
            IceCallerObserver.addSubscribe(subscribe);
        }
    }

    @Override
    public void executeClass(String serverName, String rpcGroupName, Communicator communicator) {
        initializerList.sort(Comparator.comparingInt(Initializer::priority));
        for (Initializer it : initializerList){
            try {
                long s = System.currentTimeMillis();
                it.initialization(serverName,rpcGroupName,communicator);
                Log4j.info("执行 Initializer = "+ it +" time: "+ TimeTool.formatDuring(System.currentTimeMillis() - s) );
            } catch (Exception e) {
                Log4j.error(e);
            }
        }
    }

    @Override
    public void destroy() {
        for (Initializer it : initializerList){
            try {
                long s = System.currentTimeMillis();
                it.onDestroy();
                Log4j.info("结束 Initializer = "+ it +" time: "+ TimeTool.formatDuring(System.currentTimeMillis() - s) );
            } catch (Exception e) {
                Log4j.error(e);
            }
        }
    }

    @Override
    public ScanApplicationClassCallback next() {
        return null;
    }
}
