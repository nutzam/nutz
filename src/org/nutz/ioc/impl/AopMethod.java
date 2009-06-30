package org.nutz.ioc.impl;

import org.nutz.aop.MethodListener;
import org.nutz.aop.MethodMatcher;

public class AopMethod {

	private MethodMatcher methods;

	private MethodListener[] listeners;

	public MethodMatcher getMethods() {
		return methods;
	}

	public void setMethods(MethodMatcher methods) {
		this.methods = methods;
	}

	public MethodListener[] getListeners() {
		return listeners;
	}

	public void setListeners(MethodListener[] listeners) {
		this.listeners = listeners;
	}

}
