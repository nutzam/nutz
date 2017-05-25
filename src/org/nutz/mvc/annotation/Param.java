package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以声明在 POJO 字段上，或者 入口函数的参数上。 描述，应该对应到 HTTP 请求哪一个参数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Documented
public @interface Param {

    /**
     * 对应到 HTTP 参数里的参数名称
     */
    String value();

    /**
     * 如果是日期对象，这个参数可以声明其特殊的格式，如果不声明，则用 Times 函数来转换
     */
    String dfmt() default "";

    String df() default "//NOT EXIST IN//";
    
    String locale() default "";

    /**
     * 在输入参数是一个字符串，而入口函数参数是一个数组的时候：
     * <ul>
     * <li>true - 会自动将字符串拆分成数组
     * <li>false - 不会拆分，而只会生成一个元素的数组
     * </ul>
     */
    boolean array_auto_split() default true;
}
