package bottle.properties.abs;



import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import bottle.properties.infs.FieldConvert;
import bottle.properties.infs.baseImp.*;
import bottle.properties.watch.ResourcesWatch;
import bottle.util.Log4j;
import bottle.util.StringUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * 读取属性文件
 */
public final class ApplicationPropertiesBase {

    private static final HashMap<String, FieldConvert> baseType = new HashMap<>();

    private static final Properties propertiesAll = new Properties();

    static {
        baseType.put("class java.lang.String",new StringConvertImp());
        baseType.put("boolean",new BooleanConvertImp());
        baseType.put("int",new IntConvertImp());
        baseType.put("float",new FloatConvertImp());
        baseType.put("double",new DoubleConvertImp());
        baseType.put("long",new LongConvertImp());
    }


    public interface ApplicationPropertiesCallback{
        void onAssignmentComplete(Properties properties);
    }

    public static Properties getPropertiesAll() {
        return propertiesAll;
    }

    public static String getPropertiesValueOnKey(String key,String defaultValue) {
        return propertiesAll.getProperty(key,defaultValue);
    }

    public static String getRuntimeRootPath(Class<?> clazz) {
        try {
            return new File(URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(),"UTF-8")).getParent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //读取配置文件
    public static InputStream readPathProperties(Class<?> clazz,String filePath) throws IOException {
        //优先从外部配置文件获取
        String dirPath = getRuntimeRootPath(clazz);
        if (dirPath != null){
            File file = new File(dirPath+"/resources"+filePath);
            if (file.exists()){
                return Files.newInputStream(file.toPath());
            }
        }
        return clazz.getResourceAsStream( filePath );
    }

    /**
     * 需要以 @PropertiesFilePath(/xxx) 设置目标类
     * */
    public synchronized static void initStaticFields(Class<?> clazz,String propPrev,ApplicationPropertiesCallback callback,boolean monitor) {
        try {
            PropertiesFilePath p =  clazz.getAnnotation(PropertiesFilePath.class);
            if (p==null) throw new RuntimeException("请使用注解 'PropertiesFilePath' 指定配置文件路径 : "+ clazz);
            String filePath = p.value();
            String charset = p.decode();

            if(!filePath.startsWith("/")){
                filePath = "/" + filePath;
            }

            InputStream in = readPathProperties(clazz,filePath);
            if (in==null) throw new RuntimeException("找不到Properties文件 : "+ clazz+" , " +filePath);
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in,charset));

            //匹配赋值
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                PropertiesName name = field.getAnnotation(PropertiesName.class);
                if (name==null) continue;
                field.setAccessible(true);
                String key = name.value();
                if (propPrev!=null && propPrev.length()>0) key = propPrev+"."+key;
                String value = properties.getProperty(key);
                if (value==null || value.length()==0) continue;
                assignmentByType(clazz,field,value);
            }

            //记录所有配置信息
            for (String key : properties.stringPropertyNames()) {
                propertiesAll.setProperty(key,properties.getProperty(key));
            }
            if(callback!=null) callback.onAssignmentComplete(properties);

            //加入变化监听
            if (monitor) ResourcesWatch.addTarget(filePath.substring(1),propPrev,clazz,callback);

        } catch (Exception e) {
            Log4j.warn("初始化配置文件失败," + e.getMessage());
        }
    }

    public synchronized static void initStaticFields(Class<?> clazz,String propPrev) {
        initStaticFields(clazz,propPrev,null,true);
    }

    public static void initStaticFields(Class<?> clazz,ApplicationPropertiesCallback callback) {
        initStaticFields(clazz,null,callback,true);
    }

    public static void initStaticFields(Class<?> clazz) {
        initStaticFields(clazz,null,null,true);
    }

    public static void initStaticFields(String propPrev,ApplicationPropertiesCallback callback) {
        try {
            Class<?> caller = Thread.currentThread().getContextClassLoader().loadClass( Thread.currentThread().getStackTrace()[2].getClassName());
            initStaticFields(caller,propPrev,callback,true);
        } catch (Exception e) {
            Log4j.info("初始化属性Prop失败: " , e.getMessage());
        }
    }

    public static void initStaticFields(ApplicationPropertiesCallback callback) {
        try {
            Class<?> caller = Thread.currentThread().getContextClassLoader().loadClass( Thread.currentThread().getStackTrace()[2].getClassName());
            initStaticFields(caller,null,callback,true);
        } catch (Exception e) {
            Log4j.info("初始化属性Prop失败: " , e.getMessage());
        }
    }

    public static void initStaticFields(String propPrev) {
        try {
            Class<?> caller = Thread.currentThread().getContextClassLoader().loadClass( Thread.currentThread().getStackTrace()[2].getClassName());
            initStaticFields(caller,propPrev,null,true);
        } catch (Exception e) {
            Log4j.info("初始化属性Prop失败: " , e.getMessage());
        }
    }

    public static void initStaticFields() {
        try {
            Class<?> caller = Thread.currentThread().getContextClassLoader().loadClass(Thread.currentThread().getStackTrace()[2].getClassName());
            initStaticFields(caller,null,null,true);
        } catch (Exception e) {
            Log4j.info("初始化属性Prop失败: " , e.getMessage());
        }
    }

    public static void assignmentByType(Object instanceClass, Field field, String value) {
        //获取属性类型
        String type = field.getGenericType().toString();
        if(baseType.containsKey(type)){
            try {
                baseType.get(type).setValue(instanceClass,field,value);
                String name;
                if (instanceClass instanceof Class){
                    name = ((Class)instanceClass).getName();
                }else{
                    name = instanceClass.getClass().getName();
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
