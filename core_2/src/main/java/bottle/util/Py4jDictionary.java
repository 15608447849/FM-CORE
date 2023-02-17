package bottle.util;

import com.google.common.collect.ArrayListMultimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @Author: leeping
 * @Date: 2019/7/10 14:26
 */
public class Py4jDictionary {
    private ArrayListMultimap<String,String> duoYinZiMap;

    private static final String CONFIG_NAME = "py4j.dic";

    private static final String PINYIN_SEPARATOR = "#";

    private static final String WORD_SEPARATOR = "/";

    private volatile boolean inited;

    private Py4jDictionary(){

    }

    public void init(){
        if(inited){
            return;
        }
        Enumeration<URL> configs = null;
        try{
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            configs = cl.getResources(CONFIG_NAME);
        } catch (Exception e){
            e.printStackTrace();
        }

        this.duoYinZiMap = parse(configs);
        inited = true;
    }

    private ArrayListMultimap<String,String> parse(Enumeration<URL> configs){
        ArrayListMultimap<String,String> duoYinZiMap = ArrayListMultimap.create(512, 16);
        if(configs!=null){
            while (configs.hasMoreElements()) {
                parseURL(configs.nextElement(), duoYinZiMap);
            }
        }
        return duoYinZiMap;
    }

    private void parseURL(URL url, ArrayListMultimap<String, String> duoYinZiMap){
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = url.openStream();
            br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {

                String[] arr = line.split(PINYIN_SEPARATOR);

                if (arr[1]!=null && arr[0].length()>0) {
                    String[] dyzs = arr[1].split(WORD_SEPARATOR);
                    for (String dyz : dyzs) {
                        if (dyz!=null && dyz.length()>0) {
                            duoYinZiMap.put(arr[0], dyz.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("load py4j config:%s error", url), e);
        } finally {
            try{
                if (in!=null) in.close();
                if (br!=null) br.close();
            }catch (Exception ignored){

            }
        }
    }

    ArrayListMultimap<String,String> getDuoYinZiMap(){
        return duoYinZiMap;
    }

    public static Py4jDictionary getDefault(){
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final Py4jDictionary INSTANCE = new Py4jDictionary();
    }

}
