package framework.server;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

/**
 * 请求类型 Json形如{key=val} , value=键名
 * */
public @interface IReqKey {
    String value();
}
