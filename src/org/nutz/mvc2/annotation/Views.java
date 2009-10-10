package org.nutz.mvc2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.lang.Lang;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.TYPE })
public @interface Views {

	/**
	 * It built in two type "jsp" and "json" If you want to add new type, update
	 * servlet init-param "views" according what the manual said
	 */
	String type() default "jsp";

	String ok() default Lang.NULL;

	String error() default Lang.NULL;

}
