package bottle.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Leeping on 2018/6/27.
 * email: 793065165@qq.com
 */

public class GoogleGsonUtil {

    public interface GsonPauseErrorPrintI{
        void jsonPauseError(String jsonText,Class classType,Type type, Throwable e,String errorInfo);
    }

    private static GsonPauseErrorPrintI printI = (jsonText, classType, type, e, errorInfo) -> Log4j.info(errorInfo);

    public static void setErrorPrintI(GsonPauseErrorPrintI printI) {
        GoogleGsonUtil.printI = printI;
    }

    private static void catchJsonPauseError(String jsonText, Throwable ex, Class classType, Type type){
        StringBuilder s = new StringBuilder("GSON解析异常").append( "\tJSON= "+ jsonText);
        if (classType!=null) s.append("\n类类型\t"+ classType);
        if (type!=null) s.append("\n泛型泛型\t "+ type);
        Throwable temp = ex;
        while (temp!=null){
            s.append("\n");
            if (temp instanceof JsonSyntaxException){
                assert ex instanceof JsonSyntaxException;
                JsonSyntaxException jse = (JsonSyntaxException) ex;
                s.append("类型转换错误").append("\t" + temp.getMessage());
                if (jse.getFiledPath()!=null){
                    s.append("\t").append(jse.getFiledPath());
                }
                break;
            }

            s.append(temp.getClass().getSimpleName()).append("\t" + temp.getMessage());
            temp = temp.getCause();
        }
//        s.append("\n").append(StringUtil.printExceptInfo(ex));
        s.append("\n");

        if (printI!=null){
            printI.jsonPauseError(jsonText,classType,type,ex,s.toString());
        }
    }

    private static class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        @Override
        public java.sql.Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!(jsonElement instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            try {
                Date date = format.parse(jsonElement.getAsString());
                return new java.sql.Timestamp(date.getTime());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(java.sql.Timestamp timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(format.format(new Date(timestamp.getTime())));
        }
    }

    private static class TimeTypeAdapter implements JsonSerializer<java.sql.Time>, JsonDeserializer<java.sql.Time> {
        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");


        @Override
        public java.sql.Time deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!(jsonElement instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            try {
                Date date = format.parse(jsonElement.getAsString());
                return new java.sql.Time(date.getTime());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(java.sql.Time timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(format.format(new Date(timestamp.getTime())));
        }
    }

    private static class DateTypeAdapter implements JsonSerializer<java.sql.Date>, JsonDeserializer<java.sql.Date> {
        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public java.sql.Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!(jsonElement instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            try {
                Date date = format.parse(jsonElement.getAsString());
                return new java.sql.Date(date.getTime());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(java.sql.Date timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(format.format(new Date(timestamp.getTime())));
        }
    }

    private static class BigIntegerTypeAdapter implements JsonSerializer<BigInteger>, JsonDeserializer<BigInteger> {

        @Override
        public BigInteger deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!(jsonElement instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            return new BigInteger(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(BigInteger var, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(var.toString());
        }
    }

    private static class BigDecimalTypeAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {

        @Override
        public BigDecimal deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!(jsonElement instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            return new BigDecimal(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(BigDecimal var, Type type, JsonSerializationContext jsonSerializationContext) {
            String temp = var.toString();
            int index = temp.indexOf(".");
            if (index>0){

                String sub = temp.substring(index+1);
                if (sub.length()>=16){
                    // 16位小数 转字符串
                    return new JsonPrimitive(var.toString());
                }
            }
            return new JsonPrimitive(Double.parseDouble(temp));
        }
    }

    private final static Gson builder =  new GsonBuilder()
            .disableHtmlEscaping()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
//            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src.doubleValue()) ))
//            .registerTypeAdapter(Float.class, (JsonSerializer<Float>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src.floatValue()) ))
//            .registerTypeAdapter(BigDecimal.class,(JsonSerializer<BigDecimal>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src)))
//            .registerTypeAdapter(Integer.class,(JsonSerializer<Integer>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src.intValue())))
            .registerTypeAdapter(BigInteger.class,(JsonSerializer<BigInteger>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
//            .registerTypeAdapter(BigDecimal.class,(JsonSerializer<BigDecimal>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(java.sql.Timestamp.class, new TimestampTypeAdapter())
            .registerTypeAdapter(java.sql.Date.class, new DateTypeAdapter())
            .registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter())
//            .registerTypeAdapter(BigInteger.class,new BigIntegerTypeAdapter())
            .registerTypeAdapter(BigDecimal.class,new BigDecimalTypeAdapter())
            .create();


    /** 判断是否为JSON格式字符串 */
    public static boolean isJsonFormatter(String json) {
        try {
            if (json==null || json.length()==0) return false;
//            new JsonParser().parse(json);
            JsonElement jsonElement = JsonParser.parseString(json);
//            System.out.println(jsonElement);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
//        String json = "[0,0,'','','','']";
        String json = "\"[\"111\",\"222\",2]\"";
//        String json = "{\"time\":\"2022-07-14 20:16:52\",\"type\":\"callJavaMethod\",\"methodName\":\"serverRequestDownloadFile\",\"paramJson\":\"{\\\"mac\\\":\\\"B8-86-87-DA-6F-FE,B8-86-87-DA-6F-FE,80-FA-5B-20-EA-D7,00-01-00-01-27-3F,DA-3E-80-FA-5B-20,80-FA-5B-20-EA-D7\\\",\\\"url\\\":\\\"http://121.37.6.129:9999/temp/excel/44A8C7249633680E1FCECA246C207F96.xlsx\\\",\\\"tips\\\":\\\"�����ɹ� http://121.37.6.129:9999/temp/excel/44A8C7249633680E1FCECA246C207F96.xlsx\\\"}\"}";
        System.out.println(isJsonFormatter(json));
    }



    /**
     * javabean to json
     * @param object
     * @return
     */
    public static String javaBeanToJson(Object object){
        try {
            if (object!=null){
                return builder.toJson(object);
            }
        } catch (Exception ignored) { }
        return null;
    }

    /**
     * json to javabean
     *
     * @param json
     */
    public static <T> T jsonToJavaBean(String json,Class<T> cls) {
        try {
            if (json==null || json.length()==0) return null;
            return builder.fromJson(json, cls);//对于javabean直接给出class实例
        } catch (Exception e) {
            catchJsonPauseError(json,e,cls,null);
        }
        return null;
    }


    /**
     * json to javabean
     *new TypeToken<List<xxx>>(){}.getType()
     * @param json
     */
    public static <T> T jsonToJavaBean(String json,Type type) {
        try {
            if (json==null || json.length()==0) return null;
            return builder.fromJson(json, type);//对于javabean直接给出class实例
        } catch (Exception e) {
            catchJsonPauseError(json,e,null,type);
        }
        return null;
    }

    public static <T,D> HashMap<T,D> string2Map(String json){
        try {
            if (json==null || json.length()==0) return null;

            Type type = new TypeToken<HashMap<T,D>>() {}.getType();
//            Gson _builder =  new GsonBuilder()
//                    .registerTypeAdapter(type,
//                            new TypeAdapter<Object>(){
//                        @Override
//                        public void write(JsonWriter out, Object value) throws IOException {
//
//                        }
//
//                        @Override
//                        public Object read(JsonReader in) throws IOException {
//
//                            JsonToken token = in.peek();
//
//                            switch (token) {
//                                case STRING:
//                                    return in.nextString();
//                                case NUMBER:
//                                    String s = in.nextString();
//                                    if (s.contains(".")) {
//                                        return Double.valueOf(s);
//                                    } else {
//                                        try {
//                                            return Integer.valueOf(s);
//                                        } catch (Exception e) {
//                                            return Long.valueOf(s);
//                                        }
//                                    }
//                                case BOOLEAN:
//                                    return in.nextBoolean();
//                                case NULL:
//                                    in.nextNull();
//                                    return null;
//
//                                case BEGIN_ARRAY:
//                                    List<Object> list = new ArrayList<>();
//                                    in.beginArray();
//                                    while (in.hasNext()) {
//                                        list.add(read(in));
//                                    }
//                                    in.endArray();
//                                    return list;
//
//                                case BEGIN_OBJECT:
//                                    HashMap<T,D> map = new HashMap<>();
//                                    in.beginObject();
//                                    while (in.hasNext()) {
//                                        map.put((T)in.nextName(), (D)read(in));
//                                    }
//                                    in.endObject();
//                                    return map;
//                                default:
//                                    throw new IllegalStateException();
//                            }
//                        }
//                    } ).create();
              return builder.fromJson(json, type);//对于javabean直接给出class实例
        } catch (Exception e) {
            catchJsonPauseError(json,e,null,null);
        }
        return null;
    }

    public static <T> List<T> json2List(String json,Class<T> clazz){
        List<T> list = new ArrayList<>();
        try {
            if (json!=null && json.length()>0) {

                JsonElement jsonElement =JsonParser.parseString(json);
                JsonArray array = jsonElement.getAsJsonArray();
                for (JsonElement element : array) {
                    list.add(builder.fromJson(element, clazz));
                }
            }
        } catch (Exception e){
            catchJsonPauseError(json,e,null,null);
        }
        return list;
    }

    /**
     * 判断是否是数组类型的json字符串
     */
    public static boolean checkJsonIsArray(String json){
        try {
            if (json==null || json.length()==0) return false;
            Object jsonObj = new JSONTokener(json).nextValue();
            if (jsonObj instanceof JSONArray) {
                return true;
            }
        } catch (Exception e) {
            catchJsonPauseError(json,e,null,null);
        }
        return false;
    }

    public static int convertInt(Object val){
        return new BigDecimal(String.valueOf(val)).intValue();
    }

    /** 格式化字符串 */
    public static String toPrettyFormat(Object object) {
        try {
            if (object!=null){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                return gson.toJson(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
