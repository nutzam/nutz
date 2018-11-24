package org.nutz.aop.matcher;

import java.lang.reflect.Method;

import org.nutz.aop.MethodMatcher;

public class SimpleMethodMatcher implements MethodMatcher {

    private Method m;

    public SimpleMethodMatcher(Method method) {
        this.m = method;
    }

    public boolean match(Method method) {
        if (m == method)
            return true;
        if (!m.getName().equals(method.getName()))
            return false;
        Class<?>[] parameterTypesMe = m.getParameterTypes();
        Class<?>[] parameterTypesOut = method.getParameterTypes();
        if (parameterTypesMe.length != parameterTypesOut.length)
            return false;
        for (int i = 0; i < parameterTypesMe.length; i++)
            if (!parameterTypesMe[i].isAssignableFrom(parameterTypesOut[i]))
                return false;
        return true;
    }

}
