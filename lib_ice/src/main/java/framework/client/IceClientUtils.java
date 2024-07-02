package framework.client;

import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import bottle.util.GoogleGsonUtil;
import com.onek.server.inf.IRequest;
import com.onek.server.inf.InterfacesPrx;
import com.onek.server.inf.InterfacesPrxHelper;

import java.util.HashMap;

/**
 * @Author: leeping
 * @Date: 2019/4/9 14:15
 * ice客户端远程调用
 */

public class IceClientUtils {


    private static Ice.Communicator createICE(String tag,String serverAdds) {
        StringBuilder sb = new StringBuilder("--Ice.Default.Locator="+tag+"/Locator");

        String[] infos = serverAdds.split(";");

        for (String info : infos){
            String[] host_port = info.split(":");
            sb.append(":tcp -h ").append(host_port[0]).append(" -p ").append(host_port[1]);
        }

        return Ice.Util.initialize( new String[]{sb.toString()});
    }


    private static Ice.Communicator pauseTag(String tags){
        //实例名@主机地址:端口
        try {
            String[] sarr = tags.split("@");
            String tag = sarr[0];
            String serverAdds = sarr[1];
            return createICE(tag,serverAdds);
        } catch (Exception e) {
            throw new RuntimeException("无法连接: " + tags);
        }
    }

    public static String request(String tags,String serverName,String pkg,String cls,String med,String token,int page,int index, String[] array){
        try {
            Ice.Communicator ic = pauseTag(tags);
            Ice.ObjectPrx base = ic.stringToProxy(serverName);
            InterfacesPrx curPrx =  InterfacesPrxHelper.checkedCast(base);
            curPrx.ice_invocationTimeout(30000);
            IRequest request = new IRequest();
            request.pkg = pkg;
            request.cls = cls;
            request.method = med;
            request.param.token = token;
            request.param.pageNumber = page;
            request.param.pageIndex = index;
            request.param.arrays = array;
            return curPrx.accessService(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String request(String tags,String serverName,String pkg,String cls,String med,String token,int page,int index, Object obj){
        try {
            Ice.Communicator ic = pauseTag(tags);
            Ice.ObjectPrx base = ic.stringToProxy(serverName);
            InterfacesPrx curPrx =  InterfacesPrxHelper.checkedCast(base);
            curPrx.ice_invocationTimeout(30000);
            IRequest request = new IRequest();
            request.pkg = pkg;
            request.cls = cls;
            request.method = med;
            request.param.token = token;
            request.param.pageNumber = page;
            request.param.pageIndex = index;
            request.param.json = GoogleGsonUtil.javaBeanToJson(obj);
            return curPrx.accessService(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     *  ice://InstanceName@host:port/服务名/classpath#method
     *  ice://ERPDIS@127.0.0.1:5061/serverName/framework.client.IceClient#XXX
     * */
    public static String requestPro(String url,String token,int page,int index,Object obj){
        if (!url.startsWith("ice://")) throw new IllegalArgumentException("protocol not supported.");
        url = url.replace("ice://","");
        String[] split = url.split("/");

        String tags = split[0];
        String svrn = split[1];
        String pkg_cls_med = split[2];
        String pkg_cls = pkg_cls_med.split("#")[0];
        String pkg = null;
        String cls ;
        String med = pkg_cls_med.split("#")[1];

        if (pkg_cls.contains(".")){
            cls = pkg_cls.split("\\.")[pkg_cls.split("\\.").length-1];
            pkg = pkg_cls.replace("."+cls,"");
        }else {
            cls = pkg_cls;
        }

       return request(tags,svrn,pkg,cls,med,token,page,index,obj);

    }

    /**
     *
     *  ice://InstanceName@host:port/服务名/classpath#method
     *  ice://ERPDIS@127.0.0.1:5061/serverName/framework.client.IceClient#XXX
     * */
    public static String requestPro(String url,String token,int page,int index,String[] arrays){
        if (!url.startsWith("ice://")) throw new IllegalArgumentException("protocol not supported.");
        url = url.replace("ice://","");
        String[] split = url.split("/");

        String tags = split[0];
        String svrn = split[1];
        String pkg_cls_med = split[2];
        String pkg_cls = pkg_cls_med.split("#")[0];
        String pkg = null;
        String cls ;
        String med = pkg_cls_med.split("#")[1];

        if (pkg_cls.contains(".")){
            cls = pkg_cls.split("\\.")[pkg_cls.split("\\.").length-1];
            pkg = pkg_cls.replace("."+cls,"");
        }else {
            cls = pkg_cls;
        }

        return request(tags,svrn,pkg,cls,med,token,page,index,arrays);

    }

}
