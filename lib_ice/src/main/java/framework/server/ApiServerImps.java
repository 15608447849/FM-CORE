package framework.server;

import Ice.Communicator;
import Ice.Current;
import bottle.util.ClassInstanceStorage;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import bottle.util.TimeTool;
import com.onek.server.inf.IRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static bottle.util.StringUtil.printExceptInfo;


/**
 * ice bind type = ::inf::Interfaces
 * 接口实现
 */
public class ApiServerImps extends IMServerImps{


    public static boolean isAllowPrintInformation = true;

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

    private String packagePath;

    /* 记录参数进入日志 */
    private static String writeInputParameters(StringBuilder sb) {
        String _message = sb.toString();
        Log4j.writeLogToSpecFile("./logs/ice/"+Log4j.sdfDict.format(new Date()),Log4j.sdfFile.format(new Date()),_message+"\n");
        return _message;
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
                sb.append("【").append( Log4j.sdf.format(new Date())).append("】");

                if (__current != null && __current.con!=null) {
                    sb.append("\n")
                            .append(__current.con.toString().split("\n")[1]
                            .replace("remote address =","REMOTE ADDRESS：\t")
                            .trim());
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

    //方法名 - 参数序列
    private final HashMap<String,String> idempotentMap = new HashMap<>();
    private final Timer idempotentTimer = new Timer();

    private boolean idempotent(IceSessionContext context, long interval) {
        final String key = context.getCallerSTR();
        String value = context.getParamSTR();
        String _value = idempotentMap.get(key);
        if (_value == null) {
            idempotentMap.put(key,value);
            idempotentTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    idempotentMap.remove(key);
                }
            },interval); //一秒内禁止点击
            return false;
        }
        return value.equals(_value);
    }

    //客户端 - 接入服务
    @Override
    public String accessService(IRequest request, Current __current) {

        Api api = null;
        Object result = null;//响应结果
        String queryInfo = null;
        String executeTimeStr = null;
        String executeErrorStr = null;
        String resultString ;

        IceSessionContext context = null;
        long executeTime = System.currentTimeMillis();
        try {
            //创建context

            context = IceSessionContext.create(__current,packagePath,request);

            api = context.getApi();

            queryInfo = requestParam(request, __current, api==null? "未使用@Api": api.detail());

            if (api!=null && api.idempotent()){
                //连续调用检查
                if (idempotent(context,api.idempotentInterval())) throw new IllegalStateException("BUSY");
            }

            if (interceptor(context)) { //拦截
                result = context.getResult();
            }else{
                //具体业务实现调用 返回值不限制
                result = context.call();
//                TimeTool.formatDuring(System.currentTimeMillis() - executeTime);
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
        recodeInfoAndPrintConsole(api,queryInfo,resultString,executeTimeStr,executeErrorStr);
        return resultString;
    }

    //记录请求过程并打印结果
    private void recodeInfoAndPrintConsole(Api api,String queryInfo, String resultString, String executeTimeStr, String executeErrorStr) {
        StringBuilder recode = new StringBuilder();
        //请求信息
        if (queryInfo!=null) recode.append(queryInfo);
        //响应信息
        if (resultString!=null) {
            String _resultString = resultString;
            if (resultString.length()>1024){
                _resultString = resultString.substring(0,1024)+"...省略"+(resultString.length()-1024)+"字符";
            }
            recode.append("\nRESPONSE：\t").append(_resultString);
        }
        //错误信息
        if (executeErrorStr!=null) recode.append("\nEXECUTE ERROR：\t").append(executeErrorStr);
        //执行请求耗时
        if(executeTimeStr!=null) recode.append("\nEXECUTE TIME：\t").append(executeTimeStr);
        // 写入日志
        if (recode.length() > 0) writeInputParameters(recode);

        if (isAllowPrintInformation){
            StringBuilder console = new StringBuilder();
            if(api!=null && api.inPrint()  || executeErrorStr!=null ){
                console.append(queryInfo);
            }

            if(api!=null && api.outPrint()){
                console.append(resultString);
                if (executeTimeStr!=null){
                    console.append("\n执行耗时").append(executeTimeStr);
                }
            }

            if (executeErrorStr!=null){
                console.append("\n").append(executeErrorStr);
            }
            // 控制台输出
            if (console.length()>0) System.out.println(console);
        }
    }


    @Override
    public void stopService() {
        super.stopService();
        interceptorList.clear();
        idempotentTimer.cancel();
    }

}
