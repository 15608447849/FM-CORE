package bottle.util;

import java.util.HashMap;

public class ClassInstanceStorage {

    // 类-实例 存储
    private static final HashMap<Class<?>, Object> instanceMap = new HashMap<>();
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<?> classType) throws IllegalAccessException, InstantiationException {
        Object object = instanceMap.get(classType);
        if (object == null){
            object =  classType.newInstance();
            instanceMap.put(classType,object);
//            System.out.println("ICE ClassInstanceStorage CREATE : " + classType + " -> " + object);
        }
        return (T) object;
    }

}
