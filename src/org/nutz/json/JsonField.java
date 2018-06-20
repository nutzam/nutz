package org.nutz.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Json字段的映射
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author 有心猴(belialofking@163.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface JsonField {

    String value() default "";

    /**
     * 仅仅对 Field 有效，对 Method 则无效
     * 
     * @return 是否忽略这个字段
     */
    boolean ignore() default false;

    /**
     * 有些对象类型总是被当做字符串输出会有更好的效果，比如 Region<br>
     * 当然这些对象是可以被 Castors 从字符串正确的转换回来的。即它们有一个带一个字符串为参数的构造函数即可
     * <p>
     * 这个声明，也将作用于数组，和集合
     * 
     * @return 这个字段是否被强制输出成字符串
     * @see org.nutz.lang.util.Region
     */
    boolean forceString() default false;
    
    @Deprecated
    String dateFormat() default "";
    
    String dataFormat() default "";
    
    String timeZone() default "";
    
    String locale() default "";
}
