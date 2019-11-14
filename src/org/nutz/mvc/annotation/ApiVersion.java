package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义@At里面{version}的版本号
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface ApiVersion {

    /**
     * 版本号,默认是v1
     * @return
     */
    String value() default "v1";
    
    /**
     * 是否保留对应的路径参数,默认移除
     * @return
     */
    boolean keepPathArg() default false;
}
