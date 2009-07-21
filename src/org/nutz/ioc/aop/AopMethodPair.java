package org.nutz.ioc.aop;

import org.nutz.aop.MethodListener;
import org.nutz.aop.MethodMatcher;

public class AopMethodPair {

	public AopMethodPair(MethodMatcher matcher, MethodListener listener) {
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
