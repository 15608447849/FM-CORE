import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: leeping
 * @Date: 2020/9/8 14:25
 */
public class LClassLoader  extends  ClassLoader {

    private final File jarDir;

    private final File classDir;

    ClassLoader classLoader;

    public LClassLoader(String jarDir,String classDir) {
        this.jarDir = new File(jarDir);
        this.classDir = new File(classDir);
        classLoader = getSystemClassLoader();
        System.out.println(classLoader);
//        addThisToParentClassLoader(classLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 获取类的class文件字节数组
        byte[] classData = getClassData(name);
        if (classData == null) {

            Class<?> clazz = classLoader.loadClass(name);
            if (clazz ==null){
                throw new ClassNotFoundException(name);
            }
            return clazz;
        } else {
            //直接生成class对象
            Class<?> clazz = defineClass(name, classData, 0, classData.length);

            try {
                ClassLoader classLoader = getSystemClassLoader();
                Method method = ClassLoader.class.getDeclaredMethod("defineClass",String.class, byte[].class, int.class, int.class);
                method.setAccessible(true);
                method.invoke(classLoader, name, classData, 0, classData.length);
                System.out.println("加载成功");
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("加载失败");
                System.gc();
            }

            return clazz;
        }
    }

    private byte[] getClassData(String className) {
        //查询
        System.out.println(className);

        // 查询 class下是否存在文件
        String _path = className.replace(".","/") + ".class";
        File file = new File(classDir,_path);

        if (file.exists()){
            try(InputStream ins = new FileInputStream(file)){

                try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                    int bufferSize = 4096;
                    byte[] buffer = new byte[bufferSize];
                    int bytesNumRead = 0;
                    // 读取类文件的字节码
                    while ((bytesNumRead = ins.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesNumRead);
                    }
                    return baos.toByteArray();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 将this替换为指定classLoader的parent ClassLoader
     *
     * @param classLoader
     */
    private void addThisToParentClassLoader(ClassLoader classLoader) {
        try {
            Field field = ClassLoader.class.getDeclaredField("parent");
            field.setAccessible(true);
            field.set(classLoader, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
