package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.ViewMaker;

/**
 * 这个注解声明了你扩展的视图渲染方式。你可以为特殊的模板引擎编写 ViewMaker 以及 View 然后将你的 ViewMaker
 * 通过这个注解声明到你的默认模块上即可。
 * 
 * @author zozoh
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Views {

    Class<? extends ViewMaker>[] value();

}
