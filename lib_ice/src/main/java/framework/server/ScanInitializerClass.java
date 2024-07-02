package framework.server;

import Ice.Communicator;
import bottle.util.ClassInstanceStorage;
import bottle.util.Log4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ScanInitializerClass implements ScanApplicationClassCallback{


    // 加载初始化服务列表
    private final List<Initializer> initializerList = new ArrayList<>();

    @Override
    public void findClass(Class<?> classType) throws Exception {
        if ( !classType.equals(Initializer.class) && Initializer.class.isAssignableFrom(classType)){
            Initializer initializer = ClassInstanceStorage.getInstance(classType);
            Log4j.info("加入 服务初始化加载器: "+ initializer +" 排序: " + initializer.priority());
            initializerList.add(initializer);
        }
        if ( !classType.equals(IceCallerObserver.IceCallerSubscribeI.class) && IceCallerObserver.IceCallerSubscribeI.class.isAssignableFrom(classType)){
            IceCallerObserver.IceCallerSubscribeI subscribe = ClassInstanceStorage.getInstance(classType);
            Log4j.info("加入 服务方法调用订阅器: "+ subscribe );
            IceCallerObserver.addSubscribe(subscribe);
        }
    }

    @Override
    public void executeClass(String serverName, String rpcGroupName, Communicator communicator) {
        initializerList.sort(Comparator.comparingInt(Initializer::priority));
        for (Initializer o : initializerList){
            try {
                long s = System.currentTimeMillis();
                o.initialization(serverName,rpcGroupName,communicator);
                Log4j.info(" Initializer = "+ o.getClass().getName() +" initialization() time: "+ (System.currentTimeMillis() - s)+" ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        for (Initializer it : initializerList){
            it.onDestroy();
        }
    }

    @Override
    public ScanApplicationClassCallback next() {
        return null;
    }
}
