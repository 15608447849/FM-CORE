import bottle.util.GoogleGsonUtil;
import bottle.util.HttpUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: leeping
 * @Date: 2020/3/10 10:16
 */
public class HttpTest {
    public static void main(String[] args) {
        String url = "http://10.15.0.110:8099/classify_product_table/json";

        Map<String,Object> map = new HashMap<>();
        map.put("classifyTree",1);
        map.put("needProd",0);
        map.put("cfyCode",0);
        map.put("cfyLevel",0);

        String resJson = HttpUtil.contentToHttpBody(url,"POST", GoogleGsonUtil.javaBeanToJson(map));
        System.out.println(resJson);
    }
}
