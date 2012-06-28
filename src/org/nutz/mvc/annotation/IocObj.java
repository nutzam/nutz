package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本注解可以声明在入口函数上，框架会从自身的 Ioc 容器中取得一个对象赋给相应的参数
 * <p>
 * 如果你使用了这个注解，但是没有为框架声明 Ioc 容器，那么在运行时，会抛出一个运行时异常
 * <p>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.mvc.annotation.IocBy IocBy:为系统声明 Ioc 容器
 * @see org.nutz.mvc.adaptor.injector.IocObjInjector IocObjInjector:具体的注入行为
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface IocObj {

    /**
     * 声明了容器中对象的名称，如果为空，则表示自动通过 ioc.get(MyObject.class) 方式获取对象
     * 
     * @return Ioc 容器中对象的名称
     */
    String value() default "";

}
