package bottle.objectref;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * 对象反射工具
 * 创建对象
 * 调用对象方法
 * 静态方法不用考虑并发
 *
 */
public class ObjectRefUtil {

    private ObjectRefUtil(){ }

    /**
     * 反射创建对象
     */
    @SuppressWarnings("unchecked")
    public static Object createObject(Class cls,Class[] parameterTypes,Object... parameters) throws Exception{
        Constructor constructor;
        try {
            constructor = cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            constructor = cls.getDeclaredConstructor(parameterTypes);
        }
        constructor.setAccessible(true);
        return constructor.newInstance(parameters);
    }

    /**
     * 反射创建对象
     */
    public static Object createObject(String classPath,Class[] parameterTypes,Object... parameters) throws Exception{
            Class clazz = Class.forName(classPath);
            return createObject(clazz,parameterTypes,parameters);
    }

    /**
     * 反射创建对象
     */
    public static Object createObject(String classPath) throws Exception{
        Class clazz = Class.forName(classPath);
        return createObject(clazz,null);
    }

    /*
    getDeclaredMethod*()获取的是类自身声明的所有方法,包含public、protected和private方法
    getMethod*()获取的是类的所有共有方法,这就包括自身的所有public方法,和从基类继承的、从接口实现的所有public方法
    */

    /**
     * 反射调用某个类实例方法
     */
    public static Object callMethod(Object holder,Method method,Class[] parameterTypes,Object... parameters) throws Exception{
        return method.invoke(holder,parameters);//调用对象的方法
    }

    /**
     * 反射调用某个类实例公共方法
     */
    public static Object callMethod(Object holder,String methodName,Class[] parameterTypes,Object... parameters) throws Exception{
        Method method = holder.getClass().getMethod(methodName,parameterTypes);
        return callMethod(holder,method,parameterTypes,parameters);//调用对象的方法
    }

    /**
     * 反射调用某个类实例公共方法
     */
    public static Object callMethod(String classPath,String methodName,Class[] parameterTypes,Object... parameters) throws Exception{
        Object holder = createObject(classPath);
        return callMethod(holder,methodName,parameterTypes,parameters);//调用对象的方法
    }


    /**
     * 反射调用某个类实例
     * 所有方法 包含私有不包含父类
     */
    public static Object callDeclaredMethod(Object holder,String methodName,Class[] parameterTypes,Object... parameters) throws Exception{
        Method method = holder.getClass().getDeclaredMethod(methodName,parameterTypes);
        return callMethod(holder,method,parameterTypes,parameters);//调用对象的方法
    }


    /**
     * 反射调用某个类实例
     * 所有方法 包含私有不包含父类
     */
    public static Object callDeclaredMethod(String classPath,String methodName,Class[] parameterTypes,Object... parameters) throws Exception{
        Object holder = createObject(classPath);
        return callDeclaredMethod(holder,methodName,parameterTypes,parameters);//调用对象的方法
    }

    /**
     * 反射调用某个类静态方法
     */
    public static Object callStaticMethod(String classPath,String methodName,Class[] parameterTypes,Object... parameters) throws Exception{
        Method method = Class.forName(classPath).
                getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(null, parameters);
    }

}
