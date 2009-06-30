package org.nutz.aop.javassist.lstn;

import java.lang.reflect.Method;

import org.nutz.aop.MethodListener;

public class MethodCounter implements MethodListener {

	private int[] cc;

	public MethodCounter(int[] cc) {
		this.cc = cc;
	}

	@Override
	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		cc[1] += 1;
		return returnObj;
	}

	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		cc[0] += 1;
		return true;
	}

	@Override
	public void whenError(Throwable e, Object obj, Method method, Object... args) {
		cc[2] += 1;
	}

	@Override
	public void whenException(Exception e, Object obj, Method method, Object... args) {
		cc[3] += 1;
	}

}
