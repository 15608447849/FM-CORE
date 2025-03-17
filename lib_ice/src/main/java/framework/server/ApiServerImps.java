package framework.server;

import Ice.Communicator;
import Ice.Current;
import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.util.ClassInstanceStorage;
import bottle.util.EncryptUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import com.onek.server.inf.IRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static bottle.log.PrintLogThread.addMessageQueue;
import static bottle.util.StringUtil.printExceptInfo;


/**
 * ice bind type = ::inf::Interfaces
 * 接口实现
 */
public class ApiServerImps extends IMServerImps{
    // 简略响应最大长度
    private static final int BRIEF_MAX = 1024;
    // 是否允许打印信息
    public static boolean isAllowPrintInformation = true;

    // 默认包路径
    private String packagePath;
    // 方法名 - 参数序列
    private final HashMap<String,String> idempotentMap = new HashMap<>();

    private final Timer idempotentTimer = new Timer();
    //拦截器
    private final ArrayList<Interceptor> interceptorList  = new ArrayList<>();

    private final ScanApplicationClassCallback.Adapter scanApplicationClassCallback = new ScanApplicationClassCallback.Adapter(){

        @Override
        public void findClass(Class<?> classType) throws Exception {
            //查询所有拦截器对象
            try {
                if ( !classType.equals(Interceptor.class) && Interceptor.class.isAssignableFrom(classType)){
                    //拦截器
                    Interceptor iServerInterceptor = ClassInstanceStorage.getInstance(classType);
                    interceptorList.add(iServerInterceptor);
                    Log4j.info("加入 业务实现拦截器: "+ iServerInterceptor );

                }
            } catch (Exception ignored) {
            }
        }

        @Override
        public void executeClass(String serverName, String rpcGroupName, Communicator communicator) {
            interceptorList.sort(Comparator.comparingInt(Interceptor::getPriority));
        }

        @Override
        public ScanApplicationClassCallback next() {
            return ApiServerImps.super.getExpandScanCallback();
        }
    };


    private String responseBrief(String resultString){
        if (resultString.length()> BRIEF_MAX){
            return resultString.substring(0,BRIEF_MAX) + "...省略" + (resultString.length()-BRIEF_MAX) + "字符,共"+resultString.getBytes().length+"字节";
        }
        return resultString;
    }

    /* 完整访问记录 */
    private void accessCompleteRecode(StringBuilder sb) {
        addMessageQueue(new LogBean(LogLevel.info, sb.toString())
                .setPrintLog(false)
        );
    }
    /* 简略访问记录 */
    private void accessBriefRecode(StringBuilder sb) {
        addMessageQueue(new LogBean(
                "./logs/ice/"+Log4j.sdfDict.format(new Date()),
                Log4j.sdfFile.format(new Date()),
                sb.toString()+"\n")
                .setEnableCallback(false)
        );
    }

    /* 及时访问记录 */
    private void accessBriefTimely(String title, String callSeq, String sb) {

        String prefix = String.format("【%s】【%s】【%s】\n" , Log4j.sdf.format(new Date()) , callSeq, title );
        addMessageQueue(new LogBean(
                "./logs/ice/"+Log4j.sdfDict.format(new Date()),
                "timely.log",
                prefix + sb+"\n")
                .setEnableCallback(false)
        );
    }

    @Override
    void launchService(Communicator communicator, String serverName, boolean isPushServer, String packagePath) {
        super.launchService(communicator, serverName, isPushServer, packagePath);
        this.packagePath = packagePath;
    }

    @Override
    protected ScanApplicationClassCallback getExpandScanCallback() {
        return scanApplicationClassCallback;
    }

    //打印参数
    private String requestParam(IRequest request, Current __current, String detail) throws Exception{
                StringBuilder sb = new StringBuilder();

                if (__current != null && __current.con!=null) {
                    sb.append(__current.con.toString().split("\n")[1].replace("remote address =","REMOTE ADDRESS：\t").trim());
                }
                sb.append("\nTOKEN：\t").append(request.param.token);

                sb.append("\nPATH：\t").append(request.pkg).append(".").append(request.cls).append(".").append(request.method);
                if (detail!=null && detail.length()>0) sb.append("\t【").append(detail).append("】");

                sb.append("\nJSON：\t").append(request.param.json);
                sb.append("\nARRAY：\t").append(Arrays.toString(request.param.arrays));
                sb.append("\nPAGE：\t").append(request.param.pageIndex).append(" , ").append(request.param.pageNumber);

                sb.append("\nEXTEND：\t").append(request.param.extend);
                return sb.toString();
    }

    //拦截
    private boolean interceptor(IceSessionContext context){
        for (Interceptor iServerInterceptor : interceptorList) {
            iServerInterceptor.intercept(context);
            if (context.getResult().isIntercept()){
                //设置已拦截
                return true;
            }
        }
        return false;
    }

    public void useDefaultSessionPermission(Class<? extends SessionPermissionInterceptor.CallBack> _callBackClassType){
        SessionPermissionInterceptor.setCallBackClassType(_callBackClassType);
        interceptorList.add(0,new SessionPermissionInterceptor());
    }

    //打印结果
    private String convertResultToJson(Object result) {
        String resultString;
        if (result instanceof String){
            resultString = String.valueOf(result);
        }else{
            resultString = GoogleGsonUtil.javaBeanToJson(result);
        }
        return resultString;
    }



    private boolean checkIdempotentOperate(IceSessionContext context, long interval) {
        final String key = context.getCallerSTR();
        String value = context.getParamSTR();
        String _value = idempotentMap.get(key);
        if (_value == null) {
            idempotentMap.put(key,value);
            idempotentTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //指定时间后移除
                    idempotentMap.remove(key);
                }
            },interval);
            return false;
        }
        return value.equals(_value);
    }

    //客户端 - 接入服务
    @Override
    public String accessService(IRequest request, Current __current) {
        Date queryTime = new Date(); // 请求时间
        Api api = null;
        String callSeq = null;//请求序列
        Object result = null;//响应结果
        String queryInfo = null;
        String executeErrorStr = null;
        String resultString = null;
        String executeTimeStr = null;

        IceSessionContext context = null;
        long executeTime = System.currentTimeMillis();
        try {
            //创建context
            context = IceSessionContext.create(__current,packagePath,request);
            api = context.getApi();
            callSeq = EncryptUtil.encryption(context.getCallerSTR()+context.getParamSTR()+queryTime.getTime());
            queryInfo = requestParam(request, __current, api==null? "未使用@Api": api.detail());
            if (api != null && api.idempotent()){
                //连续调用检查
                if (checkIdempotentOperate(context,api.idempotentInterval())) throw new IllegalStateException("BUSY");
            }

            accessBriefTimely(callSeq,"Request",queryInfo);
            if (interceptor(context)) { //拦截
                result = context.getResult();
            }else{
                //具体业务实现调用 返回值不限制
                result = context.call();
            }

        } catch (Exception e) {

            String callerPath = "接口("+ ( context == null ? request.cls+"/"+request.method : context.getCallerFullPath() ) +")\t";
            String errorMsg = e.toString();

            if(e instanceof NoSuchMethodException || e instanceof ClassNotFoundException){
                result = Result.create().error(callerPath +" 匹配失败",  errorMsg);
            }
            else if (e instanceof IllegalStateException && e.getMessage().equals("BUSY")){
                result = Result.create().error(callerPath +" 接口繁忙",  errorMsg);
            }
            else{
                Throwable targetEx = e;
                if (e instanceof InvocationTargetException) {
                    targetEx =((InvocationTargetException)e).getTargetException();
                }

                executeErrorStr  = printExceptInfo(targetEx);
                result = Result.create().error(callerPath+" 执行异常", executeErrorStr);
            }
        }finally {
            resultString =  convertResultToJson(result);
            if (context!=null){
                context.destroy();
            }
        }
        executeTimeStr = System.currentTimeMillis() - executeTime +" 毫秒";
        accessBriefTimely(callSeq,"Response\t"+executeTimeStr,responseBrief(resultString));
        recodeInfoAndPrintConsole(api,queryTime, callSeq,queryInfo,resultString,executeTimeStr,executeErrorStr);
        return resultString;
    }

    //记录请求过程并打印结果
    private void recodeInfoAndPrintConsole(Api api,Date queryTime,String callSeq,String queryInfo, String resultString, String executeTimeStr, String executeErrorStr) {

        String prefix = String.format("【%s】\t【%s】\n" , Log4j.sdf.format(queryTime) , callSeq );
        StringBuilder complete = new StringBuilder(prefix) ;
        StringBuilder brief = new StringBuilder(prefix);

        //请求信息
        if (queryInfo!=null) {
            complete.append(queryInfo);
            brief.append(queryInfo);
        }
        //响应信息
        if (resultString!=null) {
            complete.append("\nRESPONSE：\t").append(resultString);
            brief.append("\nRESPONSE：\t").append(responseBrief(resultString));
        }
        //错误信息
        if (executeErrorStr!=null){
            complete.append("\nEXECUTE ERROR：\t").append(executeErrorStr);
            brief.append("\nEXECUTE ERROR：\t").append(executeErrorStr);

        }
        // 执行请求耗时
        if(executeTimeStr!=null) {
            complete.append("\nEXECUTE TIME：\t").append(executeTimeStr);
            brief.append("\nEXECUTE TIME：\t").append(executeTimeStr);
        }

        // 写入日志
        if (complete.length() > 0) {
            accessCompleteRecode(complete);
            accessBriefRecode(brief);
        }

        if (isAllowPrintInformation){
            StringBuilder console = new StringBuilder();
            if(api!=null && api.inPrint()  || executeErrorStr != null ){
                console.append(queryInfo);// 请求信息
            }

            if(api!=null && api.outPrint()){
                console.append(resultString); // 响应信息
                if (executeTimeStr!=null){
                    console.append("\n执行耗时").append(executeTimeStr);
                }
            }

            if (executeErrorStr!=null){
                console.append("\n").append(executeErrorStr); // 错误信息
            }
            // 控制台输出
            if (console.length()>0) System.out.println(prefix + console);
        }
    }


    @Override
    public void stopService() {
        super.stopService();
        interceptorList.clear();
        idempotentTimer.cancel();
    }

}
