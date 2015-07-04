package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.nutz.mvc.ActionFilter;

/**
 * 声明一个过滤器。 属于前置过滤, 如果需要链式过滤,请参考"动作链"文档
 * <p>
 * 第二个属性可以是构造参数的值。但是如果数组长度为一，并且以 "ioc:"开头， 则该过滤器将通过 Ioc 容器获取
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface By {

    /**
     * 过滤器类型
     * @return 过滤器类型
     */
    Class<? extends ActionFilter> type();

    /**
     * 过滤器的构造参数,如果需要从ioc获取过滤器实例,则仅填一个参数,格式为 "ioc:xxxxFilter", 其中xxxxFilter的IocBean命名
     * @return 参数列表
     */
    String[] args() default {};

}
