package org.nutz.dao.tools.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 让辅助工具了解某一个 Java 字段在数据库中的是否是 NOT NULL
 * <p>
 * 如果不声明这个注解，辅助工具将不认为该字段在数据库中是 NOT NULL
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface NotNull {}
