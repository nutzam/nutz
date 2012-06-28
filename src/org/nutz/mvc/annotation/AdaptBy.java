package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.adaptor.PairAdaptor;

/**
 * 声明适配 Http 请求的方式。通过这个注解直接声明，而没有使用类似 ViewMaker 一类的工厂模式。 是因为，我看不出有什么必要要采用工厂模式。
 * <p>
 * 这个注解第一个参数没什么好说的，第二个参数是告诉框架你打算如何创建这个适配器。你可以：
 * <ul>
 * <li>直接调用适配器的构造函数，框架会根据你给出的参数数组，自动选择一个构造函数，或者静态工厂方法
 * <li>从 Ioc 接口获得。 前提是，你必须在默认模块类中声明了 '@IocBy' 注解。并且你的参数数组的值为 {"ioc:xxx"}。
 * 就是说，参数数组长度必须为 1。 ioc 是否为大写无所谓，xxx 就是你的注入名称。
 * </ul>
 * 
 * @author zozoh
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface AdaptBy {

    Class<? extends HttpAdaptor> type() default PairAdaptor.class;

    String[] args() default {};

}
