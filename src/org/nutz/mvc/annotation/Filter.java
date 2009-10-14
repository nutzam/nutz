package org.nutz.mvc.annotation;

import org.nutz.mvc.ActionFilter;

public @interface Filter {

	Class<? extends ActionFilter> type();

	String[] args() default {};

}
