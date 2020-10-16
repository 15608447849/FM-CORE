import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: leeping
 * @Date: 2020/9/8 15:08
 */
public class main {

    public static void main(String[] args) throws Exception{

//        LClassLoader loader = new LClassLoader(
//                "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\libs",
//                "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\classes\\java\\main"
//        );
//        Class<?>  clazz =
//                loader.findClass("lee.zp.DyLoadTest");

        while (true){
            try {
                LClassLoader loader = new LClassLoader(
                        "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\libs",
                        "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\classes\\java\\main"
                );
//        Class<?>  clazz =
                loader.findClass("lee.zp.DyLoadTest");

                Object obj = Class.forName("lee.zp.DyLoadTest").newInstance();
                Method instanceExecute = obj.getClass().getMethod("instanceExecute");
                instanceExecute.invoke(obj);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
        }

    }
}
