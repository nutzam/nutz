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
     * 获取配置项，默认使用字段名获取 配置实例 @Value(name="server.port")
     * @return
     */
    String name() default "";
}
