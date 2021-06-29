package org.nutz.ioc.loader.annotation;

import java.lang.annotation.*;

/**
 * @author wentao
 * @title
 * @description
 * @create 2021-03-09 2:46 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    /**
     * 获取配置项，默认使用字段名获取 配置实例 @Value(name="server.port")  如不配置name，则使用字段名首字母小写作为key获取配置
     * @return
     */
    String name() default "";

    /**
     * 配置项默认值
     * @return
     */
    String defaultValue() default "";
}
