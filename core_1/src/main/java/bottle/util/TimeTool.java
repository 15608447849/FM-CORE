package bottle.util;

import bottle.tuples.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2017/11/27.
 */
public class TimeTool {
    /**
     * 一分钟间隔时间
     */
    public static final long PERIOD_MINUTE =  60 * 1000L;
    /**
     * 一小时间隔时间
     */
    public static final long PERIOD_HOUR = 60 * PERIOD_MINUTE;

    /**
     * 一天得间隔时间
     */
    public static final long PERIOD_DAY = 24 * PERIOD_HOUR ;

    /**
     * 一周间隔时间
     */
    public static final long PERIOD_WEEK = 7 * PERIOD_DAY ;



    private static final String TIME_MATCH_REGULAR_YYYYMMDD_HHMMSS = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})\\s+(\\d{1,2}):(\\d{1,2}):(\\d{1,2})";
    private static final String TIME_MATCH_REGULAR_YYYYMMDD = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})";

    private final static String[] stdTimePatterns = new String[]{
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd",
            "HH:mm:ss.SSS",
            "HH:mm:ss",
            "yyyyMMddHHmmssSSS",
            "yyyyMMddHHmmss",
            "yyyyMMdd",
            "HHmmssSSS",
            "HHmmss"
    };


    /* 检查日期格式 */
    public static List<Tuple2<String,Date>> checkStrIsStdDate(String str) {
        List<Tuple2<String,Date>> list = new ArrayList<>();

        if (str != null && str.length()>0) {
            for (String stdTimePattern : stdTimePatterns) {
                try {
//                    SimpleDateFormat dateFormat = new SimpleDateFormat(stdTimePattern);
//                    dateFormat.setLenient(false);// 使用严格模式精准解析
                    LocalDate.parse(str, DateTimeFormatter.ofPattern(stdTimePattern));

                    list.add(new Tuple2<>(stdTimePattern, new SimpleDateFormat(stdTimePattern).parse(str) ));
                } catch (Exception ignored) {
                }
            }
        }
        return  list;

    }



    /**添加x天*/
    public static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

    /**减少x天*/
    public static Date subtractDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, -num);
        return startDT.getTime();
    }

    /**计算指定天数的时间字符串
     * @param dayNumber -1=昨天,+1明天...
     * @param formatStr 如yyyy-MM-dd
     * */
    public static String calSpecNumberDateToSpecFormat(int dayNumber,String formatStr){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime( new Date());
        calendar.add(Calendar.DATE, dayNumber);

        SimpleDateFormat format= new SimpleDateFormat(formatStr);
        return format.format(calendar.getTime());
    }

    /**string -> date ,  参数:"11:00:00"  如果小于当前时间,向后加一天*/
    public static Date str_Hms_2Date(String timeString) {
        try {
            String[] strArr = timeString.split(":");

            Calendar calendar = Calendar.getInstance();
            if (strArr.length >= 1){
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strArr[0]));
            }else{
                calendar.set(Calendar.HOUR_OF_DAY, 0);
            }
            if (strArr.length >= 2){
                calendar.set(Calendar.MINUTE, Integer.parseInt(strArr[1]));
            }else{
                calendar.set(Calendar.MINUTE,0);
            }
            if (strArr.length >= 3){
                calendar.set(Calendar.SECOND, Integer.parseInt(strArr[2]));
            }else{
                calendar.set(Calendar.SECOND, 0);
            }
            Date date = calendar.getTime();
            if (date.before(new Date())) {
                date = addDay(date, 1);
            }
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }


    public static String date2Str(Date date,String dateFormat){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返回: "2017-11-11 9:50:00" 字符串
     */
    public static String date_yMd_Hms_2Str(Date date){
        return date2Str(date,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回: "20171111095000" 字符串
     */
    public static String date_yMdHms_2Str(Date date){
        return date2Str(date,"yyyyMMddHHmmss");
    }

    /**
     * 2017-11-11 9:50:00 -> DATE
     */
    public static Date formatStr_yMdHms_2Date(String timeString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 毫秒数-> x天x小时x分x秒
     * @author lzp
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;

        StringBuilder sb = new StringBuilder();
        if (days > 0){
            sb.append(days).append(" 天 ");
        }
        if (hours > 0){
            sb.append(hours).append(" 小时 ");
        }
        if (minutes > 0){
            sb.append(minutes).append(" 分钟 ");
        }
        if (seconds > 0){
            sb.append(seconds).append(" 秒 ");
        }
        if (sb.toString().length() == 0){
            sb.append(mss).append(" 毫秒 ");
        }
        return sb.toString();
    }

    /** 连接时间格式化显示 */
    public static String getConnectedDurationHumStr(long timestampDiff) {
        long second = timestampDiff / 1000L;
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days) {
            return days + "天" + hours + "小时" + minutes + "分钟" + second+"秒";
        } else {
            return hours + "小时" + minutes + "分钟" + second+"秒";
        }
    }

    /** 获取指定年份 */
    public static int getSpecYear(Date date){
        try {
            return Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 1900;
    }

    /**
     * 获取当前年份
     */
    public static int getCurrentYear(){
        return getSpecYear(new Date());
    }



    /**
     * 获取当前时间
     */
    public static String getCurrentTime(){
        return date2Str(new Date(),"HH:mm:ss");
    }

    /**
     * 判断指定时间是否在[startDate, endDate]区间，注意时间格式要一致
     *
     * @param nowDate 当前时间
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return true在指定区间
     */
    public static boolean dateWithinRange(Date nowDate, Date startDate, Date endDate) {
        if (nowDate.getTime() == startDate.getTime() || nowDate.getTime() == endDate.getTime()) return true;
        Calendar date = Calendar.getInstance();
        date.setTime(nowDate);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        return date.after(begin) && date.before(end);
    }

    /**
     * 格式为"HH:mm:ss"的时间是否在指定[startDate, endDate]区间
     */
    public static boolean isEffectiveTime(String startTime, String endTime){
        try{
            String format = "HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String nowTime = sdf.format(new Date());
            Date nowDate = sdf.parse(nowTime);
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            return dateWithinRange(nowDate, startDate, endDate);

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }








    /** 正则匹配时间内容 */
    public static String[] regularMatchTimeStr(String text){
        List<String> list = new ArrayList<>();

        Pattern r1 = Pattern.compile(TIME_MATCH_REGULAR_YYYYMMDD_HHMMSS);
        Matcher m1 = r1.matcher(text);
        while (m1.find()){
            String tstr = m1.group();
            list.add(tstr);
            text = text.replaceFirst(tstr,"");
        }

        Pattern r2 = Pattern.compile(TIME_MATCH_REGULAR_YYYYMMDD);
        Matcher m2 = r2.matcher(text);
        while (m2.find()){
            String tstr = m2.group();
            list.add(tstr);
            text = text.replaceFirst(tstr,"");
        }

        String[] arr = new String[list.size()];
        return list.toArray(arr);
    }
}
