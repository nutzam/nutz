package org.nutz.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.init.DefaultMessageLoader;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface Localization {

	Class<? extends MessageLoader> type() default DefaultMessageLoader.class;

	String value();

}
