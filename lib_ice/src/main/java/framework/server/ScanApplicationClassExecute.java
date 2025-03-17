package framework.server;

import Ice.Communicator;
import bottle.util.Log4j;
import bottle.util.TimeTool;

import java.util.ArrayList;
import java.util.List;

public class ScanApplicationClassExecute {
    public static final List<String> skipInitializeScanSuffixList = new ArrayList<String>(){{
        add("Util");add("Utils");add("Tool");add("Tools");
    }};

    protected static void scanRunClass(String classFullName,ScanApplicationClassCallback callback){
        try {
            if (classFullName.lastIndexOf("$")!=-1 || classFullName.contains(".server.inf.")) return;

            for (String suffix : skipInitializeScanSuffixList){
                if (classFullName.endsWith(suffix)) return;
            }

            long a = System.currentTimeMillis();
            Class<?> classType = Class.forName(classFullName);
            long b = System.currentTimeMillis();
            if ( ( b - a ) > 1000)  Log4j.info((b - a) + "类加载超时 Class.forName( "+classFullName+" ) use time: " +  TimeTool.formatDuring(b - a));
            findClass(callback,classType);
        } catch (Exception e) {
            Log4j.error("扫描类文件(" + classFullName+ ")错误",e);
        }
    }

    private static void findClass(ScanApplicationClassCallback callback, Class<?> classType) throws Exception {
        if (classType.isInterface()) return;
        callback.findClass(classType);
        if (callback.next()!=null){
            findClass(callback.next(),classType);
        }
    }

    public static void startRunClass(ScanApplicationClassCallback callback, String serverName, String rpcGroupName, Communicator communicator) {
        callback.executeClass(serverName,rpcGroupName,communicator);
        if (callback.next()!=null){
            startRunClass(callback.next(),serverName,rpcGroupName,communicator);
        }
    }

    public static void stopRunClass(ScanApplicationClassCallback callback) {
        callback.destroy();
        if (callback.next()!=null){
            stopRunClass(callback.next());
        }
    }


    public void executeClass(String serverName, String rpcGroupName, Communicator communicator) {

    }


}
