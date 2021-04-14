
/**
 * @Author: leeping
 * @Date: 2020/9/8 10:39
 */
public class DyLoadTest {

    static {
        System.out.println("动态加载类: "+ DyLoadTest.class);
    }

    public void instanceExecute(){
        System.out.println(this +" 执行");
//        Log4j.info("---------instanceExecute----------");
    }

    public static void classExecute(){
        System.out.println( DyLoadTest.class + " 执行");
//        Log4j.info("---------classExecute----------");
    }
}
