package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  用法
 *
 *  @ok("json")
 *  返回有值字段json
 *
 *  @ok("json:full")
 *  返回所有字段
 *
 *  @ok("json:{locked:'password|createAt|salt',ignoreNull:true}")
 *  忽略password和createAt属性,忽略空属
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Ok {

    public String value();
}
