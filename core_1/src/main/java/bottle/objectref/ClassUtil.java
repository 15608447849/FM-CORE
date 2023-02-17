package bottle.objectref;



import bottle.util.Log4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;

import java.util.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: leeping
 * @Date: 2020/3/27 14:12
 */
public class ClassUtil {

    private static void addClassToClassSet(Set<Class<?>> classes,String classPath){
        try {
            classes.add(
                    Thread.currentThread().getContextClassLoader().loadClass(classPath)
            );
        } catch (Exception e) {
            Log4j.error(e);
        }
    }

    /**
     * 以class文件的形式来获取包下的所有Class
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String filePath, Set<Class<?>> classes) {
        // 获取包的目录 建立一个File
        File dir = new File(filePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) return;

        // 如果存在 就获取包下的所有文件 包括目录
        File[] files = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 子目录或则是以.class结尾的文件
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".class");
            }
        });


        // 循环所有文件
        assert files != null;
        for (File file : files) {
            // // 如果是类文件
            if (file.isFile()){
                // 去掉后面的.class 只留下类名
                String className = file.getName().replace(".class","");
                addClassToClassSet(classes,packageName + '.' + className);
            }
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile( packageName + "."+dir.getName() , file.getAbsolutePath(), classes);
            }
        }
    }

    /**
     * 以运行时Jar的形式来获取包下的所有Class
     */
    private static void findAndAddClassesInPackageByJarFile(String packageName, URL jarURL, Set<Class<?>> classes) {
        try (JarFile jarFile = ((JarURLConnection) jarURL.openConnection()).getJarFile()){
//            Log4j.info("findAndAddClassesInPackageByJarFile ... ");
            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jarFile.entries();
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                try {
                    // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                    JarEntry entry = entries.nextElement();
                    if (entry.isDirectory()) continue;
                    String name = entry.getName().replace("/",".");
                    if (!name.startsWith(packageName) || !name.endsWith(".class")) continue;

//                    Log4j.info("findAndAddClassesInPackageByJarFile name " + name);

                    String classPath = name.replace(".class","");
                    addClassToClassSet(classes,classPath);
                } catch (Exception e) {
                    Log4j.error(e);
                }
            }
//            Log4j.info("findAndAddClassesInPackageByJarFile !!! " );
        } catch (Exception e) {
            Log4j.error(e);
        }

    }

    /**
     * 当前运行时jar包下所有类
     * */
    public static Set<Class<?>> getCurrentRuntimeClasses(String packagePath){
        Set<Class<?>> classes = new LinkedHashSet<>();
        try {
            Enumeration<URL> urls
                    = Thread.currentThread().getContextClassLoader().getResources(packagePath.replace(".","/"));
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
//                Log4j.info("运行时环境经 Thread.currentThread().getContextClassLoader().getResources url = " + url);
                if (url.getProtocol().equals("jar")){
                    findAndAddClassesInPackageByJarFile(packagePath,url,classes);
                }
                if (url.getProtocol().equals("file")){
                    findAndAddClassesInPackageByFile(packagePath,URLDecoder.decode(url.getFile(),"UTF-8"),classes);
                }
            }

        } catch (Exception e) {
            Log4j.error(e);
        }
//        Log4j.info("获取运行时环境 class size = "+ classes.size());
        return classes;
    }

    private final static Map<String,Set<Class<?>>> currentRuntimeClassMap = new HashMap<>();
    public static Set<Class<?>> getClasses(String  packagePath){
        Set<Class<?>> set = currentRuntimeClassMap.get(packagePath);
        if (set == null){
            set = getCurrentRuntimeClasses(packagePath);
            if (!set.isEmpty()){
                currentRuntimeClassMap.put(packagePath,set);
            }
        }
        return set;
    }



    /* 转换jar包路径 */
    private static URL convertJarURL(String url) throws MalformedURLException {
        if (!url.startsWith("jar:")){
            url = String.format("jar:%s!/",url);
        }
        return new URL(url);
    }

    /** 加载jar文件 */
    public static boolean hotLoadJarFile(String url){
        try {
            URL jarUrl = convertJarURL(url);
            Log4j.info("加载: " + jarUrl);

            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            boolean accessible = method.isAccessible();		// 获取方法的访问权限
            if (!accessible) {
                method.setAccessible(true);		// 设置方法的访问权限
            }
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            method.invoke(classLoader, jarUrl);
            System.out.println("热加载JAR文件成功, " + jarUrl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 扫描jar文件 */
    public static Set<String> scanJarFileOnGetClassPaths(String url, String prefix) {
        // 类的集合
        Set<String> classes = new LinkedHashSet<>();
        try {
            // 获取url
            URL jarUrl = convertJarURL(url);
            System.out.println("扫描: " + jarUrl);
            // 获取jar
            try(JarFile jar = ((JarURLConnection) jarUrl.openConnection()).getJarFile()){
                scanJarFileHandle(jar,prefix,classes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void scanJarFileHandle(JarFile jar, String prefix, Set<String> classes) {
        try {
            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;

                String name = entry.getName().replace("/",".");
                if (!name.endsWith(".class")) continue;
                if (prefix!=null && !name.startsWith(prefix)) continue;
                String classPath = name.replace(".class","");
                classes.add(classPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
//        File file = new File(ClassUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        File file = new File("D:\\A_Java\\JavaProjects\\IDEAWORK\\erp\\Z_SERVERSTART\\lib\\erp-global.jar");
        System.out.println(file);
        System.out.println(file.toURI().toASCIIString());

//        Set<String> classes = scanJarFileOnGetClassPaths("file:///D:\\A_Java\\JavaProjects\\IDEAWORK\\erp\\Z_SERVERSTART\\lib\\erp-global.jar","drug.erp");
        Set<String> classes = scanJarFileOnGetClassPaths(file.toURI().toASCIIString(),"drug.erp");
        System.out.println(classes.size());

//        hotLoadJarFile("file:///D:\\A_Java\\JavaProjects\\IDEAWORK\\erp\\Z_SERVERSTART\\lib\\erp-global.jar");
//        for (String aClass : classes) {
//            Class<?> aClass1 = Class.forName(aClass);
//            System.out.println(aClass1);
//        }

    }

}
