package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.IocProvider;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * 本注解仅在主模块类上声明才有效。
 * <p>
 * 表示整个应用将采用何种方式构建 Ioc 容器。
 * 
 * @see org.nutz.mvc.IocProvider
 * @see org.nutz.ioc.Ioc
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface IocBy {

    /**
     * 通过 IocProvider，来决定采用何种方式的 Ioc 容器
     */
    Class<? extends IocProvider> type() default ComboIocProvider.class;

    /**
     * 这个参数将传递给 IocProvider 的 create 方法，作为构造 Ioc 容器必要的参数
     * <p>
     * 不同的 IocProvider 对参数数组的具体要求是不一样的，具体请参看各个 IocProvider 的说明
     */
    String[] args();

    /**
     * 声明一组 Ioc 的 Bean 名称
     * <p>
     * 如果声明了这个字段，Mvc 会在调用用户 Setup 前，首先试图依次获取对应的 Bean。<br>
     * 以便触发 Bean 的初始化逻辑
     */
    String[] init() default {};
}
