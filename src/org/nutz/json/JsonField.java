package org.nutz.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Json字段的映射
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonField {

    String value() default "";

    /**
     * 仅仅对 Field 有效，对 Method 则无效
     * 
     * @return 是否忽略这个字段
     */
    boolean ignore() default false;

}
