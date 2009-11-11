package org.nutz.ioc.aop;

import org.nutz.aop.MethodMatcher;
import org.nutz.aop.MethodListener;

public class ObjectMethodHooking {

	public ObjectMethodHooking(MethodMatcher matcher, MethodListener listener) {
		this.matcher = matcher;
		this.listener = listener;
	}

	private MethodMatcher matcher;
	private MethodListener listener;

	public MethodMatcher getMatcher() {
		return matcher;
	}

	public MethodListener getListener() {
		return listener;
	}

}
