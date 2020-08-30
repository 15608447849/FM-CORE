package framework.server;

import Ice.Communicator;
import Ice.Current;
import bottle.objectref.ObjectRefUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import com.onek.server.inf.IRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

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

    //会被回调- 全类查询所有拦截器对象
    @Override
    public void callback(String classPath)  {
        super.callback(classPath);
        try {
            //循环类
            Class<?> cls = Class.forName(classPath);
            if ( !cls.equals(Interceptor.class) && Interceptor.class.isAssignableFrom(cls)){
                //拦截器
                Interceptor iServerInterceptor = (Interceptor) ObjectRefUtil.createObject(classPath);
                interceptorList.add(iServerInterceptor);
//                print(Thread.currentThread()+"添加拦截器:"+ iServerInterceptor.getClass());
                interceptorList.sort(Comparator.comparingInt(Interceptor::getPriority));
            }

        } catch (Exception e) {
            e.printStackTrace();
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

                sb.append(",路径: " + request.pkg +"." + request.cls +"."+request.method + ",详情: "+ ditail);

                if(!StringUtil.isEmpty(request.param.token)){
                    sb.append( "\ntoken:\t"+ request.param.token);
                }
                if(!StringUtil.isEmpty(request.param.json)){
                    sb.append("\nJSON参数:\t" + request.param.json );
                }
                if(request.param.arrays!=null &&request.param.arrays.length>0){
                    sb.append("\n数组参数:\t" + Arrays.toString(request.param.arrays));
                }
                if(request.param.pageIndex > 0 && request.param.pageNumber > 0){
                    sb.append("\n分页参数:\t"+ request.param.pageIndex +" , " +request.param.pageNumber);
                }
                ;
                return writeInputParameters(sb);
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
    private String printResult(Object result) {
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
        long t = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        String queryInfo = null;
        Object result;
        Api api = null;
        IceSessionContext context = null;
        boolean isInterceptor = false;
        boolean isIdempotent = false;
        try {
            //创建context
            context = IceSessionContext.create(__current,packagePath,request);

            api = context.getApi();

            queryInfo = printParam(request, __current, api==null? "未使用@api注解": api.detail());

            if (api!=null && api.idempotent()){
                isIdempotent = idempotent(context,api.idempotentInterval());//幂等检查
                if (isIdempotent) throw new IllegalStateException("接口连续调用错误\n" + queryInfo);
            }

            isInterceptor = interceptor(context);//拦截器

            if (api==null || isAllowPrintInformation && api.inPrint() || isInterceptor ) {
                //输出参数
                sb.append("\n"+queryInfo);
            }

            if (isInterceptor) { // 拦截
                result = context.getResult();
            }else{
                //具体业务实现调用 返回值不限制
                long time = System.currentTimeMillis();
                result = context.call();
                if (isAllowPrintInformation && api!=null && api.inPrint() && api.timePrint()) {
                    sb.append("\n执行耗时: " + (System.currentTimeMillis() - time) +" 毫秒");
                }
            }

        } catch (Exception e) {

            if (e instanceof IllegalStateException && isIdempotent){
                throw new RuntimeException(e);
            }

            if(api!=null && !api.inPrint() && queryInfo!=null){
                sb.append("\n"+queryInfo);
            }

            if(e instanceof NoSuchMethodException){
                result = Result.create().error(" no matching",e.toString());
            }else{
                Throwable targetEx = e;
                if (e instanceof InvocationTargetException) {
                    targetEx =((InvocationTargetException)e).getTargetException();
                }
                sb.append(printExceptInfo(targetEx));
                result = Result.create().error("wrong","execution error");
            }
        }

        String resultString =  printResult(result);

        if (isAllowPrintInformation && api!=null && api.outPrint() || isInterceptor){
            sb.append("\n响应数据:\t" +resultString);
        }

        if (sb.length()>0){
            print(sb.toString() +"\n整体耗时: " + (System.currentTimeMillis() - t)+" 毫秒");
        }

        if (context!=null){
            context.destroy();
        }
        return resultString;
    }


    @Override
    public void stopService() {
        super.stopService();
        interceptorList.clear();
        idempotentTimer.cancel();
    }

}
