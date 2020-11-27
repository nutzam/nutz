package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述一个入口函数，是不是仅仅响应 PATCH 请求
 *
 * @author thomas(ywjno.dev@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface PATCH {
}
