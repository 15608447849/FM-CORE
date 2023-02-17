package framework.server;

import bottle.util.GoogleGsonUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
/*
*
* 基本类型: int, double, float, long, short, boolean, byte, char， void
* */
public class ApiRequestHelper {

    // 判断类型返回默认值
    private static Object getTypeDefaultValue(Class<?> classType){

        if (Boolean.class.isAssignableFrom(classType)) return false;
        else if (Integer.class.isAssignableFrom(classType)) return 0;
        else if (Long.class.isAssignableFrom(classType)) return 0L;
        else if (Float.class.isAssignableFrom(classType)) return 0.0F;
        else if (Double.class.isAssignableFrom(classType)) return 0.0D;
        // 引用类型等
        return null;
    }

    private static Object getTypeValue(Object value, Class<?> classType,Type genericType) {

        if (String.class.isAssignableFrom(classType)) {
            if (value instanceof String)  return  value;
            else  return GoogleGsonUtil.javaBeanToJson(value);
        }
        else if (Boolean.class.isAssignableFrom(classType)) return Boolean.valueOf(String.valueOf(value));
        else if (Integer.class.isAssignableFrom(classType)) return Integer.valueOf(String.valueOf(value));
        else if (Long.class.isAssignableFrom(classType)) return Long.valueOf(String.valueOf(value));
        else if (Float.class.isAssignableFrom(classType)) return Float.valueOf(String.valueOf(value));
        else if (Double.class.isAssignableFrom(classType)) return Double.valueOf(String.valueOf(value));
        else if (BigDecimal.class.isAssignableFrom(classType)) return new BigDecimal(String.valueOf(value));
        else if (BigInteger.class.isAssignableFrom(classType)) return new BigInteger(String.valueOf(value));

        else return GoogleGsonUtil.jsonToJavaBean(GoogleGsonUtil.javaBeanToJson(value),genericType);
    }

    /* 处理级联map key对象 */
    private static Object loopConvertMapParams(List<String> keys, Map<String, Object> map) {

        String key = null;
        Object value = null;

        for (String k : keys) {

            if (value!=null){
                String json = GoogleGsonUtil.javaBeanToJson(value);
                map = GoogleGsonUtil.string2Map(json);
                if (map == null) throw new IllegalArgumentException("请求参数 校验失败 ,不是MAP JSON格式文本,KEY = "+ key +" JSON = "+ json );
            }

            key = k;
            value = map.get(key);

            if (value == null) throw new IllegalArgumentException("请求参数 校验失败,JSON缺少KEY: "+ key);
        }

        return value;
    }

    static Object convertFuncRequest(IceSessionContext context,Parameter parameter, Class<?> classType, Type genericType, String[] arrayParam, String jsonParam) {

        // 获取形参注解
        Annotation[] annotations = parameter.getAnnotations();
        if (annotations.length != 1){
            return getTypeDefaultValue(classType);
        }
        Annotation annotation = annotations[0];

        if (IReqArray.class.isAssignableFrom(annotation.annotationType())){

            // 数组
            if (String[].class.isAssignableFrom(classType)){
                int value = ((IReqArray) annotation).value();
                if (value>0 && (arrayParam==null || arrayParam.length != value) ) throw new IllegalArgumentException("请求参数 数组长度("+(arrayParam==null?-1:arrayParam.length)+")不正确,固定长度: "+ value );
                // 数组参数
                return arrayParam;
            }

        }

        if (IReqJson.class.isAssignableFrom(annotation.annotationType())){
            // JSON
            if (jsonParam == null) throw new IllegalArgumentException("请求参数JSON文本为空");
            boolean value =((IReqJson) annotation).value();
            if (!value && String.class.isAssignableFrom(classType)){
                return jsonParam;
            }
            if (value){
                // 自动转换实体对象
               return GoogleGsonUtil.jsonToJavaBean(jsonParam,genericType);
            }
        }

        if (IReqKey.class.isAssignableFrom(annotation.annotationType())){
            // JSON
            if (jsonParam == null) throw new IllegalArgumentException("请求参数JSON文本为空");
            String key = ((IReqKey) annotation).value();

            if (context.stringObjectMap == null){
                context.stringObjectMap = GoogleGsonUtil.string2Map(jsonParam);
                if ( context.stringObjectMap == null ) throw new IllegalArgumentException("请求参数 校验失败,不是JSON MAP格式文本");
            }

            String[] keyArr = key.split("\\.");
            Object value = loopConvertMapParams(Arrays.asList(keyArr),context.stringObjectMap);
            return getTypeValue(value,classType,genericType);
        }

        return getTypeDefaultValue(classType);
    }


}
