package org.nutz.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.param.PairHttpAdaptor;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.TYPE })
public @interface AdaptBy {

	Class<? extends HttpAdaptor> value() default PairHttpAdaptor.class;

	String[] args() default {};

}
