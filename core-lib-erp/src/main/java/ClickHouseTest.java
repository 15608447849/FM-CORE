import bottle.util.TimeTool;
import jdbc.define.option.Page;
import jdbc.imp.TomcatJDBC;
import jdbc.imp.TomcatJDBCDAO;
import jdbc.imp.TomcatJDBCTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: leeping
 * @Date: 2020/7/22 10:59
 */
public class ClickHouseTest {


    public static void main(String[] args) throws Exception{

        TomcatJDBC.initialize("db-clickHouse.properties");

        String sql = "SELECT oid,operator, operate_datetime, operate_business_model, operate_type,operate_value FROM {{?tb_operate_all}}";

        int number = 10;

        final CountDownLatch countDownLatch = new CountDownLatch(number);
        for (int i = 0;i<number;i++){
            final  int index = i;
            new Thread(() -> {
                try {

                    List<Object[]> lines = TomcatJDBCDAO.query_clickHouse(sql,new Page(1,1000));
                    System.out.println(Thread.currentThread()+ " index - " + index+ " - 获取数据大小: "+ lines.size());

                } catch (Exception e) {
                    System.out.println("错误: "+ e);
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
        Thread.sleep(6 * 60 * 1000L);
        TomcatJDBC.destroy();

    }





}
