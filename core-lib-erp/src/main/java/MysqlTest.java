import bottle.threadpool.IOThreadPool;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import bottle.util.TimeTool;
import jdbc.define.option.DaoApi;
import jdbc.define.option.Page;
import jdbc.imp.TomcatJDBC;
import jdbc.imp.TomcatJDBCDAO;
import jdbc.imp.TomcatJDBCTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: leeping
 * @Date: 2020/7/22 10:59
 */
public class MysqlTest {


    public static void main(String[] args) throws Exception{

        TomcatJDBC.initialize("db-mysql.properties");

        String sql = " insert into {{?LEEZP}} (a,b,c,d) values (?,?,?,?)";


        final List<String> sqlList = new ArrayList<>();
        final List<Object[]> paramList = new ArrayList<>();
        sqlList.add(sql);
        paramList.add(new Object[]{1,2,3,4});
        sqlList.add(sql);
        paramList.add(new Object[]{5,6,7,8});

        String startTime = TimeTool.date_yMd_Hms_2Str(new Date());
        int number = 10;
        long time = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(number);
        for (int i = 0;i<number;i++){
            final  int index = i;
            new Thread(() -> {
                try {

                    String currentThread = Thread.currentThread().getName();
                    String currentTime = TimeTool.date_yMd_Hms_2Str(new Date());
//                    int res = TomcatJDBCDAO.update(sql,new Object[]{startTime,currentThread,currentTime,index});
                    int res = TomcatJDBCDAO.update(sqlList,paramList);
//                    System.out.println(res);
                } catch (Exception e) {
                    System.out.println("错误: "+ e);
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        TomcatJDBC.destroy();
        System.out.println("---OK--- "  +(  System.currentTimeMillis() - time ) +" ms");

    }





}
