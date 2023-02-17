package bottle.objectref;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * leezp
 * 动态加载类
 * */
public class DynamicLoadClassUtil {

    private DynamicLoadClassUtil(){ }

    public static Set<Class<?>> scanCurrentAllClassBySpecPath(String packagePath,Class<?> specType){
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = packagePath.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if (protocol == null) continue;
                if (protocol.equals("file")){
                    scanClassBySpecFile(packagePath,url.getFile(),classes,specType);
                }
                if (protocol.equals("jar")){
                    scanClassBySpecJar(packagePath,url,classes,specType);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return classes;
    }

    /** 扫描指定类 从指定文件 */
    private static void scanClassBySpecFile(String packageName, String filePath, Set<Class<?>> classes,Class<?> specType) throws Exception {
        String packagePath = URLDecoder.decode(filePath, "UTF-8");
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory())  return;

        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()){
                    try {
                        scanClassBySpecFile(packageName,file.getPath(),classes,specType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // .class结尾的文件(编译好的java类文件)
                return file.getName().endsWith(".class");
            }
        });

        if (files == null) return;

        for (File clsFile : files) {
            try{
                String classPath = clsFile.getPath().replace(File.separator,".").replace(packageName,"!");
                int index = classPath.indexOf("!");
                if (index<=0) continue;

                String className = classPath.substring(index+2, classPath.length() - 6);
                Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                if (Modifier.isInterface(cls.getModifiers()) || Modifier.isAbstract(cls.getModifiers())) continue;

                if (specType!=null){
                    if (specType.isAssignableFrom(cls)){
                        classes.add(cls);
                    }
                }else{
                    classes.add(cls);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /** 扫描指定类 从jar包 */
    private static void scanClassBySpecJar(String packageName,URL url, Set<Class<?>> classes,Class<?> specType) throws Exception {
        try {

            JarURLConnection jarURLConnection  = (JarURLConnection )url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();

                if(jarEntry.getName().endsWith(".class")){
                    String classPath = jarEntryName.replace("/", ".").replace(".class", "");
                    if (classPath.startsWith(packageName)){
                        Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(classPath);
                        if (Modifier.isInterface(cls.getModifiers()) || Modifier.isAbstract(cls.getModifiers())) continue;

                        if (specType!=null){
                            if (specType.isAssignableFrom(cls)){
                                classes.add(cls);
                            }
                        }else{
                            classes.add(cls);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Class<?> loadClass(ClassLoader loader,String classPath) {
        if (loader==null) return null;
        Class<?> clazz = null;
        try {
            clazz = loader.loadClass(classPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (clazz == null){
            return loadClass(loader.getParent(),classPath);
        }
        return clazz;
    }


}
