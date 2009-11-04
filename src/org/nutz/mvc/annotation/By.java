package org.nutz.mvc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.nutz.mvc.ActionFilter;

@Retention(RetentionPolicy.RUNTIME)
public @interface By {

	Class<? extends ActionFilter> type();

	String[] args() default {};

}
