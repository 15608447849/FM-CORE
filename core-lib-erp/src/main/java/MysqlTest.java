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
//        TomcatJDBC.initialize("db-10-12-0-241.properties");



    }





}
