package bottle.properties.watch;

import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.tuples.Tuple2;
import bottle.tuples.Tuple3;
import bottle.util.Log4j;
import com.sun.nio.file.SensitivityWatchEventModifier;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesWatch {
    private static final Map<String, List<Tuple3<Class<?>,String,ApplicationPropertiesBase.ApplicationPropertiesCallback>>> targetMap = new HashMap<>();
    private static final Map<String,Thread> threadMap = new HashMap<>();

    public static File getOutResourcesDirectory(Class<?> clazz) {
        try {
            File file = new File(URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(),"UTF-8"));
            String resourceName = "resources";
            if (file.getPath().contains("build")&&file.getPath().contains("classes")&&file.getPath().contains("java")){
                resourceName += "/"+file.getName();
                file =  file.getParentFile().getParentFile().getParentFile();
            }
            if (file.getName().endsWith(".jar")) file = file.getParentFile();
            File dirc = new File(file,resourceName);
            if (dirc.exists()){
                return dirc;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean addDirectory(Class<?> clazz){
        try {

            File resources = getOutResourcesDirectory(clazz);
            if (resources == null) return false;

            String resourcesPath = resources.getCanonicalPath();

            if (threadMap.containsKey(resourcesPath)) return true;

            Log4j.info("新增监听 Properties配置文件目录 : "+ resourcesPath);

            final WatchService watcher = FileSystems.getDefault().newWatchService();
            resources.toPath().register(watcher,
                    new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY},
                    SensitivityWatchEventModifier.LOW);

            Thread watchThread = new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = watcher.take();

                        for (WatchEvent<?> event : key.pollEvents()) {

                            if (event.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                            Path path = (Path) event.context();
                            if (path == null) continue;
                            String fileName = path.toFile().getName();
                            if (targetMap.containsKey(fileName) && event.count()==1){
                                List<Tuple3<Class<?>,String,ApplicationPropertiesBase.ApplicationPropertiesCallback>> list = targetMap.get(fileName);
                                for (Tuple3<Class<?>,String,  ApplicationPropertiesBase.ApplicationPropertiesCallback> tuple : list) {
                                    Log4j.info("监听配置文件改变 " + event.count()+" , "+ event.kind()+ " 配置文件存在更新: " + fileName + " 更新类: "+ tuple.getValue0().getName() );
                                    ApplicationPropertiesBase.initStaticFields(tuple.getValue0(),tuple.getValue1(),tuple.getValue2(),false);
                                }
                            }
                        }
                        if (!key.reset()) {
                            Log4j.info("配置文件监听异常终止");
                            break;
                        }

                    }
                } catch (Exception e) {
                    Log4j.error("配置文件监听错误",e);
                }
            });

            watchThread.setDaemon(true);
            threadMap.put(resourcesPath,watchThread);
            watchThread.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void addTarget(String fileName, String propPrev, Class<?> clazz, ApplicationPropertiesBase.ApplicationPropertiesCallback callback){
        if (addDirectory(clazz)){
            List<Tuple3<Class<?>,String,ApplicationPropertiesBase.ApplicationPropertiesCallback>> list = targetMap.computeIfAbsent(fileName, k -> new ArrayList<>());
            for (Tuple3<Class<?>, String, ApplicationPropertiesBase.ApplicationPropertiesCallback> it : list) {
                if (it.getValue0().getName().equals(clazz.getName())) return;
            }
            list.add(new Tuple3<>(clazz,propPrev,callback));
            Log4j.info("添加监听对象: " + fileName+" <-> "+ clazz+"  ");
        }
    }
}
