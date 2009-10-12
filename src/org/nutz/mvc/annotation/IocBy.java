package org.nutz.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.IocProvider;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface IocBy {

	Class<? extends IocProvider> provider();

	String[] args();

}
