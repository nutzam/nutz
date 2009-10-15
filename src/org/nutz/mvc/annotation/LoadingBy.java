package org.nutz.mvc.annotation;

import org.nutz.mvc.Loading;

public @interface LoadingBy {

	Class<? extends Loading> value();

}
