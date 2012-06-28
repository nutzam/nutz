package org.nutz.ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解可以声明在模块类上，表示当前模块的注入名称。
 * <p>
 * 如果声明了本注解，那么框架在构建本模块时，会通过 Ioc 容器获取实例。
 * <p>
 * 因此，这个注解有效的前提是，主模块声明了注解 '@IocBy'
 * 
 * @see org.nutz.mvc.annotation.IocBy
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface InjectName {

    String value() default "";

}
