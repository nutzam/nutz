package org.nutz.mvc2.annotation;

public @interface Chain {

	String factory() default "";
	
	String[] args() default {};
}
