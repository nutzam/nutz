package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.nutz.mvc.ActionFilter;

/**
 * 声明一个过滤器。
 * <p>
 * 第二个属性可以是构造参数的值。但是如果数组长度为一，并且以 "ioc:"开头， 则该过滤器将通过 Ioc 容器获取
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface By {

    Class<? extends ActionFilter> type();

    String[] args() default {};

}
