import bottle.util.HttpUtil;
import redis.RedisCacheUtil;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class IceNodeTest2 {

    public static void main(String[] args) {

//        String lsp = RedisCacheUtil.getStringProvide().get("LSP");
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>"+ lsp);

        String get = HttpUtil.formText("https://www.onekdrug.com:9999/15608447849/openai_key", "GET", null);
        System.out.println(
                get

        );
        System.out.println(
                get.length()

        );
    }
}
