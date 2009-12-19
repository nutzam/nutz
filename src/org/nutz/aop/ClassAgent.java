package org.nutz.aop;

import org.nutz.ioc.Ioc;

public interface ClassAgent {

	<T> Class<T> define(Class<T> klass);

	ClassAgent addListener(MethodMatcher matcher, MethodInterceptor listener);
	
	void setIoc(Ioc ioc);

	String CLASSNAME_SUFFIX = "$$NUTZAOP";
}
