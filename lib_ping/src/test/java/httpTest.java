import bottle.util.GoogleGsonUtil;
import bottle.util.HttpRequest;
import bottle.util.HttpUtil;

import java.io.File;
import java.util.HashMap;

public class httpTest {
    public static void main(String[] args) {
//        HttpRequest httpRequest = new HttpRequest()
//                .addFile(new File("C:\\Users\\Administrator\\Pictures\\1.png"), null,null)
//                .addFile(new File("C:\\Users\\Administrator\\Pictures\\2.png"), null,null)
//                .fileUploadUrl("http://10.15.0.110:8099/upload");
//        System.out.println(httpRequest.getRespondContent());


        HashMap<String,String> jsonBean = new HashMap<>();
//       jsonBean.put("classifyTree","1");
       jsonBean.put("needProd","1");
       jsonBean.put("cfyCode","10010000");
       jsonBean.put("cfyLevel","2");
        System.out.println(HttpUtil.contentToHttpBody("http://10.15.0.110:8099/classify_product_table/json","POST", GoogleGsonUtil.javaBeanToJson(jsonBean)));

    }
}
