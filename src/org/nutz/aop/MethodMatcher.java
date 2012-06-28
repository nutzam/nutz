package org.nutz.aop;

import java.lang.reflect.Method;

public interface MethodMatcher {

    boolean match(Method method);

}