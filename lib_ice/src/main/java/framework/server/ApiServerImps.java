package framework.server;

import Ice.Communicator;
import Ice.Current;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import bottle.util.StringUtil;
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

    private String packagePath;

    /* 记录参数进入日志 */
    private static String writeInputParameters(StringBuilder sb) {
        String str = sb.toString();
        String time = Log4j.sdf.format(new Date());
        String _message = String.format("【%s】\t%s\n",time,str);
        Log4j.writeLogToSpecFile("./logs/ice/"+Log4j.sdfDict.format(new Date()),Log4j.sdfFile.format(new Date()),_message);
        return str;
    }

    @Override
    void launchService(Communicator communicator, String serverName, boolean isPushServer, String packagePath) {
        super.launchService(communicator, serverName, isPushServer, packagePath);
        this.packagePath = packagePath;
    }

    //查询所有拦截器对象
    @Override
    public void findJarAllClass(Class<?> classType)  {
        super.findJarAllClass(classType);
        try {

            if ( !classType.equals(Interceptor.class) && Interceptor.class.isAssignableFrom(classType)){
                //拦截器
                Interceptor iServerInterceptor = (Interceptor) classType.newInstance();
                interceptorList.add(iServerInterceptor);
                interceptorList.sort(Comparator.comparingInt(Interceptor::getPriority));
            }

        } catch (Exception ignored) {
        }
    }

    //打印参数
    private String printParam(IRequest request, Current __current, String ditail) throws Exception{
                StringBuilder sb = new StringBuilder();

                if (__current != null && __current.con!=null) {
                    sb.append(__current.con.toString().split("\n")[1].replace("remote address =","客户端地址:").trim());
                }else{
                    sb.append("本地调用");
                }

                sb.append(",路径: ").append(request.pkg).append(".").append(request.cls).append(".").append(request.method).append(",详情: ").append(ditail);

                if(!StringUtil.isEmpty(request.param.token)){
                    sb.append("\ntoken:\t").append(request.param.token);
                }
                if(!StringUtil.isEmpty(request.param.json)){
                    sb.append("\nJSON参数:\t").append(request.param.json);
                }
                if(request.param.arrays!=null &&request.param.arrays.length>0){
                    sb.append("\n数组参数:\t").append(Arrays.toString(request.param.arrays));
                }
                if(request.param.pageIndex > 0 && request.param.pageNumber > 0){
                    sb.append("\n分页参数:\t").append(request.param.pageIndex).append(" , ").append(request.param.pageNumber);
                };
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
        try {
            //创建context
            context = IceSessionContext.create(__current,packagePath,request);

            api = context.getApi();

            queryInfo = printParam(request, __current, api==null? "未使用@api注解": api.detail());

            if (api!=null && api.idempotent()){
                //连续调用检查
                if (idempotent(context,api.idempotentInterval())) throw new IllegalStateException("BUSY");
            }

            if (interceptor(context)) { //拦截
                result = context.getResult();
            }else{
                long executeTime = System.currentTimeMillis();
                //具体业务实现调用 返回值不限制
                result = context.call();
                executeTimeStr = TimeTool.formatDuring(System.currentTimeMillis() - executeTime);
            }

        } catch (Exception e) {
            String callerPath = "接口("+ ( context == null?"unknown":context.getCallerFullPath() ) +")\t";
            String errorMsg = e.toString();

            if(e instanceof NoSuchMethodException){
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
                result = Result.create().error(callerPath+" 执行异常", errorMsg);
            }
        }finally {
            resultString =  convertResultToJson(result);
            if (context!=null){
                context.destroy();
            }
        }

       recodeInfoAndPrintConsole(api,queryInfo,resultString,executeTimeStr,executeErrorStr);

        return resultString;
    }

    //记录请求过程并打印结果
    private void recodeInfoAndPrintConsole(Api api,String queryInfo, String resultString, String executeTimeStr, String executeErrorStr) {
        StringBuilder recode = new StringBuilder();
        //请求信息
        if (queryInfo!=null) recode.append("\n").append(queryInfo);
        //响应信息
        if (resultString!=null) recode.append("\n响应内容:\t").append(resultString);
        //执行请求耗时
        if(executeTimeStr!=null) recode.append("\n执行耗时:\t").append(executeTimeStr);
        //错误信息
        if (executeErrorStr!=null) recode.append("\n").append(executeErrorStr);

        if (recode.length() > 0) writeInputParameters(recode);

        if (isAllowPrintInformation){
            recode = new StringBuilder();

            if(api!=null && api.inPrint()  || executeErrorStr!=null ){
                recode.append("\n").append(queryInfo);
            }

            if(api!=null && api.outPrint()){
                recode.append("\n响应内容:\t").append(resultString);
                if (executeTimeStr!=null){
                    recode.append("\n执行耗时:\t").append(executeTimeStr);
                }
            }

            if (executeErrorStr!=null){
                recode.append("\n错误栈信息\n").append(executeErrorStr);
            }

            // 控制台输出
            if (recode.length()>0) System.out.println(recode);
        }
    }


    @Override
    public void stopService() {
        super.stopService();
        interceptorList.clear();
        idempotentTimer.cancel();
    }

}
