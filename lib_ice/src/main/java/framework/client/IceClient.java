package framework.client;


import Ice.Communicator;
import Ice.NoEndpointException;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import bottle.util.GoogleGsonUtil;
import com.onek.server.inf.IParam;
import com.onek.server.inf.IRequest;
import com.onek.server.inf.InterfacesPrx;
import com.onek.server.inf.InterfacesPrxHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author: leeping
 * @Date: 2019/4/9 14:15
 * ice客户端远程调用
 */
public class IceClient {

    private static class RequestStore {
        private InterfacesPrx curPrx;
        private IRequest request;
    }

    private static final ThreadLocal<RequestStore> threadLocalStore = new ThreadLocal<>();

    private RequestStore getCurrentThreadRequestStore(){
        RequestStore store = threadLocalStore.get();
        if (store == null){
            store = new RequestStore();
            threadLocalStore.set(store);
        }
        return store;
    }

    private  Ice.Communicator ic = null;

    private final String[] args ;

    private boolean isInside = false;

    private int timeout = 300000;

    /* 本地通讯端点 */
    private Ice.ObjectAdapter localAdapter;

    public ObjectAdapter getLocalAdapter(){
        if (ic == null) throw new IllegalStateException("ICE COMMUNICATION NOT START.");
        return localAdapter;
    }

    private IceClient(Communicator communicator) {
        ic = communicator;
        isInside = true;
        args = null;
    }

    /** 内部调用的客户端通讯器 */
    public static IceClient Inside(Communicator communicator) {
        return new IceClient(communicator);
    }

    public IceClient(String tag,String serverAdds) {
        args = initParams(tag,serverAdds,"idleTimeOutSeconds=300","--Ice.MessageSizeMax=4096");
    }

    /** serverAdds = IP_1:PORT_1;IP_2:PORT_2; argsStr = idleTimeOutSeconds=300,--Ice.MessageSizeMax=4096 */
    public IceClient(String tag,String serverAdds,String argsStr) {
        args = initParams(tag,serverAdds,argsStr.split(","));
    }

    private String[] initParams(String tag,String serverAdds,String... iceArgs) {
        String[] arr;
        if (iceArgs == null) {
            arr = new String[1];
        }else{
            arr = new String[iceArgs.length + 1];
            System.arraycopy(iceArgs, 0, arr, 1, iceArgs.length);
        }
        StringBuilder address = new StringBuilder("--Ice.Default.Locator="+tag+"/Locator");
        String str = ":%s -h %s -p %s";
        String[] infos = serverAdds.split(";");
        for (String info : infos){
            String[] host_port = info.split(":");
            if (host_port.length == 3){
                address.append(String.format(Locale.CHINA,str, host_port[0],host_port[1],host_port[2]));
            }
            if (host_port.length == 2){
                address.append(String.format(Locale.CHINA,str, "tcp",host_port[0],host_port[1]));
            }

        }
        arr[0] = address.toString();
        return arr;
    }

    public Communicator iceCommunication(){
        return ic;
    }

    public IceClient setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /* 打开连接 */
    synchronized
    public IceClient startCommunication() {
        if (ic == null) {
            ic = Ice.Util.initialize(args);
            localAdapter = ic.createObjectAdapter("");
            localAdapter.activate();
        }
        return this;
    }

    /* 关闭连接 */
    synchronized
    public IceClient stopCommunication() {
        if (ic != null) {
            if (!isInside){
                ic.destroy();
            }
            ic = null;
        }
        return this;
    }

    /* 设置ICE服务名 */
    public IceClient setProxy(String serverName){
        RequestStore store = getCurrentThreadRequestStore();
        store.curPrx = convertProxy(serverName);
        return this;
    }

    private InterfacesPrx convertProxy(String serverName){
        Ice.ObjectPrx base = ic.stringToProxy(serverName).ice_invocationTimeout(timeout);
        return InterfacesPrxHelper.checkedCast(base);
    }

    public InterfacesPrx getProxy(){
        RequestStore store = getCurrentThreadRequestStore();
        return store.curPrx;
    }

    /* 设置ICE请求信息 */
    private IceClient setRequest(String token,String cls,String med){
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        store.request = new IRequest();
        store.request.cls = cls;
        store.request.method = med;
        store.request.param.token = token;
        return this;
    }

    /* 设置ICE 服务,请求信息 */
    public IceClient setRequest(String token,String serverName,String clazz,String method){
        return setProxy(serverName).setRequest(token,clazz,method);
    }

    public IceClient setAnonymousRequest(String serverName,String clazz,String method){
        return setProxy(serverName).setRequest("Anonymous",clazz,method);
    }

    /* 设置数组参数 */
    public IceClient setStringArrayParam(String[] array){
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        if (store.request == null) throw new IllegalArgumentException("请请求信息");
        store.request.param.arrays = array;
        return this;
    }

    /* 设置JSON参数*/
    public IceClient setJsonParam(String json){
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        if (store.request == null) throw new IllegalArgumentException("请请求信息");
        store.request.param.json = json;
        return this;
    }
    /* 设置拓展参数 */
    public IceClient setExtendParam(String extend){
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        if (store.request == null) throw new IllegalArgumentException("请请求信息");
        store. request.param.extend = extend;
        return this;
    }

    /* 设置数组参数 */
    public IceClient setObjectArrayParam(Object[] objects){
        String[] arr = new String[objects.length];
        for (int i = 0; i< objects.length; i++) {
            arr[i] = String.valueOf(objects[i]);
        }
        return setStringArrayParam(arr);
    }

    public  IceClient setArrayParam(Object... objects){
        return setObjectArrayParam(objects);
    }

    /* 设置JSON对象 */
    public IceClient setJsonParam(Object obj){
        return setJsonParam(GoogleGsonUtil.javaBeanToJson(obj));
    }

    /* 设置分页信息 */
    public IceClient setPageParam(int index, int number){
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        if (store.request == null) throw new IllegalArgumentException("请请求信息");
        store.request.param.pageIndex = index;
        store.request.param.pageNumber = number;
        return this;
    }

    public IceClient setIParam(IParam param){
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        if (store.request == null) throw new IllegalArgumentException("请请求信息");
        store.request.param = param;
        return this;
    }

    /* 执行请求 */
    public String execute() {
        RequestStore store = getCurrentThreadRequestStore();
        if (store.curPrx == null) throw new IllegalArgumentException("请设置服务名");
        if (store.request == null) throw new IllegalArgumentException("请请求信息");
        IRequest request = store.request;
        InterfacesPrx curPrx = store.curPrx;
        return curPrx.accessService(request);
    }

    /* 执行,错误将重试最大次数 */
    public String executeErrorRetry(int index,int maxTryNum,int sleep){
            try {
                return execute();
            } catch (Exception e) {
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) { }
                if (index<maxTryNum){
                    return executeErrorRetry(++index,maxTryNum,sleep);
                }
                throw e;
            }
    }


    /* 发送消息 */
    public void sendMessageToClient(String serverName,String identity,String message){
        InterfacesPrx curPrx = null;
        if (serverName!=null){
            Ice.ObjectPrx base = ic.stringToProxy(serverName).ice_invocationTimeout(timeout);
            curPrx = InterfacesPrxHelper.checkedCast(base);
        }else{
            curPrx = getCurrentThreadRequestStore().curPrx;
        }
        if (curPrx == null) throw new IllegalArgumentException("请设置正确的IM服务名");
        curPrx.sendMessageToClient(identity,message);
    }

    public void sendMessageToClient(String identity,String message){
        sendMessageToClient(null,identity,message);
    }

    private static final Map<String,IceClient> iceClientMap = new HashMap<>();

    /*
    自定义协议转ICE客户端
     */
    private static IceClient customURLConvertClient(String customURL){
        if (!customURL.startsWith("ice://")) throw new IllegalArgumentException("ice://实例名@通讯地址([tcp/ws]:IP_1:PORT_1;[tcp/ws]:IP_2:PORT_2)@服务名@类名(可选)@方法名(可选)@Token(可选)" );
        IceClient client = null;
        String[] strArr = customURL.replace("ice://","").trim().split("@");
        if (strArr.length>=3){
            String instanceName = strArr[0].trim();
            String addressInfo = strArr[1].trim();
            client = iceClientMap.get(instanceName+addressInfo);
            if (client == null) {
                client = new IceClient(instanceName,addressInfo);
                client.startCommunication();
                iceClientMap.put(instanceName+addressInfo,client);
            }
            client.setProxy(strArr[2].trim());
        }

        String cls = null;
        String med = null;
        String token = null;

        if (strArr.length>=5){
            cls = strArr[3].trim();
            med = strArr[4].trim();
        }
        if (strArr.length>=6){
            token = strArr[5].trim();
        }
        if (client!=null) {
            client.setRequest(token,cls,med);
        }
        return client;
    }

    public static void executeURLSendMessage(String customURL,String identity,String message){
        IceClient client = customURLConvertClient(customURL);
        if (client == null) throw new IllegalArgumentException("客户端创建失败");
        client.sendMessageToClient(identity,message);
    }

    public static String executeURL(String customURL,String json,String[] array,String extend,int index,int number){
            IceClient client = customURLConvertClient(customURL);
           if (client == null) throw new IllegalArgumentException("客户端创建失败");

           if (json!=null && json.length()>0){
               client.setJsonParam(json);
           }
           if (array!=null && array.length>0){
               client.setStringArrayParam(array);
           }
           if (extend!=null && extend.length()>0){
               client.setExtendParam(extend);
           }
           if (index>0 && number>0){
               client.setPageParam(index,number);
           }

            return client.execute();
    }

    public static String executeURLJson(String customURL,String json,int index,int number){
        return executeURL(customURL,json,null,null,index,number);
    }

    public static String executeURLArray(String customURL,String[] array,int index,int number){
        return executeURL(customURL,null,array,null,index,number);
    }

    public static String executeURL(String customURL){
        return executeURL(customURL,null,null,null,0,0);
    }

    public static String executeURLJson(String customURL,String json){
        return executeURLJson(customURL,json,0,0);
    }

    public static String executeURLArray(String customURL,String[] array){
        return executeURLArray(customURL,array,0,0);
    }

//    public static void main(String[] args) {
//
//        String[] arr = new String[]{"1609311693886000","0","false"};
//        String url = "ice://ERP@tcp:114.115.168.87:5061@phdsServer@DiagnoseReport_ProductStructI@diagnosticDataList@PHDS_LOGIN_USER_7D16807A35C166C13197E2334707E368";
//        String res = IceClient.executeURLArray(url,arr);
//        Log4j.info(res);
//
//    }

}
