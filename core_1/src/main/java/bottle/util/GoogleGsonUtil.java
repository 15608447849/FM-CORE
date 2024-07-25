package bottle.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.IOException;
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

    private static final ThreadLocal<String> errors = new ThreadLocal<>();

    private static GsonPauseErrorPrintI printI = (jsonText, classType, type, e, errorInfo) -> Log4j.info(errorInfo);

    public static void setErrorPrintI(GsonPauseErrorPrintI printI) {
        GoogleGsonUtil.printI = printI;
    }

    private static void catchJsonPauseError(String jsonText, Throwable ex, Class<?> classType, Type type){
        StringBuilder s = new StringBuilder("GSON解析异常").append( "\tJSON= "+ jsonText);

        if (classType!=null) s.append("\n类型\t"+ classType.getName());
        if (type!=null) s.append("\n泛型\t "+ type);

        Throwable temp = ex;
        while (temp!=null){
            s.append("\n");

            if (temp instanceof JsonSyntaxException){
                assert ex instanceof JsonSyntaxException;
                JsonSyntaxException jse = (JsonSyntaxException) ex;
                s.append("错误\t").append(temp.getCause());
                if (jse.getFiledPath()!=null) {
                    s.append("\t类字段\t").append(jse.getFiledPath());
                }
                break;
            }

            s.append("错误\t").append(temp.getClass().getSimpleName()).append("\t");

            temp = temp.getCause();
        }
        s.append("\n");

        errors.set(s.toString());

        if (printI!=null){
            printI.jsonPauseError(jsonText,classType,type,ex,s.toString());
        }
    }

    public static String getCurrentThreadJsonThrowableString(){
        return errors.get();
    }

    private static final TypeAdapter<java.sql.Timestamp> TIMESTAMP_ADAPTER = new TypeAdapter<java.sql.Timestamp>() {
        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @Override
        public java.sql.Timestamp read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new java.sql.Timestamp( format.parse(in.nextString()).getTime() );
            } catch (ParseException e) {
                throw new JsonSyntaxException(in,e);
            }
        }
        @Override
        public void write(JsonWriter out, java.sql.Timestamp value) throws IOException {
            out.value(format.format(new Date(value.getTime())));
        }
    };

    private static final TypeAdapter<java.sql.Date> DATE_ADAPTER = new TypeAdapter<java.sql.Date>() {
        private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        @Override
        public java.sql.Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new java.sql.Date( format.parse(in.nextString()).getTime() );
            } catch (ParseException e) {
                throw new JsonSyntaxException(in,e);
            }
        }
        @Override
        public void write(JsonWriter out, java.sql.Date value) throws IOException {
            out.value(format.format(new Date(value.getTime())));
        }
    };

    private static final TypeAdapter<java.sql.Time> TIME_ADAPTER = new TypeAdapter<java.sql.Time>() {
        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");
        @Override
        public java.sql.Time read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new java.sql.Time( format.parse(in.nextString()).getTime() );
            } catch (ParseException e) {
                throw new JsonSyntaxException(in,e);
            }
        }
        @Override
        public void write(JsonWriter out, java.sql.Time value) throws IOException {
            out.value(format.format(new Date(value.getTime())));
        }
    };


    private final static Gson builder =  new GsonBuilder()
            .disableHtmlEscaping()

            .registerTypeAdapter(long.class, (JsonSerializer<Long>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src)) )
            .registerTypeAdapter(Long.class, (JsonSerializer<Long>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src)) )
            .registerTypeAdapter(BigInteger.class, (JsonSerializer<BigInteger>) (src, typeOfSrc, context) -> new JsonPrimitive(String.valueOf(src)) )
            .registerTypeAdapter(BigDecimal.class, (JsonSerializer<BigDecimal>) (src, typeOfSrc, context) -> {
                String var = src.toPlainString();
                int index = var.indexOf(".");
                if (index>0){
                    //四位小数返回doublue
                    String lstr = var.substring(index+1);
                    if (lstr.length()<=10){
                        return new JsonPrimitive(Double.valueOf(var));
                    }
                }
                return new JsonPrimitive(var);
            })

            .registerTypeAdapter(java.sql.Timestamp.class, TIMESTAMP_ADAPTER)
            .registerTypeAdapter(java.sql.Time.class, DATE_ADAPTER)
            .registerTypeAdapter(java.sql.Date.class, TIME_ADAPTER)

            .create();

    private static class B{
//                        Long a;
        BigDecimal b;
//        BigInteger C;
//        java.sql.Date d;
    }

    public static void main(String[] args) {

        String json = "{\"a\":\"\",\"b\":\"79949.0000000007\",\"C\":\"155555555555555555555555557\",\"d\":\"4\"}";
        B b = GoogleGsonUtil.jsonToJavaBean(json, B.class);

        System.out.println(
                GoogleGsonUtil.javaBeanToJson(b)
        );

    }



    /** 判断是否为JSON格式字符串 */
    public static boolean isJsonFormatter(String json) {
        try {
            if (json==null || json.length()==0) return false;
            JsonParser.parseString(json);
        } catch (Exception e) {

            return false;
        }
        return true;
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
