package org.nutz.mvc.annotation;

import org.nutz.mvc.Loading;
import org.nutz.mvc.init.DefaultLoading;

/**
 * 在主模块上声明加载逻辑加载逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public @interface LoadingBy {

	Class<? extends Loading> value() default DefaultLoading.class;

}
