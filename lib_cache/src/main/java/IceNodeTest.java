import redis.RedisCacheUtil;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

public class IceNodeTest {

    public static void main(String[] args) {




//        Set<String> wms_dimension_outbound_task_set =
//                RedisCacheUtil.getSetProvide().getAllElements("WMS_DIMENSION_OUTBOUND_TASK_SET");
//
//        for (String s : wms_dimension_outbound_task_set) {
//            System.out.println(s);
//        }


        Jedis jedis = new Jedis("47.106.209.254",6390,0);
        jedis.auth("Fspace@onekdrug94327");
//        String ping = jedis.ping();
//        System.out.println(ping);
        Set<String> wms_dimension_outbound_task_set = jedis.smembers("WMS_DIMENSION_OUTBOUND_TASK_SET");
        wms_dimension_outbound_task_set.forEach(System.out::println);


//        String wms_dimension_outbound_task_set = (String) map;
//        System.out.println(wms_dimension_outbound_task_set);

    }
}
