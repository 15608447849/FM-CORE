package framework.server;

import Ice.Current;
import bottle.objectref.ObjectPoolManager;
import bottle.objectref.ObjectRefUtil;
import bottle.util.EncryptUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.StringUtil;
import com.onek.server.inf.IParam;
import com.onek.server.inf.IRequest;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static framework.server.ApiRequestHelper.convertFuncRequest;


/**
 * @Author: leeping
 * @Date: 2019/3/8 14:32
 */
public class IceSessionContext {

    private static boolean useAipAnnotation = true;

    private static Class<? extends IceSessionContext> contextImp = IceSessionContext.class;

    public static void setProperty(boolean useApi,Class<? extends IceSessionContext> sub) {
        contextImp = sub;
        useAipAnnotation = useApi;
    }

    static IceSessionContext create(Current current, String packagePath, IRequest request) throws Exception{
        IceSessionContext session = (IceSessionContext) ObjectPoolManager.createObject(contextImp);
        session._createSession(current,packagePath,request);
        return session;
    }

    protected IceSessionContext(){ }

    private final HashMap<Class<?>, Object> classObjectMap = new HashMap<>();

    //客户端连接信息
    private Current current;
    //请求参数
    private IParam param;
    //实际调用者
    private Class<?> callClass;
    private Method callMethod;
    private Api api;

    //方法参数
    private Parameter[] parameters;
    //方法参数类型
    private Class[] paramTypes;
    //方法参数泛型
    private Type[] paramGenericTypes;
    //请求体实例
    private Object[] paramsInstances;

    //返回值
    private Result result;
    HashMap<String, Object> stringObjectMap;


    Api getApi() {
        return api;
    }

    public Result getResult() {
        if (result == null) {
           result = Result.create();
        }
        return result;
    }

    public IceSessionContext setResultJson(String resultJson){
        setResult(GoogleGsonUtil.jsonToJavaBean(resultJson, Result.class));
        return this;
    }

    public IceSessionContext setResult(Result result){
        if (result != null) {
            this.result = result;
        }
        return this;
    }

    public Result FAIL(String message){
        return getResult().fail(message);
    }

    public Result SUCCESS(String message){
        return getResult().success(message);
    }
    public Result SUCCESS(String message,Object data){
        return getResult().success(message,data);
    }

    public Result INTERCEPT(String cause){
        return getResult().intercept(cause);
    }
    public Result INTERCEPT(int code, String cause){
        return getResult().intercept(code,cause);
    }

    public String[] getRemoteIpAndPoint() {
        try {
            if (current!=null){
                return current.con._toString().split("\\n")[1].split("=")[1].trim().split(":");
            }
        } catch (Exception ignored) { }
        return new String[]{"0.0.0.0,0"};
    }

    private void _createSession(Current __current, String packagePath, IRequest request) throws Exception {
        if (request == null) throw new IllegalArgumentException("客户端请求不正确");
        check(packagePath,request);
        initialize(__current,request);
        initHook(__current,packagePath,request);
    }

    void destroy(){
        current = null;
        param = null;
        callClass = null;
        callMethod = null;
        api = null;
        parameters = null;
        paramTypes = null;
        paramGenericTypes = null;
        paramsInstances = null;
        stringObjectMap = null;
        classObjectMap.clear();
        if (result!=null){
            result.clear();
            ObjectPoolManager.destroyObject(result);
            result = null;
        }
        ObjectPoolManager.destroyObject(this);
    }

    //检测,查询配置的包路径 - 优先 客户端指定的全路径
    private void check(String packagePath, IRequest request) throws IllegalArgumentException {

        if (StringUtil.isEmpty(request.method)) throw new IllegalArgumentException("没有指定相关服务方法");

        if (StringUtil.isEmpty(request.cls)) throw new IllegalArgumentException("没有指定相关服务类路径");

        if (StringUtil.isEmpty(request.pkg)){
            if (StringUtil.isEmpty(packagePath)) throw new IllegalArgumentException("没有指定相关服务包路径");
            request.pkg = packagePath + ".api";
        }
    }

    private void initialize(Current __current, IRequest request) throws Exception{
        current = __current;
        param = request.param;

        try {
            callClass = Class.forName(request.pkg + "."+ request.cls);
        } catch (ClassNotFoundException e) {
            throw new IllegalAccessException("接口("+request.cls+")未定义");
        }

        Method[] methods =  callClass.getDeclaredMethods();
        for (Method method : methods){
            if (method.getName().equals(request.method)
                    && method.getParameterTypes().length>=1
                    && this.getClass().isAssignableFrom( method.getParameterTypes()[0] )
            ){
                if (Modifier.isStatic(method.getModifiers())
                        || Modifier.isPrivate(method.getModifiers()))
                    continue;

                if (useAipAnnotation){
                    Api api = method.getAnnotation(Api.class);
                    if (api == null) continue;
                }

                callMethod = method;
                break;
            }
        }

        if (callMethod == null) throw new IllegalAccessException("接口("+request.cls+"#"+request.method+")未定义");
        api = callMethod.getAnnotation(Api.class);
        // 方法参数
        parameters = callMethod.getParameters();
        // 方法参数类类型
        paramTypes = callMethod.getParameterTypes();
        // 方法参数泛型
        paramGenericTypes = callMethod.getGenericParameterTypes();
        // 方法参数实例
        paramsInstances = new Object[paramTypes.length];

        if (useAipAnnotation){
            Class<?> imp = api.imp();
            if (imp != void.class) callClass = imp;
        }

    }


    /** 调用具体方法 */
    protected Object call() throws Exception{

        Object caller = ObjectPoolManager.createObject(callClass);
        Object responseValue;
        try{
            // 构建请求
            paramsInstances[0] = this;
            for (int i=1;i<paramsInstances.length;i++){
                paramsInstances[i] = convertFuncRequest(this,parameters[i],paramTypes[i], paramGenericTypes[i],getArrayParam(),getJsonParam());
            }
            // 调用前
            IceCallerObserver.onBefore(caller,callMethod);
            responseValue = ObjectRefUtil.callMethod(caller, callMethod, paramTypes, paramsInstances);
            if (responseValue == null) responseValue = getResult();
            // 调用后
            IceCallerObserver.onAfter(caller,callMethod);

        }catch (Exception e){
            // 调用异常
            IceCallerObserver.onError(caller,callMethod,e);
            if (e instanceof InvocationTargetException
                    && ((InvocationTargetException)e).getTargetException() instanceof IllegalArgumentException){

                if (getResult().getMessage() == null) {
                    getResult().fail(((InvocationTargetException)e).getTargetException().getMessage());
                }
                if (getResult().getMessage() == null){
                    getResult().fail("请求失败");
                }
                responseValue = getResult();
            } else throw  e;
        }finally {
            ObjectPoolManager.destroyObject(caller);
        }

        return responseValue;
    }



    public Class<?> getCaller() {
        return callClass;
    }

    public Method getCallMethod() {return callMethod;}


    public void putObject(Object object){
        if (object==null) return;
        classObjectMap.put(object.getClass(),object);
   }

   //获取一个对象
   @SuppressWarnings("unchecked")
   public <T> T  getObject(Class<? extends T> cls){
        Object o = classObjectMap.get(cls);
        if (o != null && o.getClass() == cls){
            return (T)o ;
        }
        return null;
   }

   public IParam getIceParam(){
        return param;
   }

    public Current getIceCurrent() {
        return current;
    }

    /** 获取 json文本 */
    public String getJsonParam() {
        if (param!=null){
            if (param.json!=null && param.json.length()>0) return param.json;
        }
        return null;
    }

   /** 获取数组参数 */
   public String[] getArrayParam(){
        if (param!=null){
            if (param.arrays!=null) return param.arrays;
        }
        return null;
   }

    /** json转对象 */
    public <T> T getJsonParamConvertObject(Class<T> cls) {
        if (param!=null){
            if (param.json!=null && param.json.length()>0) return GoogleGsonUtil.jsonToJavaBean(param.json,cls);
        }
        return null;
    }

    /** json转对象列表 */
    public <T> List<T> getJsonParamConvertList(Class<T> cls) {
        if (param!=null){
            if (param.json!=null && param.json.length()>0)
                return GoogleGsonUtil.json2List(param.json,cls);
        }
        return null;
    }

    /** 获取token */
    public String getToken(){
        if (param!=null){
            if (param.token!=null && param.token.length() > 0) return param.token;
        }
        return null;
    }

    /** 获取分页信息 前端未传递,默认前十条 */
    public int[] getPageArray(){
        if (param!=null){
            if (param.token!=null && param.token.length() > 0) return new int[]{param.pageIndex,param.pageNumber};
        }
        return new int[]{0,10};
    }

    String getCallerFullPath(){
        return callClass.getSimpleName() +"/"+callMethod.getName();
    }

    String getCallerSTR(){
        String str = callClass.getName()+callMethod.getName();
        return EncryptUtil.encryption(str);
    }

    String getParamSTR(){
        String str = param.token+param.extend+ Arrays.toString(param.arrays)+param.json+param.pageNumber+param.pageIndex+new String(param.bytes);
        return EncryptUtil.encryption(str);
    }

    public void setParam(IParam param) {
        this.param = param;
    }

    protected void initHook(Current current, String packagePath, IRequest request) {
        //初始化钩子

    }

}


//        if (useAipAnnotation && api==null) throw new IllegalAccessException("接口声明@Api未定义");
//            callMethod = callClass.getDeclaredMethod( request.method,this.getClass());
//        if (paramTypes.length<1 || !this.getClass().isAssignableFrom( paramTypes[0] )) throw new IllegalAccessException("接口定义请求错误,入参[0]需要定义"+ this.getClass().getSimpleName());
//        if (Modifier.isStatic(callMethod.getModifiers())) throw new IllegalAccessException("接口定义修饰符错误");
//        if (Modifier.isPrivate(callMethod.getModifiers())) throw new IllegalAccessException("接口定义修饰符错误");
