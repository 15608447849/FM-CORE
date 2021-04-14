import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: leeping
 * @Date: 2020/9/8 11:41
 */
public class HotLoadTest {
    public static void main(String[] args) throws Exception{
        System.out.println("系统默认的AppClassLoader: "+ClassLoader.getSystemClassLoader());
        System.out.println("AppClassLoader的父类加载器: "+ClassLoader.getSystemClassLoader().getParent());
        System.out.println("ExtClassLoader的父类加载器: "+ClassLoader.getSystemClassLoader().getParent().getParent());

        String libDirtPath = "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\libs";
        loadLibDict(libDirtPath);
        Object obj = Class.forName("lee.zp.DyLoadTest");
        Method instanceExecute = obj.getClass().getMethod("instanceExecute");
        instanceExecute.invoke(obj);
        Thread.sleep(100000);
    }

    private static void loadLibDict(String libDirtPath) {

        File libDirt = new File(libDirtPath);
        if (!libDirt.exists() || !libDirt.isDirectory()) return;

        // 获取所有的.jar
        File[] jarFiles = libDirt.listFiles((dir, name) -> name.endsWith(".jar"));

        loadJarFiles(jarFiles);

    }

    private static void loadJarFiles(File[] jarFiles) {
        if (jarFiles == null || jarFiles.length == 0) return;

        //对于jar文件，可以理解为一个存放class文件的文件夹
        for (File jarFile : jarFiles){
            loadJarFile1(jarFile);
        }
    }

    private static void loadJarFile1(File file){
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            boolean accessible = method.isAccessible();		// 获取方法的访问权限
            if (!accessible) {
                method.setAccessible(true);		// 设置方法的访问权限
            }
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = file.toURI().toURL();
            method.invoke(classLoader, url);
            System.out.println("加载JAR文件成功, " + file);

        } catch (Exception e) {
            System.out.println("加载JAR文件失败, " + file+", "+e);
        }
    }
}
