package org.nutz.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.init.DefaultMessageLoader;

/**
 * 指明本地化字符串加载方式。 value 属性会当做 MesssageLoader 的构造函数参数。
 * <p>
 * 因此 MessageLoader 必须有一个参数为 String 的构造函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Localization {

	Class<? extends MessageLoader> type() default DefaultMessageLoader.class;

	String value();

}
