package bottle.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: leeping
 * @Date: 2019/6/26 18:58
 * excel导入, 字段下标或字段名
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportFieldName {
    String name() default "";
    int index() default -1;
}