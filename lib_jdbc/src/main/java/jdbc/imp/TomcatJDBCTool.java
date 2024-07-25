package jdbc.imp;

import bottle.threadpool.IOSingerThreadPool;
import bottle.threadpool.IThreadPool;
import bottle.tuples.Tuple2;
import com.google.gson.*;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCUtils;
import jdbc.define.option.Page;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: leeping
 * @Date: 2020/2/20 13:41
 */
public final class TomcatJDBCTool {

    private TomcatJDBCTool(){};


    private static String arraysToString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "【】";

        StringBuilder b = new StringBuilder();
        b.append('【');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append('】').toString();
            b.append("¦");
        }
    }
    /**
     * 打印 行/列
     */
    public static void printLines(List<Object[]> lines) {
        StringBuilder sb = new StringBuilder("查询总条数:"+lines.size()+":\n");

        for (int i = 0; i < lines.size(); i++) {
            sb.append(i).append("\t").append(arraysToString(lines.get(i)));
            if (i != lines.size()-1) {
                sb.append("\n");
            }
        }

        JDBCLogger.print(sb.toString());
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

            // 目标类型与指定类型一样
            if (val.getClass() == cls) return val;

            if (cls == String.class) return String.valueOf(val);

            if (cls == BigDecimal.class) return new BigDecimal(String.valueOf(val));

            if (cls == BigInteger.class) return new BigInteger(String.valueOf(val));

            String className = cls.getSimpleName();
            if (className.equals("Integer")) className = "int";
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            String methodName = "parse" + className;

            String v = String.valueOf(val);
            Method method = baseTypeConvertWrapType(cls).getMethod(methodName, String.class);
            return method.invoke(null, v);
        } catch (Exception e) {
            JDBCLogger.error("类型转换错误,val=" + val + ",cls=" + cls, e);
        }
        return null;
    }

    /**
     * 转换Object为执行类型值
     */
    @SuppressWarnings("unchecked")
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

    /**
     * 转换Object为执行类型值
     */
    public static <T> T obj2Type(Object object, T def) {
        return convertObjectToSpecType(object,def);
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

//    /* 打印sql 原句*/
//    public static String convetOriginalSQL(String sql,Object... params){
//        String _sql = sql.replaceAll("\\{\\{\\?","").replaceAll("\\}\\}","");
//
//    }

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


    private static void setFieldValue(Object instance, Field field, Object value) throws IllegalAccessException {

        field.setAccessible(true);
        String fieldTypeName = field.getGenericType().getTypeName();
        Object resultValue;
        switch(fieldTypeName){
            case "int":
            case "java.lang.Integer":
                if(value!=null){
                    resultValue = Integer.parseInt(value.toString());
                }else{
                    resultValue = 0;
                }

                break;
            case "long":
            case "java.lang.Long":
                if(value!=null){
                    resultValue = Long.parseLong(value.toString());
                }else{
                    resultValue = 0L;
                }
                break;
            case "double":
            case "java.lang.Double":
                if(value!=null){
                    resultValue = Double.parseDouble(value.toString());
                }else{
                    resultValue = 0.0D;
                }
                break;
            case "boolean":
            case "java.lang.Boolean":
                if(value!=null){
                    resultValue = Boolean.parseBoolean(value.toString());
                }else{
                    resultValue = false;
                }
                break;
            case "byte":
            case "java.lang.Byte":
                if(value!=null){
                    resultValue = Byte.parseByte(value.toString());
                }else{
                    resultValue = null;
                }
                break;
            case "short":
            case "java.lang.Short":
                if(value!=null){
                    resultValue = Short.parseShort(value.toString());
                }else{
                    resultValue = 0;
                }
                break;
            case "float":
            case "java.lang.Float":
                if(value!=null){
                    resultValue = Float.parseFloat(value.toString());
                }else{
                    resultValue = 0.0f;
                }
                break;
            case "java.lang.String":
                if (value!=null){
                    resultValue = String.valueOf(value);
                }else{
                    resultValue = "";
                }
                break;

            case "BigInteger":
            case "java.math.BigInteger":
                if (value!=null){
                    resultValue = new BigInteger( String.valueOf(value));
                }else{
                    resultValue = new BigInteger("0");
                }
                break;
            case "BigDecimal":
            case "java.math.BigDecimal":
                if (value!=null){
                    resultValue = new BigDecimal( String.valueOf(value));
                }else{
                    resultValue = new BigDecimal("0.0000");
                }
                break;
            default:
                resultValue = value;
        }

        if (resultValue != null){
            field.set(instance, resultValue);
        }

    }


    /* 指定行转对象 */
    @SuppressWarnings("unchecked")
    public static <T> T  objectArrayToSpecClass(Object[] rows, Class<T> classType, Tuple2<String,Integer>... field_index_tuple_arrays) {
        try{
            if (rows == null || classType==null || field_index_tuple_arrays==null)
                throw new IllegalArgumentException("参数异常, rows="+ Arrays.toString(rows) +" ,classType="+classType+" ,field_index_tuple_arrays="+ Arrays.toString(field_index_tuple_arrays));
            T instance = JDBCUtils.createObject(classType);
            for (Tuple2<String,Integer> tuple2 : field_index_tuple_arrays) {
                String fieldName = tuple2.getValue0();
                int index = tuple2.getValue1();
                try {
                    Field field = classType.getField(fieldName);
                    if (index >= rows.length) throw new IllegalArgumentException("类属性关联的目标数据下标越界,fieldName="+fieldName+" ,index="+index);
                    Object value = rows[index];
                    // 获取属性的类型
                    setFieldValue(instance,field,value);
                } catch (Exception e) {
                    JDBCLogger.error("数据列转换对象属性错误('"+fieldName+"','"+index+"')",e);
                }
            }
            return instance;
        }catch (Exception e){
            JDBCLogger.error("数据行转换对象错误",e);
        }
        return null;

    }

    /* 指定行转对象 */
    @SuppressWarnings("unchecked")
    public static <T> T  objectArrayToSpecClass(Object[] rows, Class<T> classType,String... field_index_str_arrays) {
        if (field_index_str_arrays == null) return null;
        int len = field_index_str_arrays.length;

        Tuple2<String,Integer>[] tubArr = new Tuple2[len];
        try {
            for(int i = 0; i<len; i++){
                String[] arr = field_index_str_arrays[i].split(",");
                if (arr.length == 1){
                    tubArr[i] = new Tuple2<>(arr[0],i);
                }else if (arr.length == 2){
                    tubArr[i] = new Tuple2<>(arr[0],Integer.parseInt(arr[1]));
                }else throw new IllegalArgumentException("无法解析属性域: " +  field_index_str_arrays[i]);
            }
        } catch (Exception e) {
            JDBCLogger.error("数据行转对象_域名-下标字符串处理错误",e);
            return null;
        }
        return objectArrayToSpecClass(rows,classType,tubArr);
    }

    /* 指定行转对象列表 */
    @SuppressWarnings("unchecked")
    public static <T> List<T>  objectArrayListToSpecClassList(List<Object[]> lines, Class<T> classType,Tuple2<String,Integer>... field_index_tuple_arrays) {
        List<T> list = new ArrayList<>();
        for (Object[] rows : lines){
            T instance = objectArrayToSpecClass(rows,classType,field_index_tuple_arrays);
            if (instance!=null) list.add(instance);
        }
        return list;
    }

    /* 指定行转对象列表 */
    public static <T> List<T>  objectArrayListToSpecClassList(List<Object[]> lines, Class<T> classType,String... field_index_str_arrays) {
        List<T> list = new ArrayList<>();
        for (Object[] rows : lines){
            T instance = objectArrayToSpecClass(rows,classType,field_index_str_arrays);
            if (instance!=null) list.add(instance);
        }
        return list;
    }

    // lines -> map
    public static Map<String,Object> rowsToMap(Object[] rows,String fieldNamesStr){
        Map<String,Object> map = new HashMap<>();
        if (fieldNamesStr != null || fieldNamesStr.length()>0){
            String[] fieldNames = fieldNamesStr.split(",");
            for(int i=0; i<fieldNames.length; i++){
                if (i<rows.length){
                    map.put(fieldNames[i].trim(),rows[i]);
                }
            }
        }
        return map;
    }

    public static List<Map<String,Object>> linesToListMap(List<Object[]>  lines,String fieldNamesStr){
        List<Map<String,Object>> result = new ArrayList<>();
        if (fieldNamesStr!=null){
            for (Object[] rows : lines ){
                result.add(rowsToMap(rows,fieldNamesStr));
            }
        }
        return result;
    }


    public static long currentTimestampID() {
        // 1毫秒999ID
        return GenUniqueID.milliSecondID.currentTimestampLong();
    }

    public static long tableOID(){
        long s = currentTimestampID();
        System.out.println(s);
        System.out.println(String.valueOf(s).length());
        s = currentTimestampID();
        System.out.println(s);
        System.out.println(String.valueOf(s).length());
        s = currentTimestampID();
        System.out.println(s);
        System.out.println(String.valueOf(s).length());
        return Long.parseLong(
                s
//                        + String.format("%01d", 1)
                        + String.format("%03d", 161));
    }

    public static long currentTimestampSecSeq() {
        return Long.parseLong(GenUniqueID.secondID.currentTimestampString(false,true));
    }


    /* 多线程合并查询 */
    public interface MultiThreadCallback<ARGS,RESP>{
        List<? extends RESP> callback(Page page,ARGS vars);
    }

    /*** 多线程查询合并结果集 */
    public static <ARGS,RESP>  List<RESP> multiThreadCallback(MultiThreadCallback<ARGS,RESP> callback, int tpoolSize, int segment, final ARGS vars){
        // 1查询总条数
        // 2条数大于20w数据,分页多线程查询
        // 3当前线程等待完成
        // 4合并结果
        IThreadPool tpool = new IOSingerThreadPool(tpoolSize);
        List<RESP> allList = new ArrayList<>();
        try{
            Page page = new Page(-1,-1);
            callback.callback(page,vars);
            if (page.getTotalItems() != 0) {
                // 计算线程数量
                int threadSize = page.getTotalItems()/segment;
                int surplus =  page.getTotalItems()&segment;
                if (surplus>0) threadSize+=1;

                // 其他线程
                List<Future<List<? extends RESP>>> futureList = new ArrayList<>();
                for (int i=0;i<threadSize;i++){
                    Page temp_page = new Page(i+1,segment).setNotQuerySelectTotal();
                    Callable<List<? extends RESP>> callable = () -> callback.callback(temp_page,vars);
                    Future<List<? extends RESP>> future = tpool.submit(callable);
                    futureList.add(future);
                }
                // 等待完成
                for (Future<List<? extends RESP>> future : futureList) {
                    try{
                        List<? extends RESP> tempRes = future.get();
                        if(tempRes!=null) {
                            allList.addAll(tempRes);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                tpool.close();
            }catch (Exception ignored){

            }
        }
        return allList;
    }


    public static int intArrayToValue(int[] res){
        int resIndex = 0;
        for (int i : res){
            if (i>0) resIndex++;
        }
        return resIndex;
    }

    public static void main(String[] args) {
        System.out.println(currentTimestampSecSeq());
        System.out.println(currentTimestampSecSeq());
        System.out.println(currentTimestampSecSeq());
        System.out.println(tableOID());
    }
}
