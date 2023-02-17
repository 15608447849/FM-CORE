package framework.server;
import bottle.util.Log4j;
import java.lang.reflect.Method;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:21
 */
public class SessionPermissionInterceptor implements Interceptor {

    public interface CallBack{
        /* 检查请求是否合法 */
        boolean requestValidCheck(IceSessionContext context);
        void loadSessionHook(IceSessionContext context);
    }

    private static Class<? extends CallBack> callBackClassType = null;

    public static void setCallBackClassType(Class<? extends CallBack> _callBackClassType) {
        callBackClassType = _callBackClassType;
        Log4j.info("设置会话上下文加载类:" +  callBackClassType);
    }

    @Override
    public void intercept(IceSessionContext context) {
        Method m = context.getCallMethod();

        /* 创建会话上下文加载类实例 */
        CallBack callBack = null;
        if (callBackClassType!=null){
            try {
                callBack = callBackClassType.newInstance();
            } catch (Exception e) {
                Log4j.error("会话异常",e);
            }
        }
        if(callBack == null){
            context.INTERCEPT("会话创建失败");
            return;
        }

        // 是否有效请求
        boolean isIllegal = true;

        SessionPermission permission = m.getAnnotation(SessionPermission.class);

        /* 调用方法没有注解,拦截权限判断 */
        if (permission == null){
            // 通过会话上下文判断请求是否合法
            if (callBack.requestValidCheck(context)){
                context.INTERCEPT("请求拒绝或会话无效");
            }

        }else {

            /* 注解不允许匿名访问,拦截权限判断 */
            if (!permission.anonymousAccess()){
                // 通过会话上下文判断请求是否合法
                if (callBack.requestValidCheck(context)){
                    context.INTERCEPT("请求拒绝或会话无效");
                    return;
                }
            }
            /* 注解允许匿名访问,不创建会话 */
            else if (permission.anonymousCreateSession()){
                return;
            }

            /* 加载会话上下文 */
            callBack.loadSessionHook(context);
        }
    }


    @Override
    public int getPriority() {
        return 0;
    }
}
