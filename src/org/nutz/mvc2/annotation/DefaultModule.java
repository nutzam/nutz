package org.nutz.mvc2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明了这个注解的模块的  '@Url' '@Ok' '@Fail' 注解将被所有模块共享。
 * 没有声明这三个注解的木块将使用默认模块的这三个注解
 * 
 * @author zozoh
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface DefaultModule {}
