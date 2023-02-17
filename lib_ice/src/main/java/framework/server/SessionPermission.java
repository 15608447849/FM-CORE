package framework.server;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionPermission {
    boolean anonymousAccess () default false; //允许匿名访问,默认不允许(false)
    boolean anonymousCreateSession() default false;//匿名访问下,始终尝试创建会话
}
