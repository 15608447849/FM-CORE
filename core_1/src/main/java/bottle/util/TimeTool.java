package bottle.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2017/11/27.
 */
public class TimeTool {

    /**
     * 一周间隔时间
     */
    public static final long PERIOD_WEEK = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 一天得间隔时间
     */
    public static final long PERIOD_DAY = 24 * 60 * 60 * 1000L;

    /**
     * 一小时间隔时间
     */
    public static final long PERIOD_HOUR = 60 * 60 * 1000L;

    /**
     * 一个月
     */
    public static final long PERIOD_MONTH = 30 * 24 * 60 * 60 * 1000L;

    /**添加x天*/
    private static Date addDay(Date date, int num) {
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

    /**
     * 例: DATE , 2017-11-11 9:50:00
     */
    public static Date str_yMd_Hms_2Date(String timeString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String date2Str(Date date,String dateFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 例: "2017-11-11 9:50:00"
     */
    public static String date_yMd_Hms_2Str(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String date_yMdHms_2Str(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
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




    // 连接时间格式化显示
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
    /**
     * 获取当前年份
     */
    public static int getCurrentYear(){
        try {
            return Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 1900;
    }

    /** 获取指定年份 */
    public static int getCurrentYear(Date date){
        try {
            return Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 1900;
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime(){
        try {
            return new SimpleDateFormat("HH:mm:ss").format(new Date());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "00:00:00";
    }

    /**
     * 判断当前时间是否在[startDate, endDate]区间，注意时间格式要一致
     *
     * @param nowDate 当前时间
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowDate, Date startDate, Date endDate) {
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
     * 格式为"HH:mm:ss"
     * 判断当前时间是否在[startDate, endDate]区间，注意时间格式要一致
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isEffectiveTime(String startTime, String endTime){
        try{
            String format = "HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String nowTime = sdf.format(new Date());
            Date nowDate = sdf.parse(nowTime);
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            return isEffectiveDate(nowDate, startDate, endDate);

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private final static String[] parsePatterns = new String[]{
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",

            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd",

            "HH:mm:ss"
    };

    private final static SimpleDateFormat[] parsePatterns_dateFormat = new SimpleDateFormat[parsePatterns.length];

    static {
        for (int i = 0; i< parsePatterns.length;i++){
            parsePatterns_dateFormat[i] =  new SimpleDateFormat(parsePatterns[i]);
        }
    }

    /* 检查日期格式 */
    public static Object[] parseDate(String str) {
        if (str == null) {
            return null;
        }

        for (int i = 0;i<parsePatterns_dateFormat.length;i++){
            try {
                SimpleDateFormat dateFormat =  parsePatterns_dateFormat[i];
                dateFormat.setLenient(false);
                return new Object[]{ parsePatterns[i] , dateFormat.parse(str)};
            } catch (ParseException ignored) { }
        }
        return  null;

    }

    public static Date parseDateSpecFormat(String str,String format) {
        if (str == null) {
            return null;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setLenient(false);
            return dateFormat.parse(str);
        } catch (ParseException ignored) { }

        return  null;
    }

    private static final String TIME_MATCH_REGULAR_YYYYMMDD_HHMMSS = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})\\s+(\\d{1,2}):(\\d{1,2}):(\\d{1,2})";
    private static final String TIME_MATCH_REGULAR_YYYYMMDD = "(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})";

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
