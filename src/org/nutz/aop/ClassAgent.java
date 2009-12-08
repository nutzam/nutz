package org.nutz.aop;

public interface ClassAgent {

	<T> Class<T> define(Class<T> klass);

	ClassAgent addListener(MethodMatcher matcher, MethodInterceptor listener);

	String CLASSNAME_SUFFIX = "$$NUTZAOP";
}
