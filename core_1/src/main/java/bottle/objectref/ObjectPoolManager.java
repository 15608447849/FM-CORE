package bottle.objectref;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * lzp
 * 对象池
 * 随系统运行则启动
 * 单例
 */
public class ObjectPoolManager extends Thread{

    //线程安全的池 >> 类全路径 - 可用的对象的矢量池
    private final LinkedHashMap<String, ObjectPool> poolMap = new LinkedHashMap<>(new LinkedHashMap<>());

    private ObjectPoolManager(){
        setDaemon(true);
        setName("object-pool-msg-"+getId());
        start();
    }

    private static class Holder{
        private static ObjectPoolManager INSTANCE = new ObjectPoolManager();
    }

    //获取实例
    public static ObjectPoolManager get(){
        return Holder.INSTANCE;
    }

    @Override
    public void run() {
        //间隔执行 清理对象池
        while (true){
            try {
                //最大清理时间
                long maxClearTime = 2 * 60 * 60 * 1000L;
                sleep(maxClearTime/2);
                Iterator<Map.Entry<String, ObjectPool>> iterator = poolMap.entrySet().iterator();
                Map.Entry<String, ObjectPool> entry;
                while (iterator.hasNext()){
                    entry = iterator.next();
                    if (entry.getValue().checkSelf(maxClearTime)){
                        iterator.remove();
                    }
                }
            } catch (Exception e) { e.printStackTrace();}
        }
    }

    /**
     * 获取一个池中缓存的对象
     */
    private Object getObject(Class objectClass){
        String classPath = objectClass.getName();
        ObjectPool pool = poolMap.get(classPath);
        if (pool == null){
            return null;
        }
        return pool.getObject();
    }

    /**
     * 存入一个对象
     */
    private void putObject(Object object){
        String classPath = object.getClass().getName();
        ObjectPool pool = poolMap.get(classPath);
        if (pool==null){
            pool = new ObjectPool(15000);
            poolMap.put(classPath,pool);
        }
        pool.putObject(object);
    }

    public static Object createObject(Class cls) throws Exception {
        //对象池中获取对象
        Object obj = ObjectPoolManager.get().getObject(cls);
        //不存在则创建
        if (obj == null)  obj = ObjectRefUtil.createObject(cls,null);
        return obj;
    }

    public static void destroyObject(Object obj){
        //使用完毕之后再放入池中,缓存对象
        ObjectPoolManager.get().putObject(obj);
    }



}
