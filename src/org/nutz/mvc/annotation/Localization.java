package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.impl.NutMessageLoader;

/**
 * 指明本地化字符串加载方式。 value 属性会当做 MesssageLoader 的构造函数参数。
 * <p>
 * 因此 MessageLoader 必须有一个参数为 String 的构造函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Localization {

    /**
     * @return 加载类的类型
     */
    Class<? extends MessageLoader> type() default NutMessageLoader.class;

    /**
     * @return 传递给 Message Loader 的值
     */
    String value();

    /**
     * @return 如果有值，则从 Ioc 容器中获取 MessageLoader（需要 '@IocBy' 支持）
     * @since 1.b.45
     */
    String beanName() default "";

    /**
     * @return 整个应用默认的语言
     * @since 1.b.45
     */
    String defaultLocalizationKey() default "";
}
