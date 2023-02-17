package framework.server;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

/**
 * 请求类型 数组 value = 数组固定长度
 * */
public @interface IReqArray {
    int value() default 0;
}
