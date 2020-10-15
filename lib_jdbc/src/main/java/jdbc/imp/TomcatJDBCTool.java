package jdbc.imp;

import com.google.gson.*;
import jdbc.define.log.JDBCLogger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: leeping
 * @Date: 2020/2/20 13:41
 */
public final class TomcatJDBCTool {

    private TomcatJDBCTool(){};

    /**
     * 打印 行/列
     */
    public static void printLines(List<Object[]> lines) {
        StringBuilder sb = new StringBuilder("打印查询结果,总条数:"+lines.size()+":\n");

        for (int i = 0; i < lines.size(); i++) {
            sb.append(i).append("\t").append(arraysToString(lines.get(i)));
            if (i != lines.size()-1) {
                sb.append("\n");
            }
        }

        JDBCLogger.print(sb.toString());
    }

    public static String arraysToString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(" | ");
        }
    }

    /**
     * 转换为LINK 子句
     */
    public static Object[] convertLikeValue(Object[] params) {
        for (int i = 0; i < params.length; i++) {
            params[i] = convertLikeValue(params[i]);
        }
        return params;
    }

    /**
     * 转换为LINK 子句
     */
    public static Object convertLikeValue(Object param) {
        if (param == null) param = "";
        return "%" + param + "%";
    }

    /**
     * 转换基本类型为包装类型
     */
    private static Class<?> baseTypeConvertWrapType(Class clazz) {
        if (clazz == byte.class) {
            return Byte.class;
        }
        if (clazz == short.class) {
            return Short.class;
        }
        if (clazz == int.class) {
            return Integer.class;
        }
        if (clazz == long.class) {
            return Long.class;
        }
        if (clazz == boolean.class) {
            return Boolean.class;
        }
        if (clazz == float.class) {
            return Float.class;
        }
        if (clazz == double.class) {
            return Double.class;
        }
        if (clazz == char.class) {
            return Character.class;
        }
        return clazz;
    }

    /**
     * 转换对象为执行类类型
     */
    static Object convertStringToBaseType(Object val, Class<?> cls) {
        try {

            if (cls == String.class) return String.valueOf(val);

            if (val.getClass() == cls) {
                return val;
            }

            if (cls == BigDecimal.class) return new BigDecimal(String.valueOf(val));

            if (cls == BigInteger.class) return new BigInteger(String.valueOf(val));

            String className = cls.getSimpleName();
            if (className.equals("Integer")) className = "int";
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            String methodName = "parse" + className;

            String v = String.valueOf(val);
            Method method = baseTypeConvertWrapType(cls).getMethod(methodName, String.class);
            Object res = method.invoke(null, v);
            return res;
        } catch (Exception e) {
            JDBCLogger.error("类型转换错误,val=" + val + ",cls=" + cls, e);
        }
        return null;
    }

    /**
     * 转换Object为执行类型值
     */
    public static <T> T convertObjectToSpecType(Object object, T def) {
        try {
            if (object != null) {
                Object value = convertStringToBaseType(object, def.getClass());
                if (value != null) return (T) value;
            }
        } catch (Exception e) {
            JDBCLogger.error("类型转换错误,object=" + object + ",def=" + def, e);
        }
        return def;
    }

    /**
     * 转换Object为String类型
     */
    public static String convertObjectToString(Object object) {
        return convertObjectToSpecType(object, "");
    }

    /** 列表 转 字符串,各项逗号隔开 */
    public static String join(List list) {
       return join(list,",");
    }

    /** 列表 转 字符串,各项逗号隔开 */
    public static String join(List list,String symbol) {
        if (list == null || list.size() == 0) return "";
        StringBuilder result=new StringBuilder();
        for (Object obj : list){
            result.append(obj).append(symbol);
        }
        if (result.length()>0) return result.deleteCharAt(result.length()-1).toString();
        return "";
    }

    /* 打印sql 原句*/
    public static void printOriginal(String... sqlArray){
        for (String sql : sqlArray){
            JDBCLogger.print(sql.replaceAll("\\{\\{\\?","").replaceAll("\\}\\}",""));
        }
    }

    /* 转换object数组->字符串数组 */
    private static String[] convertObjectArray(Object[] objectArray){
//        System.out.println("object[]转换string[]: "+ Arrays.toString(objectArray) );
        if (objectArray == null) return null;
        String[] stringArray = new String[objectArray.length];
        for (int i = 0;i<objectArray.length;i++){

            if (objectArray[i]==null){
                stringArray[i] = "";
            }else{
                stringArray[i] = String.valueOf(objectArray[i]);
            }
        }
        return stringArray;
    }

    private static List<String[]> convertObjectArray(List<Object[]> objectArrayList){
        if (objectArrayList == null) return null;
        List<String[]> stringArrayList = new ArrayList<>();
        for (Object[] objectArray : objectArrayList){
            stringArrayList.add(convertObjectArray(objectArray));
        }
        return stringArrayList;
    }

    /*  转换字符串数组->object数组 */
    private static Object[] convertStringArray(String[] stringArray){
        if (stringArray == null) return null;
//        System.out.println("string[]转换object[]: "+ Arrays.toString(stringArray) );
        Object[] objectArray = new Object[stringArray.length];
        for (int i = 0;i<stringArray.length;i++){
            objectArray[i] = stringArray[i];
        }
        return objectArray;
    }

    private static List<Object[]> convertStringArray(List<String[]> stringArrayList){
        if (stringArrayList == null) return null;
        List<Object[]> objectArrayList = new ArrayList<>();
        for (String[] stringArray : stringArrayList){
            objectArrayList.add(convertStringArray(stringArray));
        }
        return objectArrayList;
    }

    /* 转换参数列表 null字符串->null对象 */
    private void convertNullString(List<Object[]> paramList) {
        if (paramList!=null){
            for (Object[] objects : paramList){
                if (objects!=null){
                    for (int i = 0 ; i<objects.length;i++){
                        Object var = objects[i];
                        if (var instanceof String){
                            if (var.equals("null")){
                                objects[i] = null;
                            }
                        }
                    }
                }
            }
        }
    }

}
