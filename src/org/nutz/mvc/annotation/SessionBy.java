package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.SessionProvider;

/**
 * 自定义Session提供者, 通过过滤HttpServletRequest对象来实现Session拦截
 * @author wendal
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface SessionBy {

	/**
	 * Session提供者
	 */
    Class<? extends SessionProvider> value();
    
    /**
     * Session提供者的构造方法参,如果只有一个参数且以ioc:开头,则代表引用一个ioc的bean
     */
    String[] args() default {};
}
