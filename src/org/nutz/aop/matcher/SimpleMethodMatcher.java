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
		if (m.getParameterTypes().length != method.getParameterTypes().length)
			return false;
		for (int i = 0; i < m.getParameterTypes().length; i++) {
			if (!m.getParameterTypes()[i].equals(method.getParameterTypes()[i]))
				return false;
		}
		return true;
	}

}
