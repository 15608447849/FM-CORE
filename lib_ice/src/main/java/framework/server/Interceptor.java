package framework.server;

/**
 * @Author: leeping
 * @Date: 2019/3/7 13:37
 * 拦截器
 */

public interface Interceptor {
    /**
     * 如果拦截返回结果,否则NULL
     */
    void intercept(IceSessionContext context);
    int getPriority();
}
