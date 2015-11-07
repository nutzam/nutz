package org.nutz.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 把对象转为Json字符串时调用的方法
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ToJson {

    /**
     * 把对象转为Json字符串时调用的方法，默认为调用该对象的『toJson』方法
     *
     * @return 对象转为Json字符串时调用的方法
     */
    String value() default "toJson";
}
