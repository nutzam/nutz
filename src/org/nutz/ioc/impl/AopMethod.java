package org.nutz.ioc.impl;

import org.nutz.aop.MethodMatcher;
import org.nutz.aop.MethodListener;

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
