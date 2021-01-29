package framework.server;

import Ice.Current;
import bottle.objectref.ObjectPoolManager;
import bottle.objectref.ObjectRefUtil;
import bottle.util.EncryptUtil;
import bottle.util.GoogleGsonUtil;
import bottle.util.StringUtil;
import com.onek.server.inf.IParam;
import com.onek.server.inf.IRequest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


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

    protected IceSessionContext(){ };

    //客户端连接信息
    private Current current;
    //请求参数
    private IParam param;
    //实际调用者
    private Class<?> callClass;
    private Method callMethod;
    private Api api;
    //返回值
    private Result result;

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

    public Method getCallMethod() {
        return callMethod;
    }

    public Result FAIL(String message){
        return getResult().fail(message);
    }

    public Result SUCCESS(String message){
        return getResult().success(message);
    }

    public Result INTERCEPT(String cause){
        return getResult().intercept(cause);
    }
    public Result INTERCEPT(int code, String cause){
        return getResult().intercept(code,cause);
    }
    private final HashMap<Class<?>,Object> additionalObjectMap = new HashMap<>();

    public String[] getRemoteIpAndPoint() {
        try {
            if (current!=null){
                return current.con._toString().split("\\n")[1].split("=")[1].trim().split(":");
            }
        } catch (Exception ignored) { }
        return new String[]{"0.0.0.0,0"};
    }

    private void _createSession(Current current, String packagePath, IRequest request) throws Exception {
        if (request == null) throw new IllegalArgumentException("客户端请求不正确");
        check(packagePath,request);
        initialize(request);
        this.current = current;
        this.param = request.param;
        initHook(current,packagePath,request);
    }

    void destroy(){
        current = null;
        param = null;
        callClass = null;
        callMethod = null;
        api = null;
        additionalObjectMap.clear();
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

    private void initialize(IRequest request) throws Exception{
        String packagePath = request.pkg;
        String className = request.cls;
        String methodName = request.method;

        String classPath = packagePath + "."+ className;

        callClass = Class.forName(classPath);
        callMethod = callClass.getDeclaredMethod(methodName,this.getClass());

        api = callMethod.getAnnotation(Api.class);
        if (useAipAnnotation && api==null) throw new IllegalAccessException("未定义的API接口声明");

       if (useAipAnnotation){
           Class<?> imp = api.imp();
           if (imp != void.class) callClass = imp;
       }

    }

    /**调用具体方法*/
    protected Object call() throws Exception{

        Object caller = ObjectPoolManager.createObject(callClass);
        Object value;
        try{
            value = ObjectRefUtil.callMethod(caller, callMethod, new Class[]{this.getClass()},this);
            if (value == null) value = getResult();
        }finally {
            ObjectPoolManager.destroyObject(caller);
        }
        return value;
    }

    public void putObject(Object object){
        if (object==null) return;
        additionalObjectMap.put(object.getClass(),object);
   }

   //获取一个对象
   @SuppressWarnings("unchecked")
   public <T> T  getObject(Class<? extends T> cls){
        Object o = additionalObjectMap.get(cls);
        if (o != null && o.getClass() == cls){
            return (T)o ;
        }
        return null;
   }

   public IParam getIceParam(){
        return param;
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

    protected void initHook(Current current, String packagePath, IRequest request) {
        //初始化钩子

    }

}
