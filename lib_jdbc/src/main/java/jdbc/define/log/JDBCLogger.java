package jdbc.define.log;

import bottle.util.Log4j;
import bottle.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: leeping
 * @Date: 2019/8/16 11:04
 */
public final class JDBCLogger {

    private JDBCLogger(){}

    public static void print(String message){
        Log4j.info(message);
    }


    public static void write(String message){
        String time = Log4j.sdf.format(new Date());
        String threadThreadName = Thread.currentThread().getName();
        String _message = String.format("【%s】【%s】\t%s\n",time,threadThreadName,message);
        Log4j.writeLogToSpecFile("./logs/jdbc/"+ Log4j.sdfDict.format(new Date()),Log4j.sdfFile.format(new Date()),_message);
    }

    public static void error(String desc,Throwable e){
        desc = desc == null ? "":desc ;
        write("desc = "+desc+",\n"+StringUtil.printExceptInfo(e));
        Log4j.error(desc,e);
    }


}
